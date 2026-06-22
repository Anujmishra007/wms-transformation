package com.maersk.wms.inventory.domain.lifecycle.service;

import com.maersk.wms.inventory.domain.core.model.Inventory;
import com.maersk.wms.inventory.domain.lifecycle.model.*;
import com.maersk.wms.inventory.shared.kernel.identifiers.*;
import com.maersk.wms.inventory.shared.kernel.valueobjects.*;

import java.util.List;

/**
 * Service interface for inventory lifecycle operations.
 * Handles Create, Change, Remove, and Finalization.
 */
public interface InventoryLifecycleService {

    // ═══════════════════════════════════════════════════════════════
    // CREATE OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Create inventory from receipt.
     */
    Inventory createFromReceipt(InventoryCreation creation);

    /**
     * Create inventory from return.
     */
    Inventory createFromReturn(InventoryCreation creation);

    /**
     * Create inventory from adjustment.
     */
    Inventory createFromAdjustment(SkuKey skuKey, LotKey lotKey, LocationKey locationKey,
                                    LpnKey lpnKey, Quantity quantity, LottableAttributes lottables,
                                    String reason, UserKey createdBy, WarehouseKey warehouseKey);

    /**
     * Create inventory from crossdock.
     */
    Inventory createFromCrossdock(InventoryCreation creation);

    /**
     * Batch create inventory records.
     */
    List<Inventory> createBatch(List<InventoryCreation> creations);

    // ═══════════════════════════════════════════════════════════════
    // CHANGE OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Update inventory quantity.
     */
    void updateQuantity(InventoryKey inventoryKey, Quantity newQuantity,
                        String reason, UserKey updatedBy);

    /**
     * Adjust inventory quantity (positive or negative).
     */
    void adjustQuantity(InventoryKey inventoryKey, Quantity adjustment,
                        String adjustmentType, String reason, UserKey adjustedBy);

    /**
     * Update inventory status.
     */
    void updateStatus(InventoryChange change);

    /**
     * Update lottable attributes.
     */
    void updateLottables(InventoryKey inventoryKey, LottableAttributes newLottables,
                         String reason, UserKey updatedBy);

    /**
     * Change inventory ownership.
     */
    void changeOwnership(InventoryKey inventoryKey, StorerKey newStorerKey,
                         String reason, UserKey changedBy);

    /**
     * Apply hold.
     */
    void applyHold(InventoryKey inventoryKey, String holdCode, String reason, UserKey appliedBy);

    /**
     * Release hold.
     */
    void releaseHold(InventoryKey inventoryKey, String reason, UserKey releasedBy);

    // ═══════════════════════════════════════════════════════════════
    // REMOVE OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Remove inventory (consumption).
     */
    void remove(InventoryRemoval removal);

    /**
     * Pick inventory for order.
     */
    void pick(InventoryKey inventoryKey, AllocationKey allocationKey, Quantity quantity,
              UserKey pickedBy);

    /**
     * Confirm shipment (final removal).
     */
    void confirmShipment(List<InventoryKey> inventoryKeys, String shipmentKey,
                         UserKey confirmedBy);

    /**
     * Write off inventory.
     */
    void writeOff(InventoryKey inventoryKey, String writeOffReason, UserKey writtenOffBy);

    /**
     * Delete inventory record (zero quantity cleanup).
     */
    void delete(InventoryKey inventoryKey, String reason, UserKey deletedBy);

    // ═══════════════════════════════════════════════════════════════
    // FINALIZATION OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Finalize inventory transaction.
     */
    void finalizeTransaction(InventoryFinalization finalization);

    /**
     * Reconcile inventory at location.
     */
    InventoryFinalization reconcileLocation(LocationKey locationKey, WarehouseKey warehouseKey,
                                             UserKey reconciledBy);

    /**
     * Period-end close.
     */
    InventoryFinalization periodEndClose(WarehouseKey warehouseKey, String period, UserKey closedBy);
}
