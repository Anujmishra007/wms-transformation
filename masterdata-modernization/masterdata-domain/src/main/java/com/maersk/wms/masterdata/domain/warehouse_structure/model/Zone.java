package com.maersk.wms.masterdata.domain.warehouse_structure.model;

import com.maersk.wms.masterdata.shared.kernel.identifiers.*;

import lombok.*;
import java.time.Instant;

/**
 * Zone entity representing a logical area within a warehouse.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Zone {

    private ZoneKey zoneKey;
    private WarehouseKey warehouseKey;

    private String zoneName;
    private String zoneCode;
    private ZoneType zoneType;

    // Configuration
    private String storageType; // BULK, RACK, FLOOR, COLD, etc.
    private String temperatureClass; // AMBIENT, CHILLED, FROZEN
    private String handlingType; // STANDARD, HAZMAT, HIGH_VALUE

    // Capacity
    private int totalLocations;
    private int availableLocations;

    // Picking Configuration
    private String pickStrategy;
    private int pickSequence;

    // Status
    private boolean active;

    // Audit
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;

    public enum ZoneType {
        RECEIVING, STAGING, STORAGE, PICKING, PACKING, SHIPPING, RETURNS, QUALITY, DAMAGED
    }

    // Business Methods
    public void activate() {
        this.active = true;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = Instant.now();
    }

    public double getUtilization() {
        if (totalLocations == 0) return 0.0;
        return (double) (totalLocations - availableLocations) / totalLocations * 100;
    }

    public boolean hasCapacity() {
        return availableLocations > 0;
    }
}
