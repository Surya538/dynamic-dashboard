package com.axlero.dashboard.services;

import com.axlero.dashboard.dto.WidgetDtos.WidgetConfigRequest;
import com.axlero.dashboard.dto.WidgetDtos.WidgetConfigResponse;
import com.axlero.dashboard.entities.Dashboard;
import com.axlero.dashboard.entities.WidgetConfig;
import com.axlero.dashboard.exceptions.ResourceNotFoundException;
import com.axlero.dashboard.repositories.WidgetConfigRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WidgetService {

    private final WidgetConfigRepository widgetRepository;
    private final DashboardService dashboardService;
    private final WorkspaceService workspaceService;

    public WidgetService(WidgetConfigRepository widgetRepository,
                         DashboardService dashboardService,
                         WorkspaceService workspaceService) {
        this.widgetRepository = widgetRepository;
        this.dashboardService = dashboardService;
        this.workspaceService = workspaceService;
    }

    @Transactional(readOnly = true)
    public List<WidgetConfigResponse> getWidgetsByDashboard(Long dashboardId, String username) {
        Dashboard dashboard = dashboardService.getEntityById(dashboardId);
        workspaceService.validateOwnership(dashboard.getWorkspace(), username);

        return widgetRepository.findByDashboardIdOrderByPositionOrderAsc(dashboardId).stream()
                .map(dashboardService::toWidgetResponse)
                .toList();
    }

    @Transactional
    @CacheEvict(value = "dashboardCache", key = "#request.dashboardId()")
    public WidgetConfigResponse createWidget(WidgetConfigRequest request, String username) {
        Dashboard dashboard = dashboardService.getEntityById(request.dashboardId());
        workspaceService.validateOwnership(dashboard.getWorkspace(), username);

        WidgetConfig widget = new WidgetConfig(
                request.title(),
                request.chartType(),
                request.metricType(),
                request.dimensionField(),
                request.metricField(),
                request.datasetName(),
                request.filterConditions(),
                request.refreshInterval(),
                request.positionOrder(),
                dashboard
        );

        WidgetConfig saved = widgetRepository.save(widget);
        return dashboardService.toWidgetResponse(saved);
    }

    @Transactional
    @CacheEvict(value = {"dashboardCache", "metricsCache"}, allEntries = true)
    public WidgetConfigResponse updateWidget(Long id, WidgetConfigRequest request, String username) {
        WidgetConfig widget = widgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Widget id " + id + " not found"));

        workspaceService.validateOwnership(widget.getDashboard().getWorkspace(), username);

        widget.setTitle(request.title());
        widget.setChartType(request.chartType());
        widget.setMetricType(request.metricType());
        widget.setDimensionField(request.dimensionField());
        widget.setMetricField(request.metricField());
        widget.setDatasetName(request.datasetName());
        widget.setFilterConditions(request.filterConditions());
        widget.setRefreshInterval(request.refreshInterval());
        widget.setPositionOrder(request.positionOrder());

        return dashboardService.toWidgetResponse(widget);
    }

    @Transactional
    @CacheEvict(value = {"dashboardCache", "metricsCache"}, allEntries = true)
    public void deleteWidget(Long id, String username) {
        WidgetConfig widget = widgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Widget id " + id + " not found"));

        workspaceService.validateOwnership(widget.getDashboard().getWorkspace(), username);
        widgetRepository.delete(widget);
    }
}
