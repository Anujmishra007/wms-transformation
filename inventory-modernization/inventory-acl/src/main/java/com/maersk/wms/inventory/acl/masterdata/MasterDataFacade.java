package com.maersk.wms.inventory.acl.masterdata;

import com.maersk.wms.inventory.shared.kernel.identifiers.*;
import com.maersk.wms.inventory.shared.kernel.valueobjects.*;

import java.util.List;
import java.util.Optional;

/**
 * Anti-Corruption Layer facade for Master Data Service.
 * Provides SKU, Location, Storer, and configuration data to inventory domain.
 */
public interface MasterDataFacade {

    // ═══════════════════════════════════════════════════════════════
    // SKU QUERIES
    // ═══════════════════════════════════════════════════════════════

    /**
     * Get SKU details.
     */
    Optional<SkuInfo> getSkuInfo(SkuKey skuKey);

    /**
     * Get SKU by code.
     */
    Optional<SkuInfo> getSkuByCode(String skuCode, StorerKey storerKey);

    /**
     * Get SKUs for storer.
     */
    List<SkuInfo> getSkusForStorer(StorerKey storerKey);

    /**
     * Check if SKU exists.
     */
    boolean skuExists(SkuKey skuKey);

    /**
     * Get SKU pack configuration.
     */
    Optional<SkuPackInfo> getSkuPackInfo(SkuKey skuKey);

    // ═══════════════════════════════════════════════════════════════
    // LOCATION QUERIES
    // ═══════════════════════════════════════════════════════════════

    /**
     * Get location details.
     */
    Optional<LocationInfo> getLocationInfo(LocationKey locationKey);

    /**
     * Get location by code.
     */
    Optional<LocationInfo> getLocationByCode(String locationCode, WarehouseKey warehouseKey);

    /**
     * Get locations in zone.
     */
    List<LocationInfo> getLocationsInZone(String zoneCode, WarehouseKey warehouseKey);

    /**
     * Check if location exists.
     */
    boolean locationExists(LocationKey locationKey);

    /**
     * Check if location can accept inventory.
     */
    boolean canAcceptInventory(LocationKey locationKey, SkuKey skuKey, Quantity quantity);

    /**
     * Get location capacity.
     */
    LocationCapacity getLocationCapacity(LocationKey locationKey);

    // ═══════════════════════════════════════════════════════════════
    // STORER QUERIES
    // ═══════════════════════════════════════════════════════════════

    /**
     * Get storer details.
     */
    Optional<StorerInfo> getStorerInfo(StorerKey storerKey);

    /**
     * Get storer by code.
     */
    Optional<StorerInfo> getStorerByCode(String storerCode);

    /**
     * Check if storer exists.
     */
    boolean storerExists(StorerKey storerKey);

    /**
     * Get storer inventory settings.
     */
    Optional<StorerInventorySettings> getStorerInventorySettings(StorerKey storerKey);

    // ═══════════════════════════════════════════════════════════════
    // WAREHOUSE QUERIES
    // ═══════════════════════════════════════════════════════════════

    /**
     * Get warehouse details.
     */
    Optional<WarehouseInfo> getWarehouseInfo(WarehouseKey warehouseKey);

    /**
     * Get warehouse by code.
     */
    Optional<WarehouseInfo> getWarehouseByCode(String warehouseCode);

    /**
     * Get warehouse inventory settings.
     */
    Optional<WarehouseInventorySettings> getWarehouseInventorySettings(WarehouseKey warehouseKey);

    // ═══════════════════════════════════════════════════════════════
    // UOM QUERIES
    // ═══════════════════════════════════════════════════════════════

    /**
     * Get UOM conversion factor.
     */
    double getUomConversionFactor(String fromUom, String toUom, SkuKey skuKey);

    /**
     * Convert quantity to base UOM.
     */
    Quantity convertToBaseUom(Quantity quantity, SkuKey skuKey);

    /**
     * Get valid UOMs for SKU.
     */
    List<String> getValidUoms(SkuKey skuKey);

    // ═══════════════════════════════════════════════════════════════
    // LOTTABLE CONFIGURATION
    // ═══════════════════════════════════════════════════════════════

    /**
     * Get lottable configuration for storer.
     */
    Optional<LottableConfig> getLottableConfig(StorerKey storerKey);

    /**
     * Get lottable labels (display names).
     */
    List<String> getLottableLabels(StorerKey storerKey);

    // ═══════════════════════════════════════════════════════════════
    // DTOs
    // ═══════════════════════════════════════════════════════════════

    record SkuInfo(
            SkuKey skuKey,
            String skuCode,
            String description,
            StorerKey storerKey,
            String baseUom,
            double stdCost,
            double stdWeight,
            String weightUom,
            double stdCube,
            String cubeUom,
            boolean lotControlled,
            boolean serialControlled,
            boolean expiryControlled,
            int shelfLife,
            String abcClass,
            String velocityCode
    ) {}

    record SkuPackInfo(
            SkuKey skuKey,
            String packCode,
            String packUom,
            int unitsPerPack,
            int packsPerCase,
            int casesPerPallet,
            double packWeight,
            double packCube
    ) {}

    record LocationInfo(
            LocationKey locationKey,
            String locationCode,
            WarehouseKey warehouseKey,
            String zoneCode,
            String aisle,
            String bay,
            String level,
            String locationType,
            boolean pickable,
            boolean putawayable,
            boolean active,
            double maxWeight,
            double maxCube,
            int maxPallets
    ) {}

    record LocationCapacity(
            LocationKey locationKey,
            double maxWeight,
            double currentWeight,
            double availableWeight,
            double maxCube,
            double currentCube,
            double availableCube,
            int maxPallets,
            int currentPallets,
            int availablePallets
    ) {}

    record StorerInfo(
            StorerKey storerKey,
            String storerCode,
            String storerName,
            String storerType,
            boolean active
    ) {}

    record StorerInventorySettings(
            StorerKey storerKey,
            String fifoStrategy,
            boolean allowNegativeInventory,
            boolean requireLotOnReceipt,
            boolean requireLpnOnReceipt,
            boolean autoAllocate,
            double varianceThresholdPercent,
            double varianceThresholdValue
    ) {}

    record WarehouseInfo(
            WarehouseKey warehouseKey,
            String warehouseCode,
            String warehouseName,
            String timezone,
            String country,
            boolean active
    ) {}

    record WarehouseInventorySettings(
            WarehouseKey warehouseKey,
            String defaultFifoStrategy,
            boolean allowMixedLots,
            boolean allowMixedSkus,
            boolean requireCycleCount,
            int cycleCountFrequencyDays,
            String inventoryValuationMethod
    ) {}

    record LottableConfig(
            StorerKey storerKey,
            List<LottableFieldConfig> fields
    ) {}

    record LottableFieldConfig(
            int fieldNumber,
            String label,
            String dataType,
            boolean required,
            boolean searchable,
            boolean displayOnReceipt,
            boolean displayOnPick
    ) {}
}
