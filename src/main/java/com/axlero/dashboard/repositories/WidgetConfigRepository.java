package com.axlero.dashboard.repositories;

import com.axlero.dashboard.entities.WidgetConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WidgetConfigRepository extends JpaRepository<WidgetConfig, Long> {
    List<WidgetConfig> findByDashboardIdOrderByPositionOrderAsc(Long dashboardId);
}
