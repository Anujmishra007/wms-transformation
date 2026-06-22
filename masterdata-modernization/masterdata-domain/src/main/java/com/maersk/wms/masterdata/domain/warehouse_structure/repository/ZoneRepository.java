package com.maersk.wms.masterdata.domain.warehouse_structure.repository;

import com.maersk.wms.masterdata.domain.warehouse_structure.model.Zone;
import com.maersk.wms.masterdata.shared.kernel.identifiers.*;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Zone entity.
 */
public interface ZoneRepository {

    Zone save(Zone zone);

    Optional<Zone> findByZoneKey(ZoneKey zoneKey);

    Optional<Zone> findByWarehouseAndCode(WarehouseKey warehouseKey, String zoneCode);

    List<Zone> findByWarehouse(WarehouseKey warehouseKey);

    List<Zone> findActiveByWarehouse(WarehouseKey warehouseKey);

    List<Zone> findByWarehouseAndType(WarehouseKey warehouseKey, Zone.ZoneType zoneType);

    List<Zone> findByStorageType(WarehouseKey warehouseKey, String storageType);

    List<Zone> findByTemperatureClass(WarehouseKey warehouseKey, String temperatureClass);

    boolean existsByWarehouseAndCode(WarehouseKey warehouseKey, String zoneCode);

    void delete(Zone zone);
}
