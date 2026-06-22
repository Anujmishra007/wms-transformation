package com.maersk.wms.masterdata.domain.repository;

import com.maersk.wms.masterdata.domain.Zone;
import com.maersk.wms.masterdata.domain.ZoneStatus;
import com.maersk.wms.masterdata.domain.ZoneType;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Zone entities.
 */
public interface ZoneRepository {

    Zone save(Zone zone);

    Optional<Zone> findById(Long id);

    Optional<Zone> findByZoneCode(String zoneCode);

    List<Zone> findByWarehouseCode(String warehouseCode);

    List<Zone> findByStatus(ZoneStatus status);

    List<Zone> findByZoneType(ZoneType zoneType);

    List<Zone> findActiveZones();

    List<Zone> findPickingZones();

    List<Zone> findPutawayZones();

    List<Zone> findAll();

    void delete(Zone zone);

    boolean existsByZoneCode(String zoneCode);
}
