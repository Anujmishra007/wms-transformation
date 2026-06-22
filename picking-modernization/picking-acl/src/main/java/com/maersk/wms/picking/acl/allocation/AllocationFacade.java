package com.maersk.wms.picking.acl.allocation;

import com.maersk.wms.picking.shared.kernel.identifiers.*;
import com.maersk.wms.picking.shared.kernel.valueobjects.Quantity;

import java.util.List;
import java.util.Optional;

/**
 * Anti-Corruption Layer facade for Allocation Service integration.
 * Handles communication with allocation/order service for reallocation scenarios.
 */
public interface AllocationFacade {

    // Deallocation
    void deallocatePickDetail(PickDetailKey pickDetailKey, String reason);
    void deallocateBatch(List<PickDetailKey> pickDetailKeys, String reason);
    void deallocateOrder(OrderKey orderKey, String reason);

    // Reallocation
    Optional<AllocationResult> reallocatePickDetail(PickDetailKey pickDetailKey);
    Optional<AllocationResult> reallocateFromAlternateLocation(PickDetailKey pickDetailKey, LocationKey excludeLocation);
    List<AllocationResult> reallocateBatch(List<PickDetailKey> pickDetailKeys);

    // Short Handling
    Optional<AllocationResult> findAlternateAllocation(PickDetailKey pickDetailKey, Quantity requiredQty);
    void markForBackorder(PickDetailKey pickDetailKey, Quantity shortQty);
    void requestReplenishment(LocationKey location, SkuKey sku, Quantity requiredQty);

    // Query
    Optional<AllocationInfo> getAllocationInfo(PickDetailKey pickDetailKey);
    List<AllocationInfo> getAllocationsByOrder(OrderKey orderKey);
    boolean isFullyAllocated(OrderKey orderKey);

    // Validation
    boolean canDeallocate(PickDetailKey pickDetailKey);
    boolean canReallocate(PickDetailKey pickDetailKey);

    /**
     * Allocation result from reallocation attempt.
     */
    record AllocationResult(
            PickDetailKey pickDetailKey,
            boolean success,
            LocationKey allocatedLocation,
            LpnKey allocatedLpn,
            Quantity allocatedQty,
            String failureReason
    ) {}

    /**
     * Allocation information DTO.
     */
    record AllocationInfo(
            PickDetailKey pickDetailKey,
            OrderKey orderKey,
            SkuKey sku,
            LocationKey allocatedLocation,
            LpnKey allocatedLpn,
            Quantity allocatedQty,
            String allocationStatus,
            String allocationKey
    ) {}
}
