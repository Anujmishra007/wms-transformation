package com.maersk.wms.outbound.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Temporal activity interface for allocation operations.
 * Part of Inventory Allocation Service bounded context.
 */
@ActivityInterface
public interface AllocationActivities {

    @ActivityMethod
    AllocationResult allocateOrder(String orderNumber, String clientCode, String facilityCode);

    @ActivityMethod
    AllocationResult allocateOrderLine(String orderNumber, String lineNumber,
                                        String clientCode, String facilityCode);

    @ActivityMethod
    void deallocateOrder(String orderNumber, String clientCode, String facilityCode);

    @ActivityMethod
    void deallocateAllocation(String allocationId, String clientCode, String facilityCode);

    @ActivityMethod
    AllocationResult reallocateShort(String orderNumber, String clientCode, String facilityCode);

    // === Additional methods for workflow integration ===

    /**
     * Validates an allocation request before processing.
     */
    @ActivityMethod
    boolean validateAllocationRequest(String waveKey, List<String> orderKeys, String userId);

    /**
     * Allocates inventory for an order using a specific strategy.
     */
    @ActivityMethod
    AllocationResultV2 allocateOrderWithStrategy(String orderKey, String strategy,
                                                   boolean allowPartial, String userId);

    /**
     * Unallocates all allocations for a wave.
     */
    @ActivityMethod
    void unallocateWave(String waveKey, String userId);

    /**
     * Releases pick headers for picking.
     */
    @ActivityMethod
    void releasePickHeaders(List<String> pickHeaderKeys, String userId);

    /**
     * Gets allocation status for an order.
     */
    @ActivityMethod
    AllocationStatusResult getAllocationStatus(String orderKey);

    @Data
    @Builder
    class AllocationResult {
        private boolean success;
        private boolean partiallyAllocated;
        private int allocatedLines;
        private int shortLines;
        private BigDecimal totalAllocatedQty;
        private BigDecimal shortQty;
        private List<String> allocationIds;
        private List<String> errors;
        private List<AllocationDetail> allocations;
    }

    @Data
    @Builder
    class AllocationResultV2 {
        private String orderKey;
        private boolean fullyAllocated;
        private BigDecimal allocatedQty;
        private BigDecimal shortageQty;
        private List<String> pickHeaderKeys;
        private Map<String, BigDecimal> shortagesBySku;
    }

    @Data
    @Builder
    class AllocationStatusResult {
        private String orderKey;
        private boolean fullyAllocated;
        private boolean partiallyAllocated;
        private BigDecimal totalOrdered;
        private BigDecimal totalAllocated;
        private BigDecimal shortage;
    }

    @Data
    @Builder
    class AllocationDetail {
        private String allocationId;
        private String orderNumber;
        private String lineNumber;
        private String sku;
        private String location;
        private String lpn;
        private String lot;
        private BigDecimal quantity;
        private String status;
    }
}
