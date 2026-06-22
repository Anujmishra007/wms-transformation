package com.maersk.wms.masterdata.domain.warehouse_structure.model;

import com.maersk.wms.masterdata.shared.kernel.identifiers.*;

import lombok.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Aisle entity representing a warehouse aisle.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Aisle {

    private AisleKey aisleKey;
    private WarehouseKey warehouseKey;
    private ZoneKey zoneKey;

    private String aisleName;
    private String aisleCode;

    // Configuration
    private int aisleNumber;
    private String aisleType; // SINGLE, DOUBLE, NARROW
    private String direction; // UNIDIRECTIONAL, BIDIRECTIONAL
    private int widthInches;

    // Sequence
    private int pickSequenceStart;
    private int pickSequenceEnd;

    // Equipment Requirements
    private String equipmentType; // REACH_TRUCK, FORKLIFT, ORDER_PICKER
    private boolean narrowAisle;

    // Child Entities
    @Builder.Default
    private List<Bay> bays = new ArrayList<>();

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

    public void addBay(Bay bay) {
        this.bays.add(bay);
        this.updatedAt = Instant.now();
    }

    public int getBayCount() {
        return bays.size();
    }
}
