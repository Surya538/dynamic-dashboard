package com.axlero.dashboard.controllers;

import com.axlero.dashboard.dto.WidgetDtos.WidgetConfigRequest;
import com.axlero.dashboard.dto.WidgetDtos.WidgetConfigResponse;
import com.axlero.dashboard.services.WidgetService;
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
@RequestMapping("/api/widgets")
@Tag(name = "Widgets", description = "Widget configuration and dynamic chart settings")
public class WidgetController {

    private final WidgetService widgetService;

    public WidgetController(WidgetService widgetService) {
        this.widgetService = widgetService;
    }

    @GetMapping("/dashboard/{dashboardId}")
    @Operation(summary = "Get all widgets belonging to a dashboard")
    public ResponseEntity<List<WidgetConfigResponse>> getWidgetsByDashboard(@PathVariable Long dashboardId,
                                                                            @AuthenticationPrincipal UserDetails userDetails) {
        List<WidgetConfigResponse> widgets = widgetService.getWidgetsByDashboard(dashboardId, userDetails.getUsername());
        return ResponseEntity.ok(widgets);
    }

    @PostMapping
    @Operation(summary = "Create a new widget configuration")
    public ResponseEntity<WidgetConfigResponse> createWidget(@Valid @RequestBody WidgetConfigRequest request,
                                                             @AuthenticationPrincipal UserDetails userDetails) {
        WidgetConfigResponse created = widgetService.createWidget(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update widget configuration")
    public ResponseEntity<WidgetConfigResponse> updateWidget(@PathVariable Long id,
                                                             @Valid @RequestBody WidgetConfigRequest request,
                                                             @AuthenticationPrincipal UserDetails userDetails) {
        WidgetConfigResponse updated = widgetService.updateWidget(id, request, userDetails.getUsername());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a widget")
    public ResponseEntity<Void> deleteWidget(@PathVariable Long id,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        widgetService.deleteWidget(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
