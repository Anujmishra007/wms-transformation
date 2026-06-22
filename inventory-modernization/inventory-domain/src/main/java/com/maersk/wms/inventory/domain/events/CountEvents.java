package com.maersk.wms.inventory.domain.events;

import com.maersk.wms.inventory.shared.kernel.events.InventoryDomainEvent;
import com.maersk.wms.inventory.shared.kernel.identifiers.*;
import com.maersk.wms.inventory.shared.kernel.valueobjects.*;

import com.maersk.wms.inventory.domain.controls.model.CountType;

import java.time.Instant;
import java.util.List;

/**
 * Domain events for inventory counting operations.
 * Published to inventory-operations-service.
 */
public final class CountEvents {

    private CountEvents() {}

    // ═══════════════════════════════════════════════════════════════
    // COUNT TYPE EVENTS
    // ═══════════════════════════════════════════════════════════════

    public record CountTypeCreated(
            CountKey countTypeKey,
            String countTypeCode,
            CountType.CountStrategy strategy,
            WarehouseKey warehouseKey,
            UserKey createdBy,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.count_type_created";
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // CYCLE COUNT EVENTS
    // ═══════════════════════════════════════════════════════════════

    public record CycleCountInitiated(
            CountKey countKey,
            String countTypeCode,
            LocationKey locationKey,
            List<SkuKey> skusToCount,
            UserKey initiatedBy,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.cycle_count_initiated";
        }
    }

    public record CycleCountCompleted(
            CountKey countKey,
            LocationKey locationKey,
            int inventoryRecordsCounted,
            int variancesFound,
            Quantity totalVarianceQuantity,
            UserKey countedBy,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.cycle_count_completed";
        }
    }

    public record CountVarianceDetected(
            CountKey countKey,
            InventoryKey inventoryKey,
            SkuKey skuKey,
            LocationKey locationKey,
            LpnKey lpnKey,
            Quantity expectedQuantity,
            Quantity countedQuantity,
            Quantity varianceQuantity,
            double variancePercent,
            boolean requiresRecount,
            UserKey countedBy,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.count_variance_detected";
        }
    }

    public record CountVarianceApproved(
            CountKey countKey,
            InventoryKey inventoryKey,
            Quantity varianceQuantity,
            String approvalNotes,
            UserKey approvedBy,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.count_variance_approved";
        }
    }

    public record CountVarianceRejected(
            CountKey countKey,
            InventoryKey inventoryKey,
            Quantity varianceQuantity,
            String rejectionReason,
            boolean recountRequired,
            UserKey rejectedBy,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.count_variance_rejected";
        }
    }

    public record RecountRequested(
            CountKey originalCountKey,
            CountKey recountKey,
            InventoryKey inventoryKey,
            LocationKey locationKey,
            int recountAttempt,
            String recountReason,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.recount_requested";
        }
    }

    public record PhysicalCountStarted(
            CountKey countKey,
            WarehouseKey warehouseKey,
            List<String> zonesIncluded,
            int estimatedLocations,
            UserKey startedBy,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.physical_count_started";
        }
    }

    public record PhysicalCountCompleted(
            CountKey countKey,
            WarehouseKey warehouseKey,
            int locationsCounted,
            int inventoryRecordsCounted,
            int variancesFound,
            Quantity totalPositiveVariance,
            Quantity totalNegativeVariance,
            UserKey completedBy,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.physical_count_completed";
        }
    }
}
