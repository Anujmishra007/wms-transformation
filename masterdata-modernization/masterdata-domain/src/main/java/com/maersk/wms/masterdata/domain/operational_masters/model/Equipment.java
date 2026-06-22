package com.maersk.wms.masterdata.domain.operational_masters.model;

import com.maersk.wms.masterdata.shared.kernel.identifiers.*;

import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Equipment entity representing warehouse equipment (forklifts, RF devices, MHE).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Equipment {

    private EquipmentKey equipmentKey;
    private WarehouseKey warehouseKey;

    private String equipmentCode;
    private String equipmentName;
    private String serialNumber;
    private String assetTag;

    // Type
    private EquipmentType equipmentType;
    private String equipmentCategory; // MHE, RF, SCANNER, PRINTER, CONVEYOR
    private String manufacturer;
    private String model;

    // Specifications
    private BigDecimal liftCapacityLbs;
    private BigDecimal maxHeightInches;
    private String powerType; // ELECTRIC, PROPANE, DIESEL
    private BigDecimal batteryCapacityHours;

    // Location
    private ZoneKey homeZone;
    private LocationKey currentLocation;
    private String parkingSpot;

    // Assignment
    private UserKey assignedUser;
    private String assignedShift;

    // Maintenance
    private LocalDate lastMaintenanceDate;
    private LocalDate nextMaintenanceDate;
    private int maintenanceIntervalDays;
    private int operatingHours;

    // Status
    private EquipmentStatus status;
    private String statusReason;

    // Audit
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;

    public enum EquipmentType {
        FORKLIFT, REACH_TRUCK, ORDER_PICKER, PALLET_JACK, TUGGER,
        RF_DEVICE, SCANNER, PRINTER, CONVEYOR, SORTER, ROBOT
    }

    public enum EquipmentStatus {
        AVAILABLE, IN_USE, CHARGING, MAINTENANCE, OUT_OF_SERVICE, RETIRED
    }

    // Business Methods
    public void makeAvailable() {
        this.status = EquipmentStatus.AVAILABLE;
        this.assignedUser = null;
        this.statusReason = null;
        this.updatedAt = Instant.now();
    }

    public void assignToUser(UserKey userKey) {
        this.status = EquipmentStatus.IN_USE;
        this.assignedUser = userKey;
        this.updatedAt = Instant.now();
    }

    public void returnFromUser() {
        makeAvailable();
    }

    public void startCharging() {
        this.status = EquipmentStatus.CHARGING;
        this.statusReason = "Battery charging";
        this.updatedAt = Instant.now();
    }

    public void sendToMaintenance(String reason) {
        this.status = EquipmentStatus.MAINTENANCE;
        this.statusReason = reason;
        this.lastMaintenanceDate = LocalDate.now();
        this.updatedAt = Instant.now();
    }

    public void retire(String reason) {
        this.status = EquipmentStatus.RETIRED;
        this.statusReason = reason;
        this.updatedAt = Instant.now();
    }

    public void updateLocation(LocationKey location) {
        this.currentLocation = location;
        this.updatedAt = Instant.now();
    }

    public boolean isAvailable() {
        return status == EquipmentStatus.AVAILABLE;
    }

    public boolean needsMaintenance() {
        if (nextMaintenanceDate == null) return false;
        return LocalDate.now().isAfter(nextMaintenanceDate) ||
               LocalDate.now().isEqual(nextMaintenanceDate);
    }

    public boolean isMobileEquipment() {
        return equipmentType == EquipmentType.FORKLIFT ||
               equipmentType == EquipmentType.REACH_TRUCK ||
               equipmentType == EquipmentType.ORDER_PICKER ||
               equipmentType == EquipmentType.PALLET_JACK ||
               equipmentType == EquipmentType.TUGGER;
    }
}
