package com.maersk.wms.outbound.domain.allocation_service.service;

import com.maersk.wms.outbound.domain.allocation_service.model.PickHeader;
import com.maersk.wms.outbound.domain.allocation_service.strategy.AllocationContext;
import com.maersk.wms.outbound.domain.allocation_service.strategy.AllocationResult;
import com.maersk.wms.outbound.shared.kernel.identifiers.OrderKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.WaveKey;

import java.util.List;

/**
 * Service interface for Inventory Allocation.
 * Part of Inventory Allocation Service bounded context.
 */
public interface AllocationService {

    /**
     * Allocates inventory for a single order.
     */
    AllocationResult allocateOrder(OrderKey orderKey, AllocationContext context);

    /**
     * Allocates inventory for all orders in a wave.
     */
    AllocationResult allocateWave(WaveKey waveKey, AllocationContext context);

    /**
     * Allocates inventory for multiple orders.
     */
    AllocationResult allocateOrders(List<OrderKey> orderKeys, AllocationContext context);

    /**
     * Gets the pick headers created by allocation.
     */
    List<PickHeader> getPickHeaders(OrderKey orderKey);

    /**
     * Gets allocation status for an order.
     */
    AllocationStatus getAllocationStatus(OrderKey orderKey);

    /**
     * Reallocates a pick detail to a different location.
     */
    AllocationResult reallocate(String pickDetailKey, AllocationContext context);

    /**
     * Status of allocation.
     */
    record AllocationStatus(
            OrderKey orderKey,
            boolean fullyAllocated,
            boolean partiallyAllocated,
            java.math.BigDecimal totalOrdered,
            java.math.BigDecimal totalAllocated,
            java.math.BigDecimal shortage,
            List<String> shortageSkus
    ) {}
}
