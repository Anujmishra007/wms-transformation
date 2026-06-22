package com.maersk.wms.events.contracts.inventory;

import com.maersk.wms.events.contracts.BaseDomainEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Event contracts for Inventory operations.
 * Published by: inventory-service
 * Consumed by: picking-operations-service, packing-operations-service,
 *              order-service, task-management-service
 */
public final class InventoryEvents {

    private InventoryEvents() {}

    /**
     * Published when inventory is created.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class InventoryCreated extends BaseDomainEvent {
        private String inventoryKey;
        private String skuKey;
        private String lotKey;
        private String locationKey;
        private String lpnKey;
        private String storerKey;
        private BigDecimal quantity;
        private String uom;
        private String source; // RECEIPT, RETURN, ADJUSTMENT
        private String sourceKey;
    }

    /**
     * Published when inventory quantity changes.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class InventoryQuantityChanged extends BaseDomainEvent {
        private String inventoryKey;
        private String skuKey;
        private String locationKey;
        private BigDecimal previousQuantity;
        private BigDecimal newQuantity;
        private BigDecimal changeAmount;
        private String reason;
        private String changedBy;
    }

    /**
     * Published when inventory is allocated.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class InventoryAllocated extends BaseDomainEvent {
        private String allocationKey;
        private String orderKey;
        private String skuKey;
        private BigDecimal allocatedQuantity;
        private List<AllocationDetail> allocations;
    }

    /**
     * Published when allocation is released.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class InventoryDeallocated extends BaseDomainEvent {
        private String allocationKey;
        private String orderKey;
        private BigDecimal deallocatedQuantity;
        private String reason;
    }

    /**
     * Published when inventory is transferred.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class InventoryTransferred extends BaseDomainEvent {
        private String inventoryKey;
        private String skuKey;
        private String lotKey;
        private String fromLocationKey;
        private String toLocationKey;
        private BigDecimal quantity;
        private String reason;
        private String transferredBy;
    }

    /**
     * Published when hold is applied.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class InventoryHoldApplied extends BaseDomainEvent {
        private String inventoryKey;
        private String skuKey;
        private String locationKey;
        private String holdCode;
        private String reason;
        private String appliedBy;
    }

    /**
     * Published when hold is released.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class InventoryHoldReleased extends BaseDomainEvent {
        private String inventoryKey;
        private String skuKey;
        private String locationKey;
        private String holdCode;
        private String reason;
        private String releasedBy;
    }

    /**
     * Published when inventory shortage is detected.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class InventoryShortage extends BaseDomainEvent {
        private String skuKey;
        private String orderKey;
        private BigDecimal requestedQuantity;
        private BigDecimal availableQuantity;
        private BigDecimal shortageQuantity;
    }

    /**
     * Allocation detail for events.
     */
    @Data
    @NoArgsConstructor
    public static class AllocationDetail {
        private String inventoryKey;
        private String locationKey;
        private String lotKey;
        private String lpnKey;
        private BigDecimal quantity;
    }
}
