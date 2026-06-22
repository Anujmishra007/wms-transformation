package com.maersk.wms.outbound.plugin;

import com.maersk.wms.outbound.domain.Allocation;
import com.maersk.wms.outbound.domain.AllocationStrategy;
import com.maersk.wms.outbound.domain.Order;
import com.maersk.wms.outbound.domain.OrderDetail;

import java.util.List;

/**
 * Plugin interface for allocation operations.
 * Allows client-specific customizations for inventory allocation.
 */
public interface AllocationPlugin extends OutboundPlugin {

    /**
     * Determine allocation strategy for order.
     */
    default AllocationStrategy determineStrategy(Order order, OutboundPluginContext context) {
        return AllocationStrategy.FIFO;
    }

    /**
     * Called before allocation starts.
     */
    default PluginResult beforeAllocate(Order order, OutboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called after allocation completes.
     */
    default PluginResult afterAllocate(Order order, List<Allocation> allocations, OutboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Filter or sort candidate inventory for allocation.
     */
    default List<CandidateInventory> filterCandidates(OrderDetail orderLine,
                                                       List<CandidateInventory> candidates,
                                                       OutboundPluginContext context) {
        return candidates;
    }

    /**
     * Validate allocation before committing.
     */
    default PluginResult validateAllocation(Allocation allocation, OutboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called when allocation fails or shorts.
     */
    default PluginResult onAllocationShort(OrderDetail orderLine,
                                           java.math.BigDecimal shortQty,
                                           OutboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Candidate inventory for allocation.
     */
    @lombok.Data
    @lombok.Builder
    class CandidateInventory {
        private String location;
        private String lpn;
        private String lot;
        private java.math.BigDecimal availableQty;
        private java.time.LocalDateTime receiptDate;
        private java.time.LocalDateTime expirationDate;
        private String lottable01;
        private String lottable02;
        private int priority;
    }
}
