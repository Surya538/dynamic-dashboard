package com.axlero.dashboard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public class DashboardDtos {

    public record DashboardRequest(
            @NotBlank(message = "Dashboard title is required")
            String title,

            String description,

            @NotNull(message = "Workspace ID is required")
            Long workspaceId
    ) {}

    public record DashboardResponse(
            Long id,
            String title,
            String description,
            Long workspaceId,
            String workspaceName,
            List<WidgetDtos.WidgetConfigResponse> widgets,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}
}
