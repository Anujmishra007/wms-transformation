package com.maersk.wms.inventory.domain.events;

import com.maersk.wms.inventory.shared.kernel.events.InventoryDomainEvent;
import com.maersk.wms.inventory.shared.kernel.identifiers.*;
import com.maersk.wms.inventory.shared.kernel.valueobjects.*;

import java.time.Instant;
import java.util.List;

/**
 * Domain events for inventory hold operations.
 * Published to downstream services: inventory-operations-service.
 */
public final class HoldEvents {

    private HoldEvents() {}

    // ═══════════════════════════════════════════════════════════════
    // SIMPLIFIED HOLD EVENTS (for service compatibility)
    // ═══════════════════════════════════════════════════════════════

    public record HoldApplied(
            InventoryKey inventoryKey,
            String holdCode,
            String reason,
            UserKey appliedBy,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.hold.applied";
        }
    }

    public record HoldReleased(
            InventoryKey inventoryKey,
            String holdCode,
            String reason,
            UserKey releasedBy,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.hold.released";
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // DETAILED HOLD EVENTS
    // ═══════════════════════════════════════════════════════════════

    public record InventoryHoldApplied(
            HoldKey holdKey,
            String holdCode,
            String holdType,
            String holdReason,
            HoldScope scope,
            List<InventoryKey> affectedInventoryKeys,
            Quantity totalAffectedQuantity,
            UserKey appliedBy,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.hold_applied";
        }
    }

    public enum HoldScope {
        SINGLE_INVENTORY,   // Single LOTxLOCxID
        LPN,                // All inventory in LPN
        LOT,                // All inventory for lot
        SKU,                // All inventory for SKU
        LOCATION,           // All inventory at location
        STORER,             // All inventory for storer
        RECEIPT             // All inventory from receipt
    }

    public record InventoryHoldReleased(
            HoldKey holdKey,
            String holdCode,
            String releaseReason,
            List<InventoryKey> releasedInventoryKeys,
            Quantity totalReleasedQuantity,
            UserKey releasedBy,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.hold_released";
        }
    }

    public record InventoryHoldModified(
            HoldKey holdKey,
            String previousHoldCode,
            String newHoldCode,
            String modificationReason,
            UserKey modifiedBy,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.hold_modified";
        }
    }

    public record QualityHoldApplied(
            HoldKey holdKey,
            String qcReasonCode,
            SkuKey skuKey,
            LotKey lotKey,
            List<InventoryKey> affectedInventoryKeys,
            Quantity affectedQuantity,
            UserKey appliedBy,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.qc_hold_applied";
        }
    }

    public record QualityHoldCleared(
            HoldKey holdKey,
            String qcDecision,          // PASS, FAIL, CONDITIONAL
            SkuKey skuKey,
            LotKey lotKey,
            Quantity passedQuantity,
            Quantity failedQuantity,
            UserKey clearedBy,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.qc_hold_cleared";
        }
    }
}
