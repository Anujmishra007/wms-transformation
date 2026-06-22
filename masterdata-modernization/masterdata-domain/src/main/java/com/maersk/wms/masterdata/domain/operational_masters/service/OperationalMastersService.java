package com.maersk.wms.masterdata.domain.operational_masters.service;

import com.maersk.wms.masterdata.domain.operational_masters.model.*;
import com.maersk.wms.masterdata.shared.kernel.identifiers.*;
import com.maersk.wms.masterdata.shared.kernel.valueobjects.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Operational Masters Service - manages storers, lanes, docks, equipment, and users.
 */
public interface OperationalMastersService {

    // ==================== Storer Management ====================
    Storer createStorer(CreateStorerRequest request);
    Storer getStorer(StorerKey storerKey);
    Storer getStorerByCode(String storerCode);
    List<Storer> getActiveStorers();
    List<Storer> searchStorers(StorerSearchCriteria criteria);

    void updateStorer(StorerKey storerKey, Storer storer);
    void activateStorer(StorerKey storerKey);
    void deactivateStorer(StorerKey storerKey);
    void suspendStorer(StorerKey storerKey, String reason);

    // ==================== Lane Management ====================
    Lane createLane(CreateLaneRequest request);
    Lane getLane(LaneKey laneKey);
    List<Lane> getLanesByZone(ZoneKey zoneKey);
    List<Lane> getAvailableLanes(ZoneKey zoneKey, Lane.LaneType type);
    List<Lane> getLanesByDock(DockKey dockKey);

    void updateLane(LaneKey laneKey, Lane lane);
    void assignLaneToDock(LaneKey laneKey, DockKey dockKey);
    void unassignLaneFromDock(LaneKey laneKey);
    void reserveLane(LaneKey laneKey, String reservedFor);
    void releaseLane(LaneKey laneKey);
    void blockLane(LaneKey laneKey, String reason);

    // ==================== Dock Management ====================
    Dock createDock(CreateDockRequest request);
    Dock getDock(DockKey dockKey);
    List<Dock> getDocksByWarehouse(WarehouseKey warehouseKey);
    List<Dock> getAvailableDocks(WarehouseKey warehouseKey, Dock.DockType type);
    List<Dock> getDocksForCarrier(WarehouseKey warehouseKey, String carrierCode);

    void updateDock(DockKey dockKey, Dock dock);
    void reserveDock(DockKey dockKey, String appointmentId, LocalDateTime start, LocalDateTime end);
    void occupyDock(DockKey dockKey, String trailerId, String appointmentId);
    void releaseDock(DockKey dockKey);
    void blockDock(DockKey dockKey, String reason);

    // ==================== Equipment Management ====================
    Equipment createEquipment(CreateEquipmentRequest request);
    Equipment getEquipment(EquipmentKey equipmentKey);
    List<Equipment> getEquipmentByWarehouse(WarehouseKey warehouseKey);
    List<Equipment> getAvailableEquipment(WarehouseKey warehouseKey, Equipment.EquipmentType type);
    List<Equipment> getEquipmentByZone(ZoneKey zoneKey);
    List<Equipment> getEquipmentNeedingMaintenance();

    void updateEquipment(EquipmentKey equipmentKey, Equipment equipment);
    void assignEquipmentToUser(EquipmentKey equipmentKey, UserKey userKey);
    void returnEquipment(EquipmentKey equipmentKey);
    void sendEquipmentToMaintenance(EquipmentKey equipmentKey, String reason);
    void completeEquipmentMaintenance(EquipmentKey equipmentKey);
    void retireEquipment(EquipmentKey equipmentKey, String reason);

    // ==================== User Management ====================
    User createUser(CreateUserRequest request);
    User getUser(UserKey userKey);
    User getUserByUserId(String userId);
    List<User> getUsersByWarehouse(WarehouseKey warehouseKey);
    List<User> getActiveUsers(WarehouseKey warehouseKey);
    List<User> getAvailableUsers(ZoneKey zoneKey);
    List<User> getUsersByShift(WarehouseKey warehouseKey, String shift);

    void updateUser(UserKey userKey, User user);
    void activateUser(UserKey userKey);
    void deactivateUser(UserKey userKey);
    void suspendUser(UserKey userKey, String reason);
    void loginUser(UserKey userKey);
    void logoffUser(UserKey userKey);
    void assignUserToZone(UserKey userKey, ZoneKey zoneKey);
    void removeUserFromZone(UserKey userKey, ZoneKey zoneKey);
    void setUserSkillLevel(UserKey userKey, String skill, int level);
    void authorizeUserForEquipment(UserKey userKey, Equipment.EquipmentType equipmentType);

    // Request Records
    record CreateStorerRequest(
            String storerCode,
            String storerName,
            String companyName,
            Storer.StorerType storerType,
            Address billingAddress,
            ContactInfo primaryContact,
            String putawayStrategy,
            String rotationRule
    ) {}

    record StorerSearchCriteria(
            String storerCode,
            String storerName,
            Storer.StorerType storerType,
            Storer.StorerStatus status,
            int limit,
            int offset
    ) {}

    record CreateLaneRequest(
            WarehouseKey warehouseKey,
            ZoneKey zoneKey,
            String laneCode,
            String laneName,
            Lane.LaneType laneType,
            int maxPallets
    ) {}

    record CreateDockRequest(
            WarehouseKey warehouseKey,
            ZoneKey zoneKey,
            String dockCode,
            String dockName,
            int dockNumber,
            Dock.DockType dockType,
            boolean canReceive,
            boolean canShip,
            OperatingHours operatingHours
    ) {}

    record CreateEquipmentRequest(
            WarehouseKey warehouseKey,
            String equipmentCode,
            String equipmentName,
            String serialNumber,
            Equipment.EquipmentType equipmentType,
            String manufacturer,
            String model,
            ZoneKey homeZone
    ) {}

    record CreateUserRequest(
            WarehouseKey warehouseKey,
            String userId,
            String userName,
            String firstName,
            String lastName,
            String badgeNumber,
            ContactInfo contact,
            String department,
            String jobTitle,
            String shift
    ) {}
}
