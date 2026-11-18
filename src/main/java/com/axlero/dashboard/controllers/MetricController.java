package com.axlero.dashboard.controllers;

import com.axlero.dashboard.dto.MetricDtos.MetricQueryRequest;
import com.axlero.dashboard.dto.MetricDtos.MetricQueryResult;
import com.axlero.dashboard.entities.ChartType;
import com.axlero.dashboard.entities.MetricType;
import com.axlero.dashboard.services.MetricService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/api/metrics")
@Tag(name = "Metrics & Dynamic Query Engine", description = "Dynamic aggregation engine powered by JPA Criteria API")
public class MetricController {

    private final MetricService metricService;

    public MetricController(MetricService metricService) {
        this.metricService = metricService;
    }

    @PostMapping("/aggregate")
    @Operation(summary = "Execute dynamic GROUP BY and aggregation query (SUM, COUNT, AVG, MIN, MAX)")
    public ResponseEntity<MetricQueryResult> executeAggregation(@Valid @RequestBody MetricQueryRequest request,
                                                                @AuthenticationPrincipal UserDetails userDetails) {
        MetricQueryResult result = metricService.executeAggregation(request, userDetails.getUsername());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/schema")
    @Operation(summary = "Get available dimensions, metrics, chart types, and aggregation functions")
    public ResponseEntity<Map<String, Object>> getQuerySchema() {
        return ResponseEntity.ok(Map.of(
                "allowedDimensions", MetricService.getAllowedDimensions(),
                "allowedMetrics", MetricService.getAllowedMetrics(),
                "chartTypes", Arrays.stream(ChartType.values()).map(Enum::name).toList(),
                "aggregationTypes", Arrays.stream(MetricType.values()).map(Enum::name).toList()
        ));
    }
}
