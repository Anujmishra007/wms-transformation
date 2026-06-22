package com.maersk.wms.masterdata.domain.operational_masters.model;

import com.maersk.wms.masterdata.shared.kernel.identifiers.*;
import com.maersk.wms.masterdata.shared.kernel.valueobjects.ContactInfo;

import lombok.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User entity representing warehouse personnel.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private UserKey userKey;
    private WarehouseKey homeWarehouse;

    // Identity
    private String userId;
    private String userName;
    private String firstName;
    private String lastName;
    private String badgeNumber;

    // Contact
    private ContactInfo contact;

    // Employment
    private String employeeId;
    private String department;
    private String jobTitle;
    private String supervisor;
    private LocalDate hireDate;
    private String employmentType; // FULL_TIME, PART_TIME, TEMPORARY, CONTRACT

    // Shift & Schedule
    private String shift; // DAY, SWING, NIGHT
    private String scheduleGroup;
    @Builder.Default
    private List<ZoneKey> assignedZones = new ArrayList<>();

    // Skills & Certifications
    @Builder.Default
    private Map<String, Integer> skillLevels = new HashMap<>(); // Skill -> Level (1-100)
    @Builder.Default
    private List<String> certifications = new ArrayList<>();
    private LocalDate certificationExpiryDate;

    // Equipment Authorization
    @Builder.Default
    private List<Equipment.EquipmentType> authorizedEquipment = new ArrayList<>();

    // Access
    @Builder.Default
    private List<String> roles = new ArrayList<>();
    @Builder.Default
    private List<String> permissions = new ArrayList<>();
    private String languagePreference;

    // Current State
    private UserStatus status;
    private EquipmentKey currentEquipment;
    private ZoneKey currentZone;
    private LocationKey currentLocation;
    private String currentTaskId;

    // Metrics
    private int tasksCompletedToday;
    private int unitsProcessedToday;
    private double productivityScore;

    // Audit
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private Instant lastLoginAt;

    public enum UserStatus {
        ACTIVE, INACTIVE, ON_BREAK, LOGGED_OFF, SUSPENDED, TERMINATED
    }

    // Business Methods
    public void activate() {
        this.status = UserStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.status = UserStatus.INACTIVE;
        this.updatedAt = Instant.now();
    }

    public void logIn() {
        this.status = UserStatus.ACTIVE;
        this.lastLoginAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void logOff() {
        this.status = UserStatus.LOGGED_OFF;
        this.currentEquipment = null;
        this.currentTaskId = null;
        this.updatedAt = Instant.now();
    }

    public void goOnBreak() {
        this.status = UserStatus.ON_BREAK;
        this.updatedAt = Instant.now();
    }

    public void returnFromBreak() {
        this.status = UserStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void suspend(String reason) {
        this.status = UserStatus.SUSPENDED;
        this.updatedAt = Instant.now();
    }

    public void terminate() {
        this.status = UserStatus.TERMINATED;
        this.updatedAt = Instant.now();
    }

    public void assignEquipment(EquipmentKey equipmentKey) {
        this.currentEquipment = equipmentKey;
        this.updatedAt = Instant.now();
    }

    public void returnEquipment() {
        this.currentEquipment = null;
        this.updatedAt = Instant.now();
    }

    public void assignToZone(ZoneKey zoneKey) {
        if (!assignedZones.contains(zoneKey)) {
            this.assignedZones.add(zoneKey);
            this.updatedAt = Instant.now();
        }
    }

    public void removeFromZone(ZoneKey zoneKey) {
        this.assignedZones.remove(zoneKey);
        this.updatedAt = Instant.now();
    }

    public void updateLocation(ZoneKey zone, LocationKey location) {
        this.currentZone = zone;
        this.currentLocation = location;
        this.updatedAt = Instant.now();
    }

    public void setSkillLevel(String skill, int level) {
        this.skillLevels.put(skill, Math.min(100, Math.max(1, level)));
        this.updatedAt = Instant.now();
    }

    public int getSkillLevel(String skill) {
        return skillLevels.getOrDefault(skill, 0);
    }

    public void addCertification(String certification) {
        if (!certifications.contains(certification)) {
            this.certifications.add(certification);
            this.updatedAt = Instant.now();
        }
    }

    public void authorizeEquipment(Equipment.EquipmentType equipmentType) {
        if (!authorizedEquipment.contains(equipmentType)) {
            this.authorizedEquipment.add(equipmentType);
            this.updatedAt = Instant.now();
        }
    }

    public boolean isAuthorizedForEquipment(Equipment.EquipmentType equipmentType) {
        return authorizedEquipment.contains(equipmentType);
    }

    public boolean isAvailable() {
        return status == UserStatus.ACTIVE && currentTaskId == null;
    }

    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    public String getFullName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }

    public void completeTask() {
        this.tasksCompletedToday++;
        this.currentTaskId = null;
        this.updatedAt = Instant.now();
    }

    public void processUnits(int units) {
        this.unitsProcessedToday += units;
        this.updatedAt = Instant.now();
    }
}
