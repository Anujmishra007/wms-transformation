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
    // QUANTITY CALCULATIONS
    // ═══════════════════════════════════════════════════════════════

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
        this.holdCode = holdCode;
        this.status = InventoryStatusCode.HOLD;
        this.updatedAt = Instant.now();
    }

    /**
     * Release hold from inventory.
     */
    public void releaseHold() {
        this.holdCode = null;
        this.status = InventoryStatusCode.AVAILABLE;
        this.updatedAt = Instant.now();
    }

    /**
     * Change status.
     */
    public void changeStatus(InventoryStatusCode newStatus) {
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
     * Check if inventory is expired.
     */
    public boolean isExpired() {
        return lottables != null && lottables.isExpired();
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
