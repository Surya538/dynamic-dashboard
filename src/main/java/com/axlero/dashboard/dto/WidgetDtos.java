package com.axlero.dashboard.dto;

import com.axlero.dashboard.entities.ChartType;
import com.axlero.dashboard.entities.MetricType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class WidgetDtos {

    public record WidgetConfigRequest(
            @NotBlank(message = "Widget title is required")
            String title,

            @NotNull(message = "Chart type is required")
            ChartType chartType,

            @NotNull(message = "Metric type is required")
            MetricType metricType,

            @NotBlank(message = "Dimension field is required (e.g. region, category, status)")
            String dimensionField,

            @NotBlank(message = "Metric field is required (e.g. numericValue, quantity)")
            String metricField,

            String datasetName,
            String filterConditions,
            Integer refreshInterval,
            Integer positionOrder,

            @NotNull(message = "Dashboard ID is required")
            Long dashboardId
    ) {}

    public record WidgetConfigResponse(
            Long id,
            String title,
            ChartType chartType,
            MetricType metricType,
            String dimensionField,
            String metricField,
            String datasetName,
            String filterConditions,
            Integer refreshInterval,
            Integer positionOrder,
            Long dashboardId,
            LocalDateTime createdAt
    ) {}
}
