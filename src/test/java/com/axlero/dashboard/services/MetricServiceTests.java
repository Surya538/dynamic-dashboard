package com.axlero.dashboard.services;

import com.axlero.dashboard.dto.MetricDtos.MetricQueryRequest;
import com.axlero.dashboard.dto.MetricDtos.MetricQueryResult;
import com.axlero.dashboard.entities.MetricType;
import com.axlero.dashboard.exceptions.InvalidQueryException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MetricServiceTests {

    @Autowired
    private MetricService metricService;

    @Test
    @DisplayName("Should execute SUM aggregation on sales by region successfully")
    void testDynamicAggregationByRegion() {
        MetricQueryRequest request = new MetricQueryRequest(
                1L,
                "sales",
                "region",
                "numericValue",
                MetricType.SUM,
                Map.of(),
                10
        );

        MetricQueryResult result = metricService.executeAggregation(request, "demouser");

        assertNotNull(result);
        assertEquals("region", result.dimensionField());
        assertEquals(MetricType.SUM, result.aggregation());
        assertFalse(result.buckets().isEmpty());
        assertTrue(result.overallTotal().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Should block SQL injection and reject unwhitelisted dimension fields")
    void testSqlInjectionPreventionOnDimension() {
        MetricQueryRequest request = new MetricQueryRequest(
                1L,
                "sales",
                "region; DROP TABLE users;--",
                "numericValue",
                MetricType.SUM,
                Map.of(),
                10
        );

        assertThrows(InvalidQueryException.class, () ->
                metricService.executeAggregation(request, "demouser")
        );
    }

    @Test
    @DisplayName("Should block SQL injection on metric field")
    void testSqlInjectionPreventionOnMetric() {
        MetricQueryRequest request = new MetricQueryRequest(
                1L,
                "sales",
                "region",
                "nonExistentColumn",
                MetricType.SUM,
                Map.of(),
                10
        );

        assertThrows(InvalidQueryException.class, () ->
                metricService.executeAggregation(request, "demouser")
        );
    }
}
