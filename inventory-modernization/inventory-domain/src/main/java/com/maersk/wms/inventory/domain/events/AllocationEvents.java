package com.maersk.wms.inventory.domain.events;

import com.maersk.wms.inventory.shared.kernel.events.InventoryDomainEvent;
import com.maersk.wms.inventory.shared.kernel.identifiers.*;
import com.maersk.wms.inventory.shared.kernel.valueobjects.*;

import java.time.Instant;
import java.util.List;

/**
 * Domain events for inventory allocation operations.
 * Published to downstream services: picking-operations-service.
 */
public final class AllocationEvents {

    private AllocationEvents() {}

    // ═══════════════════════════════════════════════════════════════
    // SIMPLIFIED ALLOCATION EVENTS (for service compatibility)
    // ═══════════════════════════════════════════════════════════════

    public record AllocationDetail(
            AllocationKey allocationKey,
            InventoryKey inventoryKey,
            LocationKey locationKey,
            LotKey lotKey,
            LpnKey lpnKey,
            Quantity quantity,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.allocation.detail";
        }
    }

    public record AllocationShortage(
            SkuKey skuKey,
            OrderKey orderKey,
            Quantity requestedQuantity,
            Quantity allocatedQuantity,
            Quantity shortageQuantity,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.allocation.shortage";
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // DETAILED ALLOCATION EVENTS
    // ═══════════════════════════════════════════════════════════════

    public record InventoryAllocated(
            AllocationKey allocationKey,
            OrderKey orderKey,
            String orderLineNumber,
            SkuKey skuKey,
            StorerKey storerKey,
            Quantity allocatedQuantity,
            List<AllocationLine> allocationLines,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.allocated";
        }

        public record AllocationLine(
                InventoryKey inventoryKey,
                LotKey lotKey,
                LocationKey locationKey,
                LpnKey lpnKey,
                Quantity quantity
        ) {}
    }

    public record InventoryDeallocated(
            AllocationKey allocationKey,
            OrderKey orderKey,
            SkuKey skuKey,
            Quantity deallocatedQuantity,
            String deallocationReason,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.deallocated";
        }
    }

    public record AllocationShortageDetected(
            OrderKey orderKey,
            String orderLineNumber,
            SkuKey skuKey,
            StorerKey storerKey,
            Quantity requestedQuantity,
            Quantity allocatedQuantity,
            Quantity shortQuantity,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.allocation_shortage";
        }
    }

    public record AllocationReallocated(
            AllocationKey previousAllocationKey,
            AllocationKey newAllocationKey,
            OrderKey orderKey,
            SkuKey skuKey,
            InventoryKey fromInventoryKey,
            InventoryKey toInventoryKey,
            Quantity quantity,
            String reason,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.allocation_reallocated";
        }
    }

    public record AllocationExpired(
            AllocationKey allocationKey,
            OrderKey orderKey,
            SkuKey skuKey,
            Quantity expiredQuantity,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.allocation_expired";
        }
    }
}
