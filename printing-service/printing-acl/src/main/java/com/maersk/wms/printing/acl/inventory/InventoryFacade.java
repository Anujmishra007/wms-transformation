package com.maersk.wms.printing.acl.inventory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Anti-Corruption Layer facade for Inventory Service.
 * Provides inventory data for label generation.
 */
public interface InventoryFacade {

    /**
     * Get LPN (License Plate Number) details for label generation.
     */
    Optional<LpnLabelData> getLpnDetails(String lpnNumber, String warehouseKey);

    /**
     * Get location details for label generation.
     */
    Optional<LocationLabelData> getLocationDetails(String locationCode, String warehouseKey);

    /**
     * Get SKU details for label generation.
     */
    Optional<SkuLabelData> getSkuDetails(String skuCode, String storerKey);

    /**
     * Get lot details for label generation.
     */
    Optional<LotLabelData> getLotDetails(String lotNumber, String storerKey);

    /**
     * Get inventory details for a specific LPN at location.
     */
    Optional<InventoryLabelData> getInventoryDetails(String lpnNumber, String locationCode, String warehouseKey);

    /**
     * Get all LPNs at a location for bulk label generation.
     */
    List<LpnLabelData> getLpnsAtLocation(String locationCode, String warehouseKey);

    // DTOs for inventory data
    record LpnLabelData(
            String lpnNumber,
            String lpnType,
            String status,
            String locationCode,
            String skuCode,
            String storerKey,
            double quantity,
            String uom,
            Map<String, String> attributes
    ) {}

    record LocationLabelData(
            String locationCode,
            String locationType,
            String zone,
            String aisle,
            String bay,
            String level,
            String position,
            String checkDigit,
            Map<String, String> attributes
    ) {}

    record SkuLabelData(
            String skuCode,
            String description,
            String storerKey,
            String upc,
            String gtin,
            double weight,
            String weightUom,
            Map<String, String> attributes
    ) {}

    record LotLabelData(
            String lotNumber,
            String skuCode,
            String storerKey,
            java.time.LocalDate manufactureDate,
            java.time.LocalDate expiryDate,
            Map<String, String> lottables
    ) {}

    record InventoryLabelData(
            String lpnNumber,
            String locationCode,
            String skuCode,
            String storerKey,
            String lotNumber,
            double quantity,
            String uom,
            String status,
            Map<String, String> attributes
    ) {}
}
