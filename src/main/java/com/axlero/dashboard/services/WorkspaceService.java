package com.axlero.dashboard.services;

import com.axlero.dashboard.dto.WorkspaceDtos.WorkspaceRequest;
import com.axlero.dashboard.dto.WorkspaceDtos.WorkspaceResponse;
import com.axlero.dashboard.entities.User;
import com.axlero.dashboard.entities.Workspace;
import com.axlero.dashboard.exceptions.ResourceNotFoundException;
import com.axlero.dashboard.exceptions.UnauthorizedAccessException;
import com.axlero.dashboard.repositories.UserRepository;
import com.axlero.dashboard.repositories.WorkspaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;

    public WorkspaceService(WorkspaceRepository workspaceRepository, UserRepository userRepository) {
        this.workspaceRepository = workspaceRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<WorkspaceResponse> getUserWorkspaces(String username) {
        return workspaceRepository.findByOwnerUsername(username).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public WorkspaceResponse getWorkspaceById(Long id, String username) {
        Workspace workspace = workspaceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace with id " + id + " not found"));

        validateOwnership(workspace, username);
        return toResponse(workspace);
    }

    @Transactional
    public WorkspaceResponse createWorkspace(WorkspaceRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User '" + username + "' not found"));

        Workspace workspace = new Workspace(request.name(), request.description(), user);
        Workspace saved = workspaceRepository.save(workspace);
        return toResponse(saved);
    }

    @Transactional
    public WorkspaceResponse updateWorkspace(Long id, WorkspaceRequest request, String username) {
        Workspace workspace = workspaceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace with id " + id + " not found"));

        validateOwnership(workspace, username);
        workspace.setName(request.name());
        workspace.setDescription(request.description());
        return toResponse(workspace);
    }

    @Transactional
    public void deleteWorkspace(Long id, String username) {
        Workspace workspace = workspaceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace with id " + id + " not found"));

        validateOwnership(workspace, username);
        workspaceRepository.delete(workspace);
    }

    public void validateOwnership(Workspace workspace, String username) {
        if (!workspace.getOwner().getUsername().equals(username)) {
            throw new UnauthorizedAccessException("You do not have access to workspace id: " + workspace.getId());
        }
    }

    public Workspace getEntityById(Long id) {
        return workspaceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace id " + id + " not found"));
    }

    private WorkspaceResponse toResponse(Workspace w) {
        return new WorkspaceResponse(
                w.getId(),
                w.getName(),
                w.getDescription(),
                w.getOwner().getUsername(),
                w.getDashboards() != null ? w.getDashboards().size() : 0,
                w.getCreatedAt()
        );
    }
}
