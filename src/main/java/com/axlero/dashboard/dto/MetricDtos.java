package com.axlero.dashboard.dto;

import com.axlero.dashboard.entities.MetricType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class MetricDtos {

    public record MetricQueryRequest(
            @NotNull(message = "Workspace ID is required")
            Long workspaceId,

            String datasetName,

            @NotBlank(message = "Dimension field is required (e.g., category, region, status)")
            String dimensionField,

            @NotBlank(message = "Metric field is required (e.g., numericValue, quantity)")
            String metricField,

            @NotNull(message = "Aggregation function is required (SUM, COUNT, AVG, MIN, MAX)")
            MetricType aggregation,

            // Dynamic safe filters, e.g. {"region": "Europe", "status": "COMPLETED"}
            Map<String, String> filters,

            // Limit results (defaults to 50)
            Integer limit
    ) implements Serializable {}

    public record MetricBucket(
            String dimensionValue,
            BigDecimal metricValue,
            Long recordCount
    ) implements Serializable {}

    public record MetricQueryResult(
            String dimensionField,
            String metricField,
            MetricType aggregation,
            List<MetricBucket> buckets,
            BigDecimal overallTotal,
            long executionTimeMs
    ) implements Serializable {}
}
