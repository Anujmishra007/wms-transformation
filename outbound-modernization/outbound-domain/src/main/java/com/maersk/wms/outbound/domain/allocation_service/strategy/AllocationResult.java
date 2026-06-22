package com.maersk.wms.outbound.domain.allocation_service.strategy;

import com.maersk.wms.outbound.domain.allocation_service.model.PickDetail;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Result of an allocation operation.
 */
@Value
@Builder
public class AllocationResult {

    boolean success;
    boolean fullyAllocated;
    String strategyUsed;

    // Allocated pick details
    List<PickDetail> allocatedDetails;

    // Quantities
    BigDecimal totalRequested;
    BigDecimal totalAllocated;
    BigDecimal totalShortage;

    // Shortage details by SKU
    Map<String, BigDecimal> shortagesBySku;

    // Messages
    List<String> warnings;
    List<String> errors;

    // Timing
    long executionTimeMs;

    public BigDecimal getAllocationPercentage() {
        if (totalRequested.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return totalAllocated.divide(totalRequested, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    public boolean hasShortages() {
        return totalShortage.compareTo(BigDecimal.ZERO) > 0;
    }

    public static AllocationResult empty() {
        return AllocationResult.builder()
                .success(false)
                .fullyAllocated(false)
                .totalRequested(BigDecimal.ZERO)
                .totalAllocated(BigDecimal.ZERO)
                .totalShortage(BigDecimal.ZERO)
                .allocatedDetails(List.of())
                .shortagesBySku(Map.of())
                .warnings(List.of())
                .errors(List.of())
                .build();
    }
}
