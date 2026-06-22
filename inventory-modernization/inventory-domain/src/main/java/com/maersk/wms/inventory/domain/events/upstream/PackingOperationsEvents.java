package com.maersk.wms.inventory.domain.events.upstream;

import com.maersk.wms.inventory.shared.kernel.identifiers.*;
import com.maersk.wms.inventory.shared.kernel.valueobjects.*;

import java.time.Instant;
import java.util.List;

/**
 * Upstream events consumed FROM packing-operations-service.
 * These events trigger inventory consumption and container updates.
 */
public final class PackingOperationsEvents {

    private PackingOperationsEvents() {}

    // ═══════════════════════════════════════════════════════════════
    // SORTING EVENTS - Trigger inventory movement to sort lanes
    // ═══════════════════════════════════════════════════════════════

    public record ItemSortedToLane(
            String eventId,
            String sortTaskKey,
            OrderKey orderKey,
            SkuKey skuKey,
            InventoryKey sourceInventoryKey,
            LocationKey sortLaneLocationKey,
            LpnKey targetLpnKey,
            Quantity sortedQuantity,
            UserKey sortedBy,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) {}

    public record SortingCompleted(
            String eventId,
            String sortBatchKey,
            List<OrderKey> orderKeys,
            int totalItemsSorted,
            Quantity totalQuantity,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) {}

    // ═══════════════════════════════════════════════════════════════
    // PACKING EVENTS - Trigger inventory consumption/container creation
    // ═══════════════════════════════════════════════════════════════

    public record ItemPackedToCarton(
            String eventId,
            String packTaskKey,
            OrderKey orderKey,
            SkuKey skuKey,
            InventoryKey sourceInventoryKey,
            LpnKey cartonLpnKey,
            Quantity packedQuantity,
            UserKey packedBy,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) {}

    public record CartonClosed(
            String eventId,
            LpnKey cartonLpnKey,
            OrderKey orderKey,
            List<PackedItem> packedItems,
            Quantity totalQuantity,
            double weight,
            String weightUom,
            UserKey closedBy,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) {
        public record PackedItem(
                SkuKey skuKey,
                Quantity quantity
        ) {}
    }

    public record PackingCompleted(
            String eventId,
            OrderKey orderKey,
            List<LpnKey> cartonLpnKeys,
            int totalCartons,
            Quantity totalQuantity,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) {}

    public record PackingShortDetected(
            String eventId,
            OrderKey orderKey,
            SkuKey skuKey,
            Quantity expectedQuantity,
            Quantity packedQuantity,
            Quantity shortQuantity,
            String shortReason,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) {}
}
