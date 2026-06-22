package com.maersk.wms.inventory.acl.packing;

import com.maersk.wms.inventory.shared.kernel.identifiers.*;
import com.maersk.wms.inventory.shared.kernel.valueobjects.*;

import java.util.List;
import java.util.Optional;

/**
 * Anti-Corruption Layer facade for Packing Operations Service.
 * Translates packing domain concepts to inventory domain.
 * Upstream service providing pack completion data.
 */
public interface PackingOperationsFacade {

    // ═══════════════════════════════════════════════════════════════
    // PACK QUERIES
    // ═══════════════════════════════════════════════════════════════

    /**
     * Get pack details.
     */
    Optional<PackInfo> getPackInfo(String packKey);

    /**
     * Get pack contents.
     */
    List<PackContentInfo> getPackContents(String packKey);

    /**
     * Get packs for order.
     */
    List<PackInfo> getPacksForOrder(OrderKey orderKey);

    /**
     * Check if pack is complete.
     */
    boolean isPackComplete(String packKey);

    // ═══════════════════════════════════════════════════════════════
    // CARTON QUERIES
    // ═══════════════════════════════════════════════════════════════

    /**
     * Get carton details.
     */
    Optional<CartonInfo> getCartonInfo(LpnKey cartonLpn);

    /**
     * Get carton contents.
     */
    List<CartonContentInfo> getCartonContents(LpnKey cartonLpn);

    /**
     * Get cartons on pallet.
     */
    List<LpnKey> getCartonsOnPallet(LpnKey palletLpn);

    // ═══════════════════════════════════════════════════════════════
    // INVENTORY CONSUMPTION
    // ═══════════════════════════════════════════════════════════════

    /**
     * Get inventory consumed by pack.
     */
    List<InventoryConsumption> getInventoryConsumedByPack(String packKey);

    /**
     * Get source inventory for carton.
     */
    List<InventoryKey> getSourceInventoryForCarton(LpnKey cartonLpn);

    // ═══════════════════════════════════════════════════════════════
    // NOTIFICATION CALLBACKS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Notify packing that inventory was moved to carton.
     */
    void notifyInventoryMovedToCarton(LpnKey cartonLpn, InventoryKey inventoryKey, Quantity quantity);

    /**
     * Notify packing that carton is ready for ship.
     */
    void notifyCartonReadyForShip(LpnKey cartonLpn);

    // ═══════════════════════════════════════════════════════════════
    // DTOs
    // ═══════════════════════════════════════════════════════════════

    record PackInfo(
            String packKey,
            OrderKey orderKey,
            StorerKey storerKey,
            WarehouseKey warehouseKey,
            String packStation,
            String status,
            int totalCartons,
            int completedCartons
    ) {}

    record PackContentInfo(
            String packKey,
            String lineNumber,
            SkuKey skuKey,
            Quantity packedQuantity,
            LpnKey cartonLpn,
            InventoryKey sourceInventoryKey
    ) {}

    record CartonInfo(
            LpnKey cartonLpn,
            String cartonType,
            OrderKey orderKey,
            String packKey,
            String status,
            double weight,
            String weightUom,
            String dimensions
    ) {}

    record CartonContentInfo(
            LpnKey cartonLpn,
            SkuKey skuKey,
            Quantity quantity,
            LotKey lotKey,
            InventoryKey sourceInventoryKey
    ) {}

    record InventoryConsumption(
            InventoryKey inventoryKey,
            SkuKey skuKey,
            LotKey lotKey,
            LocationKey fromLocation,
            Quantity consumedQuantity,
            LpnKey targetCarton
    ) {}
}
