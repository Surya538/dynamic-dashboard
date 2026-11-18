package com.axlero.dashboard.controllers;

import com.axlero.dashboard.dto.WorkspaceDtos.WorkspaceRequest;
import com.axlero.dashboard.dto.WorkspaceDtos.WorkspaceResponse;
import com.axlero.dashboard.services.WorkspaceService;
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
@RequestMapping("/api/workspaces")
@Tag(name = "Workspaces", description = "Tenant and workspace management with RBAC isolation")
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @GetMapping
    @Operation(summary = "Get all workspaces owned by the authenticated user")
    public ResponseEntity<List<WorkspaceResponse>> getMyWorkspaces(@AuthenticationPrincipal UserDetails userDetails) {
        List<WorkspaceResponse> workspaces = workspaceService.getUserWorkspaces(userDetails.getUsername());
        return ResponseEntity.ok(workspaces);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get workspace details by ID")
    public ResponseEntity<WorkspaceResponse> getWorkspaceById(@PathVariable Long id,
                                                              @AuthenticationPrincipal UserDetails userDetails) {
        WorkspaceResponse workspace = workspaceService.getWorkspaceById(id, userDetails.getUsername());
        return ResponseEntity.ok(workspace);
    }

    @PostMapping
    @Operation(summary = "Create a new workspace")
    public ResponseEntity<WorkspaceResponse> createWorkspace(@Valid @RequestBody WorkspaceRequest request,
                                                             @AuthenticationPrincipal UserDetails userDetails) {
        WorkspaceResponse created = workspaceService.createWorkspace(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update workspace details")
    public ResponseEntity<WorkspaceResponse> updateWorkspace(@PathVariable Long id,
                                                             @Valid @RequestBody WorkspaceRequest request,
                                                             @AuthenticationPrincipal UserDetails userDetails) {
        WorkspaceResponse updated = workspaceService.updateWorkspace(id, request, userDetails.getUsername());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a workspace")
    public ResponseEntity<Void> deleteWorkspace(@PathVariable Long id,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        workspaceService.deleteWorkspace(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
