package com.maersk.wms.outbound.domain.allocation_service.service;

import com.maersk.wms.outbound.shared.kernel.identifiers.OrderKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.PickDetailKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.PickHeaderKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.WaveKey;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for Inventory Unallocation.
 * Part of Inventory Allocation Service - Unallocate module.
 */
public interface UnallocationService {

    /**
     * Unallocates a single pick detail.
     */
    UnallocationResult unallocatePickDetail(PickDetailKey pickDetailKey, UnallocationContext context);

    /**
     * Unallocates an entire pick header.
     */
    UnallocationResult unallocatePickHeader(PickHeaderKey pickHeaderKey, UnallocationContext context);

    /**
     * Unallocates all allocations for an order.
     */
    UnallocationResult unallocateOrder(OrderKey orderKey, UnallocationContext context);

    /**
     * Unallocates all allocations for a wave.
     */
    UnallocationResult unallocateWave(WaveKey waveKey, UnallocationContext context);

    /**
     * Unallocates a specific quantity from a pick detail.
     */
    UnallocationResult unallocatePartial(PickDetailKey pickDetailKey, BigDecimal qtyToUnallocate, UnallocationContext context);

    /**
     * Context for unallocation.
     */
    record UnallocationContext(
            String userId,
            String reason,
            boolean releaseInventory,
            boolean triggerReallocate
    ) {}

    /**
     * Result of unallocation.
     */
    record UnallocationResult(
            boolean success,
            BigDecimal qtyUnallocated,
            int pickDetailsAffected,
            List<String> messages
    ) {}
}
