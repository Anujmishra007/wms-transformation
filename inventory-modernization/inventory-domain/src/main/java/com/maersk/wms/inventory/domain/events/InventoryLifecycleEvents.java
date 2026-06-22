package com.maersk.wms.inventory.domain.events;

import com.maersk.wms.inventory.shared.kernel.events.InventoryDomainEvent;
import com.maersk.wms.inventory.shared.kernel.identifiers.*;
import com.maersk.wms.inventory.shared.kernel.valueobjects.*;
import com.maersk.wms.inventory.domain.core.model.Inventory;

import java.time.Instant;

/**
 * Domain events for Inventory Lifecycle bounded context.
 * Published to downstream services: picking-operations-service, inventory-operations-service.
 */
public final class InventoryLifecycleEvents {

    private InventoryLifecycleEvents() {}

    // ═══════════════════════════════════════════════════════════════
    // INVENTORY CREATED EVENTS (from inbound-operations-service)
    // ═══════════════════════════════════════════════════════════════

    public record InventoryCreated(
            InventoryKey inventoryKey,
            SkuKey skuKey,
            LotKey lotKey,
            LocationKey locationKey,
            LpnKey lpnKey,
            StorerKey storerKey,
            WarehouseKey warehouseKey,
            Quantity quantity,
            LottableAttributes lottables,
            String sourceType,
            String sourceKey,
            UserKey createdBy,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.created";
        }
    }

    public record InventoryReceivedFromInbound(
            InventoryKey inventoryKey,
            ReceiptKey receiptKey,
            String receiptLineNumber,
            SkuKey skuKey,
            LocationKey locationKey,
            LpnKey lpnKey,
            Quantity quantity,
            LottableAttributes lottables,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.received_from_inbound";
        }
    }

    public record ReturnReceiptCompleted(
            InventoryKey inventoryKey,
            String returnKey,
            OrderKey originalOrderKey,
            SkuKey skuKey,
            LocationKey locationKey,
            Quantity quantity,
            String returnReason,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.return_receipt_completed";
        }
    }

    public record CrossdockInventoryCreated(
            InventoryKey inventoryKey,
            ReceiptKey receiptKey,
            OrderKey targetOrderKey,
            SkuKey skuKey,
            LocationKey locationKey,
            Quantity quantity,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.crossdock_created";
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // INVENTORY CHANGED EVENTS
    // ═══════════════════════════════════════════════════════════════

    public record InventoryQuantityChanged(
            InventoryKey inventoryKey,
            SkuKey skuKey,
            LocationKey locationKey,
            LpnKey lpnKey,
            Quantity previousQuantity,
            Quantity newQuantity,
            Quantity changeAmount,
            String changeReason,
            String sourceType,
            String sourceKey,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.quantity_changed";
        }
    }

    public record InventoryStatusChanged(
            InventoryKey inventoryKey,
            SkuKey skuKey,
            LocationKey locationKey,
            Inventory.InventoryStatusCode previousStatus,
            Inventory.InventoryStatusCode newStatus,
            String changeReason,
            UserKey changedBy,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.status_changed";
        }
    }

    public record InventoryAttributesUpdated(
            InventoryKey inventoryKey,
            SkuKey skuKey,
            LottableAttributes previousLottables,
            LottableAttributes newLottables,
            UserKey updatedBy,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.attributes_updated";
        }
    }

    public record InventoryOwnershipChanged(
            InventoryKey inventoryKey,
            SkuKey skuKey,
            StorerKey previousStorerKey,
            StorerKey newStorerKey,
            Quantity quantity,
            UserKey changedBy,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.ownership_changed";
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // INVENTORY REMOVED EVENTS (to picking-operations-service)
    // ═══════════════════════════════════════════════════════════════

    public record InventoryDepleted(
            InventoryKey inventoryKey,
            SkuKey skuKey,
            LocationKey locationKey,
            LpnKey lpnKey,
            Quantity depletedQuantity,
            String depletionType,
            String sourceKey,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.depleted";
        }
    }

    public record InventoryPicked(
            InventoryKey inventoryKey,
            AllocationKey allocationKey,
            OrderKey orderKey,
            SkuKey skuKey,
            LocationKey locationKey,
            LpnKey lpnKey,
            Quantity pickedQuantity,
            UserKey pickedBy,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.picked";
        }
    }

    public record InventoryShipped(
            InventoryKey inventoryKey,
            OrderKey orderKey,
            String shipmentKey,
            SkuKey skuKey,
            Quantity shippedQuantity,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.shipped";
        }
    }

    public record InventoryWrittenOff(
            InventoryKey inventoryKey,
            SkuKey skuKey,
            LocationKey locationKey,
            Quantity writtenOffQuantity,
            String writeOffReason,
            UserKey writtenOffBy,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.written_off";
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // INVENTORY FINALIZATION EVENTS
    // ═══════════════════════════════════════════════════════════════

    public record InventoryTransactionCommitted(
            TransactionKey transactionKey,
            InventoryKey inventoryKey,
            String transactionType,
            Quantity quantity,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.transaction_committed";
        }
    }

    public record InventoryReconciled(
            WarehouseKey warehouseKey,
            String reconciliationType,
            Quantity expectedTotal,
            Quantity actualTotal,
            Quantity variance,
            int recordsAffected,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.reconciled";
        }
    }
}
