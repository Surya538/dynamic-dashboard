package com.axlero.dashboard.services;

import com.axlero.dashboard.dto.DashboardDtos.DashboardRequest;
import com.axlero.dashboard.dto.DashboardDtos.DashboardResponse;
import com.axlero.dashboard.dto.WidgetDtos.WidgetConfigResponse;
import com.axlero.dashboard.entities.Dashboard;
import com.axlero.dashboard.entities.WidgetConfig;
import com.axlero.dashboard.entities.Workspace;
import com.axlero.dashboard.exceptions.ResourceNotFoundException;
import com.axlero.dashboard.exceptions.UnauthorizedAccessException;
import com.axlero.dashboard.repositories.DashboardRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class DashboardService {

    private final DashboardRepository dashboardRepository;
    private final WorkspaceService workspaceService;

    public DashboardService(DashboardRepository dashboardRepository, WorkspaceService workspaceService) {
        this.dashboardRepository = dashboardRepository;
        this.workspaceService = workspaceService;
    }

    @Transactional(readOnly = true)
    public List<DashboardResponse> getDashboardsByWorkspace(Long workspaceId, String username) {
        Workspace workspace = workspaceService.getEntityById(workspaceId);
        workspaceService.validateOwnership(workspace, username);

        // Prevents N+1 query problem by batch-fetching widgets in a single optimized query
        return dashboardRepository.findByWorkspaceIdWithWidgets(workspaceId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "dashboardCache", key = "#id")
    public DashboardResponse getDashboardById(Long id, String username) {
        // Solves N+1 query problem using JOIN FETCH
        Dashboard dashboard = dashboardRepository.findByIdWithWidgets(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dashboard with id " + id + " not found"));

        workspaceService.validateOwnership(dashboard.getWorkspace(), username);
        return toResponse(dashboard);
    }

    @Transactional
    public DashboardResponse createDashboard(DashboardRequest request, String username) {
        Workspace workspace = workspaceService.getEntityById(request.workspaceId());
        workspaceService.validateOwnership(workspace, username);

        Dashboard dashboard = new Dashboard(request.title(), request.description(), workspace);
        Dashboard saved = dashboardRepository.save(dashboard);
        return toResponse(saved);
    }

    @Transactional
    @CacheEvict(value = "dashboardCache", key = "#id")
    public DashboardResponse updateDashboard(Long id, DashboardRequest request, String username) {
        Dashboard dashboard = dashboardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dashboard with id " + id + " not found"));

        workspaceService.validateOwnership(dashboard.getWorkspace(), username);
        dashboard.setTitle(request.title());
        dashboard.setDescription(request.description());
        return toResponse(dashboard);
    }

    @Transactional
    @CacheEvict(value = "dashboardCache", key = "#id")
    public void deleteDashboard(Long id, String username) {
        Dashboard dashboard = dashboardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dashboard with id " + id + " not found"));

        workspaceService.validateOwnership(dashboard.getWorkspace(), username);
        dashboardRepository.delete(dashboard);
    }

    public Dashboard getEntityById(Long id) {
        return dashboardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dashboard with id " + id + " not found"));
    }

    public DashboardResponse toResponse(Dashboard d) {
        List<WidgetConfigResponse> widgetDtos = d.getWidgets() != null
                ? d.getWidgets().stream().map(this::toWidgetResponse).toList()
                : Collections.emptyList();

        return new DashboardResponse(
                d.getId(),
                d.getTitle(),
                d.getDescription(),
                d.getWorkspace().getId(),
                d.getWorkspace().getName(),
                widgetDtos,
                d.getCreatedAt(),
                d.getUpdatedAt()
        );
    }

    public WidgetConfigResponse toWidgetResponse(WidgetConfig w) {
        return new WidgetConfigResponse(
                w.getId(),
                w.getTitle(),
                w.getChartType(),
                w.getMetricType(),
                w.getDimensionField(),
                w.getMetricField(),
                w.getDatasetName(),
                w.getFilterConditions(),
                w.getRefreshInterval(),
                w.getPositionOrder(),
                w.getDashboard().getId(),
                w.getCreatedAt()
        );
    }
}
