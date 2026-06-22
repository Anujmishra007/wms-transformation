package com.maersk.wms.inventory.domain.core.model;

import com.maersk.wms.inventory.shared.kernel.identifiers.*;
import com.maersk.wms.inventory.shared.kernel.valueobjects.*;

import lombok.*;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Inventory Snapshot entity.
 * Point-in-time capture of inventory state for reporting and reconciliation.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventorySnapshot {

    private SnapshotKey snapshotKey;

    // Snapshot Metadata
    private LocalDate snapshotDate;
    private Instant snapshotTimestamp;
    private SnapshotType snapshotType;
    private String snapshotReason;

    // Inventory Identity
    private InventoryKey inventoryKey;
    private SkuKey skuKey;
    private StorerKey storerKey;
    private WarehouseKey warehouseKey;
    private LocationKey locationKey;
    private LpnKey lpnKey;
    private LotKey lotKey;

    // Quantities at snapshot time
    private Quantity onHandQuantity;
    private Quantity allocatedQuantity;
    private Quantity pickedQuantity;
    private Quantity availableQuantity;

    // Status at snapshot time
    private Inventory.InventoryStatusCode status;
    private String holdCode;

    // Lottables at snapshot time
    private LottableAttributes lottables;

    // Value (optional - for financial reporting)
    private java.math.BigDecimal unitCost;
    private java.math.BigDecimal totalValue;

    // Audit
    private Instant createdAt;
    private String createdBy;

    public enum SnapshotType {
        DAILY_EOD,          // End of day snapshot
        PERIOD_END,         // Month/quarter/year end
        PHYSICAL_COUNT,     // Before/after physical inventory
        CYCLE_COUNT,        // Before/after cycle count
        ON_DEMAND,          // User-requested snapshot
        SYSTEM_BACKUP,      // System backup snapshot
        RECONCILIATION      // Stock reconciliation
    }

    /**
     * Create snapshot from current inventory state.
     */
    public static InventorySnapshot fromInventory(Inventory inventory, SnapshotType type, String reason) {
        return InventorySnapshot.builder()
                .snapshotKey(new SnapshotKey(java.util.UUID.randomUUID().toString()))
                .snapshotDate(LocalDate.now())
                .snapshotTimestamp(Instant.now())
                .snapshotType(type)
                .snapshotReason(reason)
                .inventoryKey(inventory.getInventoryKey())
                .skuKey(inventory.getSkuKey())
                .storerKey(inventory.getStorerKey())
                .warehouseKey(inventory.getWarehouseKey())
                .locationKey(inventory.getLocationKey())
                .lpnKey(inventory.getLpnKey())
                .lotKey(inventory.getLotKey())
                .onHandQuantity(inventory.getOnHandQuantity())
                .allocatedQuantity(inventory.getAllocatedQuantity())
                .pickedQuantity(inventory.getPickedQuantity())
                .availableQuantity(inventory.getAvailableQuantity())
                .status(inventory.getStatus())
                .holdCode(inventory.getHoldCode())
                .lottables(inventory.getLottables())
                .createdAt(Instant.now())
                .build();
    }

    /**
     * Create snapshot from current inventory state with explicit key and period.
     */
    public static InventorySnapshot fromInventory(SnapshotKey snapshotKey, Inventory inventory,
                                                    String period, SnapshotType type) {
        return InventorySnapshot.builder()
                .snapshotKey(snapshotKey)
                .snapshotDate(LocalDate.now())
                .snapshotTimestamp(Instant.now())
                .snapshotType(type)
                .snapshotReason(period)
                .inventoryKey(inventory.inventoryKey())
                .skuKey(inventory.skuKey())
                .storerKey(inventory.storerKey())
                .warehouseKey(inventory.warehouseKey())
                .locationKey(inventory.locationKey())
                .lpnKey(inventory.lpnKey())
                .lotKey(inventory.lotKey())
                .onHandQuantity(inventory.onHandQuantity())
                .allocatedQuantity(inventory.allocatedQuantity())
                .pickedQuantity(inventory.pickedQuantity())
                .availableQuantity(inventory.availableQuantity())
                .status(inventory.status())
                .holdCode(inventory.holdCode())
                .lottables(inventory.lottables())
                .createdAt(Instant.now())
                .build();
    }
}
