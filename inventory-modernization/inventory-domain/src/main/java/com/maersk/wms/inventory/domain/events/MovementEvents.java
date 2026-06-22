package com.maersk.wms.inventory.domain.events;

import com.maersk.wms.inventory.shared.kernel.events.InventoryDomainEvent;
import com.maersk.wms.inventory.shared.kernel.identifiers.*;
import com.maersk.wms.inventory.shared.kernel.valueobjects.*;

import java.time.Instant;

/**
 * Domain events for inventory movement/transfer operations.
 * Published to downstream services: inventory-operations-service.
 */
public final class MovementEvents {

    private MovementEvents() {}

    public record InventoryTransferInitiated(
            TransferKey transferKey,
            InventoryKey inventoryKey,
            SkuKey skuKey,
            LocationKey fromLocationKey,
            LocationKey toLocationKey,
            LpnKey lpnKey,
            Quantity quantity,
            String transferType,
            String transferReason,
            UserKey initiatedBy,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.transfer_initiated";
        }
    }

    public record InventoryTransferCompleted(
            TransferKey transferKey,
            InventoryKey fromInventoryKey,
            InventoryKey toInventoryKey,
            SkuKey skuKey,
            LocationKey fromLocationKey,
            LocationKey toLocationKey,
            LpnKey lpnKey,
            Quantity transferredQuantity,
            UserKey completedBy,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.transfer_completed";
        }
    }

    public record InventoryTransferCancelled(
            TransferKey transferKey,
            String cancellationReason,
            UserKey cancelledBy,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.transfer_cancelled";
        }
    }

    public record PutawayCompleted(
            TransferKey transferKey,
            ReceiptKey receiptKey,
            InventoryKey inventoryKey,
            SkuKey skuKey,
            LocationKey stagingLocationKey,
            LocationKey putawayLocationKey,
            LpnKey lpnKey,
            Quantity quantity,
            UserKey completedBy,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.putaway_completed";
        }
    }

    public record ReplenishmentCompleted(
            TransferKey transferKey,
            InventoryKey fromInventoryKey,
            InventoryKey toInventoryKey,
            SkuKey skuKey,
            LocationKey reserveLocationKey,
            LocationKey pickLocationKey,
            Quantity replenishedQuantity,
            UserKey completedBy,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.replenishment_completed";
        }
    }

    public record LocationConsolidated(
            LocationKey locationKey,
            int lpnsConsolidated,
            Quantity totalQuantityConsolidated,
            UserKey consolidatedBy,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.location_consolidated";
        }
    }
}
