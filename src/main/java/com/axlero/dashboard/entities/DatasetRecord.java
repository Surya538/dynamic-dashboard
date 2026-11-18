package com.axlero.dashboard.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "dataset_records", indexes = {
        @Index(name = "idx_dataset_ws_name", columnList = "workspace_id, dataset_name"),
        @Index(name = "idx_dataset_region", columnList = "region"),
        @Index(name = "idx_dataset_category", columnList = "category")
})
public class DatasetRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    @Column(name = "dataset_name", nullable = false, length = 100)
    private String datasetName = "sales";

    @Column(length = 100)
    private String category; // e.g. "Electronics", "Furniture", "Software"

    @Column(length = 100)
    private String region; // e.g. "North America", "Europe", "Asia-Pacific"

    @Column(length = 50)
    private String status; // e.g. "COMPLETED", "PENDING", "CANCELLED"

    @Column(name = "numeric_value", precision = 14, scale = 2)
    private BigDecimal numericValue; // e.g. amount, revenue

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "record_date")
    private LocalDate recordDate = LocalDate.now();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public DatasetRecord() {}

    public DatasetRecord(Workspace workspace, String datasetName, String category,
                         String region, String status, BigDecimal numericValue,
                         Integer quantity, LocalDate recordDate) {
        this.workspace = workspace;
        this.datasetName = datasetName != null ? datasetName : "sales";
        this.category = category;
        this.region = region;
        this.status = status;
        this.numericValue = numericValue;
        this.quantity = quantity != null ? quantity : 1;
        this.recordDate = recordDate != null ? recordDate : LocalDate.now();
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getNumericValue() {
        return numericValue;
    }

    public void setNumericValue(BigDecimal numericValue) {
        this.numericValue = numericValue;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDate getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(LocalDate recordDate) {
        this.recordDate = recordDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
