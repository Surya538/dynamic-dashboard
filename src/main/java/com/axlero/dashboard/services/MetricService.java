package com.axlero.dashboard.services;

import com.axlero.dashboard.dto.MetricDtos.MetricBucket;
import com.axlero.dashboard.dto.MetricDtos.MetricQueryRequest;
import com.axlero.dashboard.dto.MetricDtos.MetricQueryResult;
import com.axlero.dashboard.entities.DatasetRecord;
import com.axlero.dashboard.entities.MetricType;
import com.axlero.dashboard.entities.Workspace;
import com.axlero.dashboard.exceptions.InvalidQueryException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.*;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Core Dynamic Query Engine using Spring Data JPA Criteria API.
 * Dynamically builds GROUP BY, WHERE filters, and aggregation (SUM, COUNT, AVG, MIN, MAX)
 * queries while enforcing strict field whitelisting to eliminate SQL injection vulnerabilities.
 * Cached via Caffeine to maintain sub-100ms API response times.
 */
@Service
public class MetricService {

    @PersistenceContext
    private EntityManager entityManager;

    private final WorkspaceService workspaceService;

    // Strict whitelist of allowable dimension fields to prevent SQL injection / invalid column exploits
    private static final Set<String> ALLOWED_DIMENSIONS = Set.of(
            "category", "region", "status", "datasetName"
    );

    // Strict whitelist of allowable numeric metric fields
    private static final Set<String> ALLOWED_METRICS = Set.of(
            "numericValue", "quantity"
    );

    public static Set<String> getAllowedDimensions() {
        return ALLOWED_DIMENSIONS;
    }

    public static Set<String> getAllowedMetrics() {
        return ALLOWED_METRICS;
    }

    public MetricService(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "metricsCache", key = "#request.toString() + '-' + #username")
    public MetricQueryResult executeAggregation(MetricQueryRequest request, String username) {
        long startTime = System.currentTimeMillis();

        // 1. Validate tenant workspace access (RBAC enforcement)
        Workspace workspace = workspaceService.getEntityById(request.workspaceId());
        workspaceService.validateOwnership(workspace, username);

        // 2. Strict Whitelist Validation (SQL Injection Prevention)
        validateDimension(request.dimensionField());
        validateMetric(request.metricField());

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<DatasetRecord> root = query.from(DatasetRecord.class);

        // 3. Build WHERE Predicates (Tenant isolation + safe filters)
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("workspace").get("id"), request.workspaceId()));

        if (request.datasetName() != null && !request.datasetName().isBlank()) {
            predicates.add(cb.equal(root.get("datasetName"), request.datasetName().trim()));
        }

        // Apply dynamic user filters safely
        if (request.filters() != null && !request.filters().isEmpty()) {
            for (Map.Entry<String, String> filter : request.filters().entrySet()) {
                String field = filter.getKey();
                String value = filter.getValue();
                if (ALLOWED_DIMENSIONS.contains(field) && value != null && !value.isBlank()) {
                    predicates.add(cb.equal(root.get(field), value.trim()));
                }
            }
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        // 4. Build Dynamic Aggregation Expression
        Expression<String> dimensionExpr = root.get(request.dimensionField());
        Expression<?> metricExpr = root.get(request.metricField());
        Expression<?> aggExpr = buildAggregationExpression(cb, metricExpr, request.aggregation());
        Expression<Long> countExpr = cb.count(root);

        // 5. Select & Group By
        query.multiselect(
                dimensionExpr.alias("dimension"),
                aggExpr.alias("metricValue"),
                countExpr.alias("recordCount")
        );
        query.groupBy(dimensionExpr);
        query.orderBy(cb.desc(aggExpr));

        // 6. Execute Query with pagination limit
        int limit = (request.limit() != null && request.limit() > 0 && request.limit() <= 200)
                ? request.limit() : 50;

        List<Tuple> results = entityManager.createQuery(query)
                .setMaxResults(limit)
                .getResultList();

        // 7. Map Tuples to Java DTOs
        List<MetricBucket> buckets = new ArrayList<>();
        BigDecimal overallTotal = BigDecimal.ZERO;

        for (Tuple tuple : results) {
            String dimVal = tuple.get("dimension", String.class);
            Object rawMetric = tuple.get("metricValue");
            Long count = tuple.get("recordCount", Long.class);

            BigDecimal metricVal = convertToBigDecimal(rawMetric);
            if (metricVal != null) {
                overallTotal = overallTotal.add(metricVal);
            }

            buckets.add(new MetricBucket(
                    dimVal != null ? dimVal : "N/A",
                    metricVal != null ? metricVal.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO,
                    count != null ? count : 0L
            ));
        }

        long executionTime = System.currentTimeMillis() - startTime;

        return new MetricQueryResult(
                request.dimensionField(),
                request.metricField(),
                request.aggregation(),
                buckets,
                overallTotal.setScale(2, RoundingMode.HALF_UP),
                executionTime
        );
    }

    private Expression<?> buildAggregationExpression(CriteriaBuilder cb, Expression<?> metricExpr, MetricType type) {
        return switch (type) {
            case SUM -> cb.sum(metricExpr.as(Number.class));
            case AVG -> cb.avg(metricExpr.as(Number.class));
            case COUNT -> cb.count(metricExpr);
            case MIN -> cb.min(metricExpr.as(Number.class));
            case MAX -> cb.max(metricExpr.as(Number.class));
        };
    }

    private BigDecimal convertToBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal bd) return bd;
        if (value instanceof Number num) return BigDecimal.valueOf(num.doubleValue());
        try {
            return new BigDecimal(value.toString());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private void validateDimension(String dimension) {
        if (dimension == null || !ALLOWED_DIMENSIONS.contains(dimension)) {
            throw new InvalidQueryException("Invalid dimension field: '" + dimension + "'. Allowed dimensions: " + ALLOWED_DIMENSIONS);
        }
    }

    private void validateMetric(String metric) {
        if (metric == null || !ALLOWED_METRICS.contains(metric)) {
            throw new InvalidQueryException("Invalid metric field: '" + metric + "'. Allowed metrics: " + ALLOWED_METRICS);
        }
    }
}
