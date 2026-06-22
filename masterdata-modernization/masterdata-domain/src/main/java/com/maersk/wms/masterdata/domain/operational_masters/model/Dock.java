package com.maersk.wms.masterdata.domain.operational_masters.model;

import com.maersk.wms.masterdata.shared.kernel.identifiers.*;
import com.maersk.wms.masterdata.shared.kernel.valueobjects.OperatingHours;

import lombok.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Dock entity representing a receiving/shipping dock door.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dock {

    private DockKey dockKey;
    private WarehouseKey warehouseKey;
    private ZoneKey zoneKey;

    private String dockName;
    private String dockCode;
    private int dockNumber;

    // Type
    private DockType dockType;
    private boolean canReceive;
    private boolean canShip;

    // Physical Configuration
    private int heightInches;
    private int widthInches;
    private boolean hasDockLeveler;
    private boolean hasShorepower;
    private boolean temperatureControlled;
    private String temperatureClass;

    // Operating Hours
    private OperatingHours operatingHours;

    // Assignment
    private String assignedCarrier;
    private String assignedRoute;
    @Builder.Default
    private List<LaneKey> assignedLanes = new ArrayList<>();

    // Current Status
    private DockStatus status;
    private String currentTrailerId;
    private String currentAppointmentId;
    private LocalDateTime appointmentStartTime;
    private LocalDateTime appointmentEndTime;

    // Metrics
    private int appointmentsToday;
    private int completedToday;

    // Audit
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;

    public enum DockType {
        RECEIVING, SHIPPING, BOTH, CROSSDOCK
    }

    public enum DockStatus {
        AVAILABLE, OCCUPIED, RESERVED, BLOCKED, MAINTENANCE
    }

    // Business Methods
    public void makeAvailable() {
        this.status = DockStatus.AVAILABLE;
        this.currentTrailerId = null;
        this.currentAppointmentId = null;
        this.updatedAt = Instant.now();
    }

    public void occupy(String trailerId, String appointmentId) {
        this.status = DockStatus.OCCUPIED;
        this.currentTrailerId = trailerId;
        this.currentAppointmentId = appointmentId;
        this.updatedAt = Instant.now();
    }

    public void reserve(String appointmentId, LocalDateTime start, LocalDateTime end) {
        this.status = DockStatus.RESERVED;
        this.currentAppointmentId = appointmentId;
        this.appointmentStartTime = start;
        this.appointmentEndTime = end;
        this.updatedAt = Instant.now();
    }

    public void block(String reason) {
        this.status = DockStatus.BLOCKED;
        this.updatedAt = Instant.now();
    }

    public void assignLane(LaneKey laneKey) {
        if (!assignedLanes.contains(laneKey)) {
            this.assignedLanes.add(laneKey);
            this.updatedAt = Instant.now();
        }
    }

    public void unassignLane(LaneKey laneKey) {
        this.assignedLanes.remove(laneKey);
        this.updatedAt = Instant.now();
    }

    public void completeAppointment() {
        this.completedToday++;
        makeAvailable();
    }

    public boolean isAvailable() {
        return status == DockStatus.AVAILABLE;
    }

    public boolean canAcceptAppointment(LocalDateTime start, LocalDateTime end) {
        if (status == DockStatus.BLOCKED || status == DockStatus.MAINTENANCE) {
            return false;
        }
        if (status == DockStatus.RESERVED || status == DockStatus.OCCUPIED) {
            // Check for overlap
            if (appointmentEndTime != null && start.isBefore(appointmentEndTime)) {
                return false;
            }
        }
        return true;
    }
}
