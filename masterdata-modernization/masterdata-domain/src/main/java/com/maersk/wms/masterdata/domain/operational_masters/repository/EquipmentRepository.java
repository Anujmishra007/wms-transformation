package com.maersk.wms.masterdata.domain.operational_masters.repository;

import com.maersk.wms.masterdata.domain.operational_masters.model.Equipment;
import com.maersk.wms.masterdata.shared.kernel.identifiers.*;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Equipment entity.
 */
public interface EquipmentRepository {

    Equipment save(Equipment equipment);

    Optional<Equipment> findByEquipmentKey(EquipmentKey equipmentKey);

    Optional<Equipment> findByEquipmentCode(String equipmentCode);

    Optional<Equipment> findBySerialNumber(String serialNumber);

    List<Equipment> findByWarehouse(WarehouseKey warehouseKey);

    List<Equipment> findByWarehouseAndType(WarehouseKey warehouseKey, Equipment.EquipmentType type);

    List<Equipment> findByWarehouseAndStatus(WarehouseKey warehouseKey, Equipment.EquipmentStatus status);

    List<Equipment> findAvailableByWarehouse(WarehouseKey warehouseKey);

    List<Equipment> findAvailableByWarehouseAndType(WarehouseKey warehouseKey, Equipment.EquipmentType type);

    List<Equipment> findByHomeZone(ZoneKey zoneKey);

    List<Equipment> findByAssignedUser(UserKey userKey);

    List<Equipment> findNeedingMaintenance();

    boolean existsByEquipmentCode(String equipmentCode);

    void delete(Equipment equipment);

    int countByWarehouseAndStatus(WarehouseKey warehouseKey, Equipment.EquipmentStatus status);
}
