package com.maersk.wms.masterdata.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Location master data entity.
 * Maps to the LOC table in the WMS database.
 */
@Data
public class Location {

    private Long id;
    private String locationCode;
    private String description;

    // Location classification
    private LocationType locationType;
    private LocationStatus status;
    private String zone;
    private String area;
    private String aisle;
    private String bay;
    private String level;
    private String position;

    // Physical dimensions
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;
    private String dimensionUom;
    private BigDecimal maxWeight;
    private String weightUom;
    private BigDecimal maxCube;
    private String cubeUom;

    // Capacity
    private int maxPallets;
    private int maxCases;
    private int maxEaches;
    private int currentPallets;
    private int currentCases;
    private int currentEaches;
    private BigDecimal percentFull;

    // Pick path
    private int pickPathSequence;
    private int putawaySequence;
    private int cycleCountSequence;

    // Handling
    private boolean pickLocation;
    private boolean putawayLocation;
    private boolean replenishmentSource;
    private boolean replenishmentTarget;
    private boolean stagingLocation;
    private boolean receivingLocation;
    private boolean shippingLocation;
    private boolean qcLocation;
    private boolean damageLocation;
    private boolean returnLocation;

    // Control
    private boolean mixedSku;
    private boolean mixedLot;
    private boolean mixedStatus;
    private String defaultInventoryStatus;
    private String locationCategory;
    private String storageType;

    // ABC classification
    private String abcClass;
    private String velocityCode;

    // Equipment
    private String handlingEquipment;
    private boolean forkliftRequired;
    private boolean ladderRequired;

    // Coordinates (for visualization/automation)
    private BigDecimal xCoordinate;
    private BigDecimal yCoordinate;
    private BigDecimal zCoordinate;

    // Audit
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
