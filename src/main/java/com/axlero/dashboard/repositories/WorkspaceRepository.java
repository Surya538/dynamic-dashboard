package com.axlero.dashboard.repositories;

import com.axlero.dashboard.entities.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
    List<Workspace> findByOwnerUsername(String username);
    Optional<Workspace> findByIdAndOwnerUsername(Long id, String username);
    boolean existsByIdAndOwnerUsername(Long id, String username);
}
