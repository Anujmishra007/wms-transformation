package com.maersk.wms.printing.acl.masterdata;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Anti-Corruption Layer facade for Master Data Service.
 * Provides warehouse, storer, and configuration data for label generation.
 */
public interface MasterDataFacade {

    /**
     * Get warehouse details for labels.
     */
    Optional<WarehouseData> getWarehouseDetails(String warehouseKey);

    /**
     * Get storer details for storer-specific labels.
     */
    Optional<StorerData> getStorerDetails(String storerKey);

    /**
     * Get zone details for zone labels.
     */
    Optional<ZoneData> getZoneDetails(String zoneKey, String warehouseKey);

    /**
     * Get dock details for dock labels.
     */
    Optional<DockData> getDockDetails(String dockKey, String warehouseKey);

    /**
     * Get all zones for a warehouse.
     */
    List<ZoneData> getZonesForWarehouse(String warehouseKey);

    /**
     * Get all docks for a warehouse.
     */
    List<DockData> getDocksForWarehouse(String warehouseKey);

    /**
     * Get carrier details for shipping labels.
     */
    Optional<CarrierData> getCarrierDetails(String carrierCode);

    /**
     * Get equipment details for equipment labels.
     */
    Optional<EquipmentData> getEquipmentDetails(String equipmentKey, String warehouseKey);

    // DTOs for master data
    record WarehouseData(
            String warehouseKey,
            String warehouseCode,
            String warehouseName,
            AddressData address,
            String timeZone,
            String companyCode,
            String companyName,
            Map<String, String> attributes
    ) {}

    record StorerData(
            String storerKey,
            String storerCode,
            String storerName,
            String storerType,
            AddressData address,
            String contactName,
            String contactPhone,
            String contactEmail,
            Map<String, String> attributes
    ) {}

    record ZoneData(
            String zoneKey,
            String zoneCode,
            String zoneName,
            String zoneType,
            String warehouseKey,
            int locationCount,
            Map<String, String> attributes
    ) {}

    record DockData(
            String dockKey,
            String dockCode,
            String dockName,
            String dockType,
            String warehouseKey,
            String zoneKey,
            Map<String, String> attributes
    ) {}

    record CarrierData(
            String carrierCode,
            String carrierName,
            String carrierType,
            String trackingUrlPattern,
            Map<String, String> attributes
    ) {}

    record EquipmentData(
            String equipmentKey,
            String equipmentCode,
            String equipmentName,
            String equipmentType,
            String serialNumber,
            String warehouseKey,
            String zoneKey,
            Map<String, String> attributes
    ) {}

    record AddressData(
            String address1,
            String address2,
            String address3,
            String city,
            String state,
            String postalCode,
            String country
    ) {}
}
