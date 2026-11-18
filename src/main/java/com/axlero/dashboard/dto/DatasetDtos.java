package com.axlero.dashboard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class DatasetDtos {

    public record RecordCreateRequest(
            @NotNull(message = "Workspace ID is required")
            Long workspaceId,

            String datasetName,

            @NotBlank(message = "Category is required")
            String category,

            @NotBlank(message = "Region is required")
            String region,

            @NotBlank(message = "Status is required")
            String status,

            @NotNull(message = "Numeric value is required")
            BigDecimal numericValue,

            Integer quantity,

            LocalDate recordDate
    ) {}

    public record DatasetSummary(
            String datasetName,
            long recordCount
    ) {}
}
