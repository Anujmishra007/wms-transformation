package com.maersk.wms.masterdata.domain.operational_masters.model;

import com.maersk.wms.masterdata.shared.kernel.identifiers.*;
import com.maersk.wms.masterdata.shared.kernel.valueobjects.LocationCapacity;

import lombok.*;
import java.time.Instant;

/**
 * Lane entity representing a staging/processing lane in the warehouse.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lane {

    private LaneKey laneKey;
    private WarehouseKey warehouseKey;
    private ZoneKey zoneKey;

    private String laneName;
    private String laneCode;

    // Type
    private LaneType laneType;
    private String laneFunction; // RECEIVING, SHIPPING, STAGING, SORTING

    // Position
    private int laneNumber;
    private String side; // A, B
    private int xCoordinate;
    private int yCoordinate;

    // Capacity
    private LocationCapacity capacity;
    private int maxPallets;
    private int currentPallets;

    // Configuration
    private boolean conveyorConnected;
    private String conveyorId;
    private String sorterId;

    // Assignment
    private DockKey assignedDock;
    private String assignedCarrier;
    private String assignedRoute;

    // Status
    private LaneStatus status;
    private boolean available;

    // Audit
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;

    public enum LaneType {
        STAGING, RECEIVING, SHIPPING, SORTING, BUFFER, CROSSDOCK, RETURNS
    }

    public enum LaneStatus {
        AVAILABLE, OCCUPIED, RESERVED, BLOCKED, MAINTENANCE
    }

    // Business Methods
    public void makeAvailable() {
        this.status = LaneStatus.AVAILABLE;
        this.available = true;
        this.updatedAt = Instant.now();
    }

    public void occupy() {
        this.status = LaneStatus.OCCUPIED;
        this.available = false;
        this.updatedAt = Instant.now();
    }

    public void reserve(String reservedFor) {
        this.status = LaneStatus.RESERVED;
        this.available = false;
        this.updatedAt = Instant.now();
    }

    public void block(String reason) {
        this.status = LaneStatus.BLOCKED;
        this.available = false;
        this.updatedAt = Instant.now();
    }

    public void assignToDock(DockKey dockKey) {
        this.assignedDock = dockKey;
        this.updatedAt = Instant.now();
    }

    public void unassignFromDock() {
        this.assignedDock = null;
        this.updatedAt = Instant.now();
    }

    public boolean hasCapacity() {
        return currentPallets < maxPallets;
    }

    public int getAvailableCapacity() {
        return maxPallets - currentPallets;
    }
}
