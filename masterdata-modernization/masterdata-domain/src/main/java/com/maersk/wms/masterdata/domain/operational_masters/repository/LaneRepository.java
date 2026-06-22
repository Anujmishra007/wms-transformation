package com.maersk.wms.masterdata.domain.operational_masters.repository;

import com.maersk.wms.masterdata.domain.operational_masters.model.Lane;
import com.maersk.wms.masterdata.shared.kernel.identifiers.*;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Lane entity.
 */
public interface LaneRepository {

    Lane save(Lane lane);

    Optional<Lane> findByLaneKey(LaneKey laneKey);

    Optional<Lane> findByWarehouseAndCode(WarehouseKey warehouseKey, String laneCode);

    List<Lane> findByWarehouse(WarehouseKey warehouseKey);

    List<Lane> findByZone(ZoneKey zoneKey);

    List<Lane> findByZoneAndType(ZoneKey zoneKey, Lane.LaneType laneType);

    List<Lane> findByZoneAndStatus(ZoneKey zoneKey, Lane.LaneStatus status);

    List<Lane> findAvailableByZone(ZoneKey zoneKey);

    List<Lane> findAvailableByZoneAndType(ZoneKey zoneKey, Lane.LaneType type);

    List<Lane> findByAssignedDock(DockKey dockKey);

    boolean existsByWarehouseAndCode(WarehouseKey warehouseKey, String laneCode);

    void delete(Lane lane);

    int countByZoneAndStatus(ZoneKey zoneKey, Lane.LaneStatus status);
}
