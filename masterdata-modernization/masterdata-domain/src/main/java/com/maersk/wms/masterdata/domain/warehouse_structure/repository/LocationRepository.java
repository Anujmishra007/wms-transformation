package com.maersk.wms.masterdata.domain.warehouse_structure.repository;

import com.maersk.wms.masterdata.domain.warehouse_structure.model.Location;
import com.maersk.wms.masterdata.shared.kernel.identifiers.*;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Location entity.
 */
public interface LocationRepository {

    Location save(Location location);

    Optional<Location> findByLocationKey(LocationKey locationKey);

    Optional<Location> findByWarehouseAndCode(WarehouseKey warehouseKey, String locationCode);

    List<Location> findByWarehouse(WarehouseKey warehouseKey);

    List<Location> findByZone(ZoneKey zoneKey);

    List<Location> findByAisle(AisleKey aisleKey);

    List<Location> findByBay(BayKey bayKey);

    List<Location> findByLevel(LevelKey levelKey);

    List<Location> findAvailableByZone(ZoneKey zoneKey);

    List<Location> findEmptyByZone(ZoneKey zoneKey);

    List<Location> findByZoneAndType(ZoneKey zoneKey, Location.LocationType type);

    List<Location> findByZoneAndStatus(ZoneKey zoneKey, Location.LocationStatus status);

    List<Location> findByAbcClass(ZoneKey zoneKey, String abcClass);

    List<Location> findByStorageType(ZoneKey zoneKey, String storageType);

    List<Location> findByPutawayZone(String putawayZone);

    List<Location> findByPickZone(String pickZone);

    boolean existsByWarehouseAndCode(WarehouseKey warehouseKey, String locationCode);

    void delete(Location location);

    int countByZone(ZoneKey zoneKey);

    int countAvailableByZone(ZoneKey zoneKey);

    int countEmptyByZone(ZoneKey zoneKey);
}
