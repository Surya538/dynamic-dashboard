package com.axlero.dashboard.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "widget_configs")
public class WidgetConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChartType chartType = ChartType.BAR;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MetricType metricType = MetricType.SUM;

    @Column(name = "dimension_field", nullable = false, length = 60)
    private String dimensionField; // e.g. "region", "category", "status"

    @Column(name = "metric_field", nullable = false, length = 60)
    private String metricField; // e.g. "amount", "revenue", "quantity"

    @Column(name = "dataset_name", length = 100)
    private String datasetName = "sales";

    @Column(name = "filter_conditions", length = 1000)
    private String filterConditions; // optional JSON string of filter rules

    @Column(name = "refresh_interval")
    private Integer refreshInterval = 30; // seconds

    @Column(name = "position_order")
    private Integer positionOrder = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dashboard_id", nullable = false)
    private Dashboard dashboard;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public WidgetConfig() {}

    public WidgetConfig(String title, ChartType chartType, MetricType metricType,
                        String dimensionField, String metricField, String datasetName,
                        String filterConditions, Integer refreshInterval, Integer positionOrder,
                        Dashboard dashboard) {
        this.title = title;
        this.chartType = chartType;
        this.metricType = metricType;
        this.dimensionField = dimensionField;
        this.metricField = metricField;
        this.datasetName = datasetName != null ? datasetName : "sales";
        this.filterConditions = filterConditions;
        this.refreshInterval = refreshInterval != null ? refreshInterval : 30;
        this.positionOrder = positionOrder != null ? positionOrder : 0;
        this.dashboard = dashboard;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ChartType getChartType() {
        return chartType;
    }

    public void setChartType(ChartType chartType) {
        this.chartType = chartType;
    }

    public MetricType getMetricType() {
        return metricType;
    }

    public void setMetricType(MetricType metricType) {
        this.metricType = metricType;
    }

    public String getDimensionField() {
        return dimensionField;
    }

    public void setDimensionField(String dimensionField) {
        this.dimensionField = dimensionField;
    }

    public String getMetricField() {
        return metricField;
    }

    public void setMetricField(String metricField) {
        this.metricField = metricField;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

    public String getFilterConditions() {
        return filterConditions;
    }

    public void setFilterConditions(String filterConditions) {
        this.filterConditions = filterConditions;
    }

    public Integer getRefreshInterval() {
        return refreshInterval;
    }

    public void setRefreshInterval(Integer refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    public Integer getPositionOrder() {
        return positionOrder;
    }

    public void setPositionOrder(Integer positionOrder) {
        this.positionOrder = positionOrder;
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
