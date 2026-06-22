package com.maersk.wms.outbound.workflow.allocation;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Result of allocation workflow execution.
 */
@Value
@Builder
public class AllocationWorkflowResult {

    String waveKey;
    boolean success;
    boolean fullyAllocated;
    AllocationWorkflowStatus status;

    // Quantities
    BigDecimal totalOrdered;
    BigDecimal totalAllocated;
    BigDecimal totalShortage;

    // Counts
    int ordersProcessed;
    int ordersFullyAllocated;
    int ordersPartiallyAllocated;
    int ordersNotAllocated;
    int pickHeadersCreated;
    int pickDetailsCreated;

    // Shortages by SKU
    Map<String, BigDecimal> shortagesBySku;

    // Pick headers created (for release)
    List<String> pickHeaderKeys;

    // Messages
    String message;
    List<String> warnings;
    List<String> errors;

    // Timing
    long executionTimeMs;
}
