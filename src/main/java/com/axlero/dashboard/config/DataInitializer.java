package com.axlero.dashboard.config;

import com.axlero.dashboard.entities.*;
import com.axlero.dashboard.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDatabase(UserRepository userRepository,
                                          WorkspaceRepository workspaceRepository,
                                          DashboardRepository dashboardRepository,
                                          WidgetConfigRepository widgetRepository,
                                          DatasetRecordRepository datasetRepository,
                                          PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() > 0) {
                return; // Already initialized
            }

            // 1. Seed Users
            User admin = new User("admin", "admin@axlero.com", passwordEncoder.encode("password123"), Role.ROLE_ADMIN);
            User user = new User("demouser", "demo@axlero.com", passwordEncoder.encode("password123"), Role.ROLE_USER);
            userRepository.saveAll(List.of(admin, user));

            // 2. Seed Workspace
            Workspace workspace = new Workspace(
                    "Axlero Global Analytics",
                    "Primary enterprise workspace for corporate business intelligence",
                    user
            );
            workspaceRepository.save(workspace);

            // 3. Seed Dashboard
            Dashboard dashboard = new Dashboard(
                    "Executive Revenue & Regional Sales",
                    "Real-time monitoring of corporate revenue, regional distribution, and product metrics",
                    workspace
            );
            dashboardRepository.save(dashboard);

            // 4. Seed Widgets
            WidgetConfig widget1 = new WidgetConfig(
                    "Revenue by Region",
                    ChartType.BAR,
                    MetricType.SUM,
                    "region",
                    "numericValue",
                    "sales",
                    null,
                    30,
                    1,
                    dashboard
            );

            WidgetConfig widget2 = new WidgetConfig(
                    "Sales by Product Category",
                    ChartType.PIE,
                    MetricType.SUM,
                    "category",
                    "numericValue",
                    "sales",
                    null,
                    30,
                    2,
                    dashboard
            );

            WidgetConfig widget3 = new WidgetConfig(
                    "Transaction Count by Status",
                    ChartType.LINE,
                    MetricType.COUNT,
                    "status",
                    "numericValue",
                    "sales",
                    null,
                    60,
                    3,
                    dashboard
            );

            WidgetConfig widget4 = new WidgetConfig(
                    "Total Global Volume",
                    ChartType.KPI,
                    MetricType.SUM,
                    "status",
                    "quantity",
                    "sales",
                    null,
                    15,
                    4,
                    dashboard
            );

            widgetRepository.saveAll(List.of(widget1, widget2, widget3, widget4));

            // 5. Seed Dataset Records (Realistic transactional sales data)
            List<DatasetRecord> records = List.of(
                    new DatasetRecord(workspace, "sales", "Electronics", "North America", "COMPLETED", new BigDecimal("14500.50"), 120, LocalDate.now().minusDays(1)),
                    new DatasetRecord(workspace, "sales", "Software", "North America", "COMPLETED", new BigDecimal("32000.00"), 45, LocalDate.now().minusDays(2)),
                    new DatasetRecord(workspace, "sales", "Furniture", "North America", "PENDING", new BigDecimal("8200.00"), 30, LocalDate.now().minusDays(3)),
                    new DatasetRecord(workspace, "sales", "Electronics", "Europe", "COMPLETED", new BigDecimal("21500.00"), 180, LocalDate.now().minusDays(1)),
                    new DatasetRecord(workspace, "sales", "Software", "Europe", "COMPLETED", new BigDecimal("19800.75"), 32, LocalDate.now().minusDays(2)),
                    new DatasetRecord(workspace, "sales", "Furniture", "Europe", "COMPLETED", new BigDecimal("11200.00"), 40, LocalDate.now().minusDays(4)),
                    new DatasetRecord(workspace, "sales", "Electronics", "Asia-Pacific", "COMPLETED", new BigDecimal("38900.00"), 310, LocalDate.now().minusDays(1)),
                    new DatasetRecord(workspace, "sales", "Software", "Asia-Pacific", "COMPLETED", new BigDecimal("25400.00"), 50, LocalDate.now().minusDays(2)),
                    new DatasetRecord(workspace, "sales", "Furniture", "Asia-Pacific", "PENDING", new BigDecimal("7400.00"), 25, LocalDate.now().minusDays(3)),
                    new DatasetRecord(workspace, "sales", "Electronics", "Latin America", "COMPLETED", new BigDecimal("9800.00"), 85, LocalDate.now().minusDays(5)),
                    new DatasetRecord(workspace, "sales", "Software", "Latin America", "CANCELLED", new BigDecimal("4500.00"), 8, LocalDate.now().minusDays(2)),
                    new DatasetRecord(workspace, "sales", "Furniture", "Latin America", "COMPLETED", new BigDecimal("6300.00"), 22, LocalDate.now().minusDays(4))
            );

            datasetRepository.saveAll(records);
            System.out.println(">>> Demo data seeded successfully: User 'demouser' / 'password123', Workspace 'Axlero Global Analytics'");
        };
    }
}
