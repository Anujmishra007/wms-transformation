package com.maersk.wms.masterdata.domain;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Zone master data entity.
 * Maps to the PUTAWAYZONE table in the WMS database.
 */
@Data
public class Zone {

    private Long id;
    private String zoneCode;
    private String zoneName;
    private String description;
    private ZoneType zoneType;
    private ZoneStatus status;

    // Warehouse
    private String warehouseCode;
    private String buildingCode;
    private String floorCode;

    // Zone configuration
    private int priority;
    private int sequence;
    private String defaultLocationType;
    private String defaultInventoryStatus;

    // Storage characteristics
    private String storageType;
    private boolean temperatureControlled;
    private String temperatureMin;
    private String temperatureMax;
    private String temperatureUom;
    private boolean humidityControlled;
    private String humidityMin;
    private String humidityMax;

    // Operations
    private boolean receivingAllowed;
    private boolean putawayAllowed;
    private boolean pickingAllowed;
    private boolean replenishmentAllowed;
    private boolean cycleCountAllowed;

    // Item restrictions
    private boolean hazmatAllowed;
    private boolean refrigeratedAllowed;
    private boolean oversizedAllowed;
    private String allowedItemTypes;
    private String excludedItemTypes;

    // Capacity
    private int totalLocations;
    private int availableLocations;
    private int occupiedLocations;

    // Equipment
    private String requiredEquipment;
    private boolean forkliftRequired;
    private boolean reachTruckRequired;

    // Custom
    private String customField01;
    private String customField02;

    // Audit
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
