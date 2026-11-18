package com.axlero.dashboard.controllers;

import com.axlero.dashboard.dto.DashboardDtos.DashboardRequest;
import com.axlero.dashboard.dto.DashboardDtos.DashboardResponse;
import com.axlero.dashboard.services.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboards")
@Tag(name = "Dashboards", description = "Dashboard management with widget configurations")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/workspace/{workspaceId}")
    @Operation(summary = "Get all dashboards for a specific workspace")
    public ResponseEntity<List<DashboardResponse>> getDashboardsByWorkspace(@PathVariable Long workspaceId,
                                                                            @AuthenticationPrincipal UserDetails userDetails) {
        List<DashboardResponse> dashboards = dashboardService.getDashboardsByWorkspace(workspaceId, userDetails.getUsername());
        return ResponseEntity.ok(dashboards);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get dashboard by ID (with widget configurations)")
    public ResponseEntity<DashboardResponse> getDashboardById(@PathVariable Long id,
                                                              @AuthenticationPrincipal UserDetails userDetails) {
        DashboardResponse dashboard = dashboardService.getDashboardById(id, userDetails.getUsername());
        return ResponseEntity.ok(dashboard);
    }

    @PostMapping
    @Operation(summary = "Create a new dashboard")
    public ResponseEntity<DashboardResponse> createDashboard(@Valid @RequestBody DashboardRequest request,
                                                             @AuthenticationPrincipal UserDetails userDetails) {
        DashboardResponse created = dashboardService.createDashboard(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update dashboard title and description")
    public ResponseEntity<DashboardResponse> updateDashboard(@PathVariable Long id,
                                                             @Valid @RequestBody DashboardRequest request,
                                                             @AuthenticationPrincipal UserDetails userDetails) {
        DashboardResponse updated = dashboardService.updateDashboard(id, request, userDetails.getUsername());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a dashboard")
    public ResponseEntity<Void> deleteDashboard(@PathVariable Long id,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        dashboardService.deleteDashboard(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
