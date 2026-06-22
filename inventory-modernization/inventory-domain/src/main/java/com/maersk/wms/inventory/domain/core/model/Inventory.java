package com.maersk.wms.inventory.domain.core.model;

import com.maersk.wms.inventory.shared.kernel.identifiers.*;
import com.maersk.wms.inventory.shared.kernel.valueobjects.*;

import lombok.*;
import java.time.Instant;
import java.time.LocalDateTime;

/**
 * Core Inventory Aggregate Root.
 * Represents a single inventory record at LOT × LOCATION × LPN granularity.
 * This is the heart of the WMS inventory system.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {

    // Primary Key
    private InventoryKey inventoryKey;

    // Composite Key (LOTxLOCxID)
    private InventoryCompositeKey compositeKey;

    // Alternative Key (SKUxLOC)
    private SkuLocationKey skuLocationKey;

    // Identity
    private SkuKey skuKey;
    private LotKey lotKey;
    private LocationKey locationKey;
    private LpnKey lpnKey;
    private StorerKey storerKey;
    private WarehouseKey warehouseKey;

    // Quantities
    private Quantity onHandQuantity;
    private Quantity allocatedQuantity;
    private Quantity pickedQuantity;
    private String uom;

    // Lottable Attributes
    private LottableAttributes lottables;

    // Status
    private InventoryStatusCode status;
    private String holdCode;

    // Receipt Information
    private ReceiptKey receiptKey;
    private String receiptLineNumber;
    private LocalDateTime receiptDate;

    // Pack Information
    private String packKey;
    private String packLevel; // PALLET, CASE, INNER_PACK, EACH

    // Audit
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;

    // Concurrency Control
    private int trafficCop;
    private byte[] rowVersion;

    public enum InventoryStatusCode {
        AVAILABLE,      // Available for allocation
        HOLD,           // On hold
        DAMAGED,        // Damaged goods
        QC_PENDING,     // Quality control pending
        IN_TRANSIT,     // Being moved
        ALLOCATED,      // Fully allocated
        PICKED,         // Picked but not shipped
        SHIPPED,        // Shipped (historical)
        QUARANTINE      // Quarantined
    }

    // ═══════════════════════════════════════════════════════════════
    // RECORD-STYLE ACCESSORS (for DDD compatibility)
    // ═══════════════════════════════════════════════════════════════

    public InventoryKey inventoryKey() { return inventoryKey; }
    public SkuKey skuKey() { return skuKey; }
    public LotKey lotKey() { return lotKey; }
    public LocationKey locationKey() { return locationKey; }
    public LpnKey lpnKey() { return lpnKey; }
    public StorerKey storerKey() { return storerKey; }
    public WarehouseKey warehouseKey() { return warehouseKey; }
    public Quantity onHandQuantity() { return onHandQuantity; }
    public Quantity allocatedQuantity() { return allocatedQuantity != null ? allocatedQuantity : Quantity.ZERO; }
    public Quantity pickedQuantity() { return pickedQuantity != null ? pickedQuantity : Quantity.ZERO; }
    public LottableAttributes lottables() { return lottables; }
    public InventoryStatusCode status() { return status; }
    public String holdCode() { return holdCode; }

    // ═══════════════════════════════════════════════════════════════
    // QUANTITY CALCULATIONS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Get available quantity = onHand - allocated - picked.
     */
    public Quantity availableQuantity() {
        return getAvailableQuantity();
    }

    /**
     * Get available quantity = onHand - allocated - picked.
     */
    public Quantity getAvailableQuantity() {
        Quantity allocated = allocatedQuantity != null ? allocatedQuantity : Quantity.ZERO;
        Quantity picked = pickedQuantity != null ? pickedQuantity : Quantity.ZERO;
        return onHandQuantity.subtract(allocated).subtract(picked);
    }

    /**
     * Check if inventory has available quantity for allocation.
     */
    public boolean hasAvailableQuantity() {
        return getAvailableQuantity().isPositive();
    }

    /**
     * Check if inventory can be allocated.
     */
    public boolean isAvailableForAllocation() {
        return status == InventoryStatusCode.AVAILABLE
                && holdCode == null
                && hasAvailableQuantity();
    }

    // ═══════════════════════════════════════════════════════════════
    // STATUS OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Check if inventory is on hold.
     */
    public boolean isOnHold() {
        return status == InventoryStatusCode.HOLD || holdCode != null;
    }

    /**
     * Apply hold to inventory.
     */
    public void applyHold(String holdCode) {
        applyHold(holdCode, null);
    }

    /**
     * Apply hold to inventory with reason.
     */
    public void applyHold(String holdCode, String reason) {
        this.holdCode = holdCode;
        this.status = InventoryStatusCode.HOLD;
        this.updatedAt = Instant.now();
    }

    /**
     * Release hold from inventory.
     */
    public void releaseHold() {
        releaseHold(null);
    }

    /**
     * Release hold from inventory with reason.
     */
    public void releaseHold(String reason) {
        this.holdCode = null;
        this.status = InventoryStatusCode.AVAILABLE;
        this.updatedAt = Instant.now();
    }

    /**
     * Change status.
     */
    public void changeStatus(InventoryStatusCode newStatus) {
        changeStatus(newStatus, null);
    }

    /**
     * Change status with reason.
     */
    public void changeStatus(InventoryStatusCode newStatus, String reason) {
        this.status = newStatus;
        this.updatedAt = Instant.now();
    }

    // ═══════════════════════════════════════════════════════════════
    // ALLOCATION OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Allocate quantity.
     */
    public void allocate(Quantity quantity) {
        allocate(quantity, null, null);
    }

    /**
     * Allocate quantity with order and allocation key tracking.
     */
    public void allocate(Quantity quantity, OrderKey orderKey, AllocationKey allocationKey) {
        if (!isAvailableForAllocation()) {
            throw new IllegalStateException("Inventory is not available for allocation");
        }
        if (quantity.isGreaterThan(getAvailableQuantity())) {
            throw new IllegalArgumentException("Cannot allocate more than available quantity");
        }

        this.allocatedQuantity = (allocatedQuantity != null ? allocatedQuantity : Quantity.ZERO).add(quantity);
        this.updatedAt = Instant.now();
    }

    /**
     * Deallocate quantity.
     */
    public void deallocate(Quantity quantity) {
        deallocate(quantity, null);
    }

    /**
     * Deallocate quantity with reason.
     */
    public void deallocate(Quantity quantity, String reason) {
        if (allocatedQuantity == null || quantity.isGreaterThan(allocatedQuantity)) {
            throw new IllegalArgumentException("Cannot deallocate more than allocated quantity");
        }

        this.allocatedQuantity = allocatedQuantity.subtract(quantity);
        this.updatedAt = Instant.now();
    }

    /**
     * Mark quantity as picked.
     */
    public void pick(Quantity quantity) {
        if (allocatedQuantity == null || quantity.isGreaterThan(allocatedQuantity)) {
            throw new IllegalArgumentException("Cannot pick more than allocated quantity");
        }

        this.allocatedQuantity = allocatedQuantity.subtract(quantity);
        this.pickedQuantity = (pickedQuantity != null ? pickedQuantity : Quantity.ZERO).add(quantity);
        this.updatedAt = Instant.now();
    }

    /**
     * Confirm pick (remove from on-hand).
     */
    public void confirmPick(Quantity quantity) {
        if (pickedQuantity == null || quantity.isGreaterThan(pickedQuantity)) {
            throw new IllegalArgumentException("Cannot confirm more than picked quantity");
        }

        this.pickedQuantity = pickedQuantity.subtract(quantity);
        this.onHandQuantity = onHandQuantity.subtract(quantity);
        this.updatedAt = Instant.now();
    }

    // ═══════════════════════════════════════════════════════════════
    // QUANTITY ADJUSTMENT OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Adjust quantity (positive or negative).
     */
    public void adjustQuantity(Quantity adjustment) {
        this.onHandQuantity = onHandQuantity.add(adjustment);
        this.updatedAt = Instant.now();
    }

    /**
     * Set quantity to specific value.
     */
    public void setQuantity(Quantity newQuantity) {
        this.onHandQuantity = newQuantity;
        this.updatedAt = Instant.now();
    }

    /**
     * Update quantity with reason (for saga operations).
     */
    public void updateQuantity(Quantity newQuantity, String reason) {
        this.onHandQuantity = newQuantity;
        this.updatedAt = Instant.now();
    }

    // ═══════════════════════════════════════════════════════════════
    // ATTRIBUTE OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Update lottable attributes.
     */
    public void updateLottables(LottableAttributes newLottables) {
        this.lottables = newLottables;
        this.updatedAt = Instant.now();
    }

    /**
     * Update lottable attributes with reason.
     */
    public void updateLottables(LottableAttributes newLottables, String reason) {
        this.lottables = newLottables;
        this.updatedAt = Instant.now();
    }

    /**
     * Check if inventory is expired.
     */
    public boolean isExpired() {
        return lottables != null && lottables.isExpired();
    }

    // ═══════════════════════════════════════════════════════════════
    // ADDITIONAL OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Pick with allocation key tracking.
     */
    public void pick(Quantity quantity, AllocationKey allocationKey) {
        if (allocatedQuantity == null || quantity.isGreaterThan(allocatedQuantity)) {
            throw new IllegalArgumentException("Cannot pick more than allocated quantity");
        }

        this.allocatedQuantity = allocatedQuantity.subtract(quantity);
        this.pickedQuantity = (pickedQuantity != null ? pickedQuantity : Quantity.ZERO).add(quantity);
        this.updatedAt = Instant.now();
    }

    /**
     * Confirm shipment.
     */
    public void confirmShipment(Quantity quantity) {
        if (pickedQuantity == null || quantity.isGreaterThan(pickedQuantity)) {
            throw new IllegalArgumentException("Cannot ship more than picked quantity");
        }

        this.pickedQuantity = pickedQuantity.subtract(quantity);
        this.onHandQuantity = onHandQuantity.subtract(quantity);
        this.updatedAt = Instant.now();
    }

    /**
     * Change ownership/storer.
     */
    public void changeOwnership(StorerKey newStorerKey, String reason) {
        this.storerKey = newStorerKey;
        this.updatedAt = Instant.now();
    }

    // ═══════════════════════════════════════════════════════════════
    // STATIC FACTORY METHODS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Create inventory from receipt.
     */
    public static Inventory createFromReceipt(InventoryKey inventoryKey, SkuKey skuKey, LotKey lotKey,
                                               LocationKey locationKey, LpnKey lpnKey, StorerKey storerKey,
                                               WarehouseKey warehouseKey, Quantity quantity,
                                               LottableAttributes lottables) {
        return Inventory.builder()
                .inventoryKey(inventoryKey)
                .skuKey(skuKey)
                .lotKey(lotKey)
                .locationKey(locationKey)
                .lpnKey(lpnKey)
                .storerKey(storerKey)
                .warehouseKey(warehouseKey)
                .onHandQuantity(quantity)
                .allocatedQuantity(Quantity.ZERO)
                .pickedQuantity(Quantity.ZERO)
                .lottables(lottables)
                .status(InventoryStatusCode.AVAILABLE)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .trafficCop(0)
                .build();
    }

    /**
     * Create inventory from return.
     */
    public static Inventory createFromReturn(InventoryKey inventoryKey, SkuKey skuKey, LotKey lotKey,
                                              LocationKey locationKey, LpnKey lpnKey, StorerKey storerKey,
                                              WarehouseKey warehouseKey, Quantity quantity,
                                              LottableAttributes lottables) {
        return Inventory.builder()
                .inventoryKey(inventoryKey)
                .skuKey(skuKey)
                .lotKey(lotKey)
                .locationKey(locationKey)
                .lpnKey(lpnKey)
                .storerKey(storerKey)
                .warehouseKey(warehouseKey)
                .onHandQuantity(quantity)
                .allocatedQuantity(Quantity.ZERO)
                .pickedQuantity(Quantity.ZERO)
                .lottables(lottables)
                .status(InventoryStatusCode.QC_PENDING) // Returns typically need QC
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .trafficCop(0)
                .build();
    }

    // ═══════════════════════════════════════════════════════════════
    // CONCURRENCY CONTROL
    // ═══════════════════════════════════════════════════════════════

    /**
     * Increment traffic cop for optimistic locking.
     */
    public void incrementTrafficCop() {
        this.trafficCop++;
    }

    /**
     * Validate traffic cop for concurrent updates.
     */
    public boolean validateTrafficCop(int expectedVersion) {
        return this.trafficCop == expectedVersion;
    }
}
