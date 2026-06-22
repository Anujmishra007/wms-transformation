package com.maersk.wms.masterdata.domain.warehouse_structure.model;

import com.maersk.wms.masterdata.shared.kernel.identifiers.*;
import com.maersk.wms.masterdata.shared.kernel.valueobjects.LocationCapacity;

import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Level entity representing a rack level within a bay.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Level {

    private LevelKey levelKey;
    private BayKey bayKey;
    private AisleKey aisleKey;
    private WarehouseKey warehouseKey;

    private String levelName;
    private String levelCode;
    private int levelNumber;

    // Position
    private int heightFromFloorInches;
    private int clearanceInches;

    // Capacity
    private int positionCount; // Number of pallet positions
    private LocationCapacity capacity;

    // Configuration
    private String levelType; // FLOOR, BEAM, TOP
    private boolean groundLevel;
    private boolean requiresEquipment;
    private String requiredEquipment;

    // Status
    private boolean active;

    // Audit
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;

    // Business Methods
    public void activate() {
        this.active = true;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = Instant.now();
    }

    public boolean isAccessibleWithoutEquipment() {
        return groundLevel || !requiresEquipment;
    }

    public BigDecimal getTotalWeightCapacity() {
        if (capacity == null) return null;
        return capacity.maxWeight().multiply(BigDecimal.valueOf(positionCount));
    }
}
