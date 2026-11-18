package com.axlero.dashboard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class WorkspaceDtos {

    public record WorkspaceRequest(
            @NotBlank(message = "Workspace name is required")
            @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
            String name,

            String description
    ) {}

    public record WorkspaceResponse(
            Long id,
            String name,
            String description,
            String ownerUsername,
            int dashboardCount,
            LocalDateTime createdAt
    ) {}
}
