package com.axlero.dashboard.repositories;

import com.axlero.dashboard.entities.Dashboard;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DashboardRepository extends JpaRepository<Dashboard, Long> {

    List<Dashboard> findByWorkspaceId(Long workspaceId);

    // Optimized with JOIN FETCH to prevent the N+1 query problem
    @Query("SELECT d FROM Dashboard d LEFT JOIN FETCH d.widgets WHERE d.id = :id")
    Optional<Dashboard> findByIdWithWidgets(@Param("id") Long id);

    @Query("SELECT d FROM Dashboard d LEFT JOIN FETCH d.widgets WHERE d.workspace.id = :workspaceId")
    List<Dashboard> findByWorkspaceIdWithWidgets(@Param("workspaceId") Long workspaceId);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM Dashboard d " +
           "WHERE d.id = :dashboardId AND d.workspace.owner.username = :username")
    boolean isOwnedByUser(@Param("dashboardId") Long dashboardId, @Param("username") String username);
}
