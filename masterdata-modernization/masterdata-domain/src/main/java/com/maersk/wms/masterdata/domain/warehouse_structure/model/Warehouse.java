package com.maersk.wms.masterdata.domain.warehouse_structure.model;

import com.maersk.wms.masterdata.shared.kernel.identifiers.*;
import com.maersk.wms.masterdata.shared.kernel.valueobjects.*;

import lombok.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Warehouse aggregate root.
 * Represents a physical warehouse facility.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Warehouse {

    private WarehouseKey warehouseKey;
    private String warehouseName;
    private String warehouseCode;

    // Address
    private Address address;

    // Contact
    private ContactInfo contact;

    // Operating Hours
    private OperatingHours operatingHours;

    // Configuration
    private String timezone;
    private String countryCode;
    private String regionCode;
    private String currency;

    // Capacity
    private int totalLocations;
    private int activeLocations;
    private int totalSquareFeet;

    // Status
    private WarehouseStatus status;

    // Child Entities
    @Builder.Default
    private List<Zone> zones = new ArrayList<>();
    @Builder.Default
    private List<Aisle> aisles = new ArrayList<>();

    // Audit
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;

    public enum WarehouseStatus {
        ACTIVE, INACTIVE, UNDER_CONSTRUCTION, CLOSED
    }

    // Business Methods
    public void activate() {
        this.status = WarehouseStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.status = WarehouseStatus.INACTIVE;
        this.updatedAt = Instant.now();
    }

    public void close() {
        this.status = WarehouseStatus.CLOSED;
        this.updatedAt = Instant.now();
    }

    public void addZone(Zone zone) {
        this.zones.add(zone);
        this.updatedAt = Instant.now();
    }

    public void addAisle(Aisle aisle) {
        this.aisles.add(aisle);
        this.updatedAt = Instant.now();
    }

    public double getUtilization() {
        if (totalLocations == 0) return 0.0;
        return (double) activeLocations / totalLocations * 100;
    }

    public boolean isActive() {
        return status == WarehouseStatus.ACTIVE;
    }
}
