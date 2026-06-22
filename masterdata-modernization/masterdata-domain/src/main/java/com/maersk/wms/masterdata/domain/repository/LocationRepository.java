package com.maersk.wms.masterdata.domain.repository;

import com.maersk.wms.masterdata.domain.Location;
import com.maersk.wms.masterdata.domain.LocationStatus;
import com.maersk.wms.masterdata.domain.LocationType;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Location entities.
 */
public interface LocationRepository {

    Location save(Location location);

    Optional<Location> findById(Long id);

    Optional<Location> findByLocationCode(String locationCode);

    List<Location> findByZone(String zone);

    List<Location> findByStatus(LocationStatus status);

    List<Location> findByLocationType(LocationType locationType);

    List<Location> findAvailableLocations(String zone);

    List<Location> findPickLocations(String zone);

    List<Location> findByAisle(String aisle);

    List<Location> findAll();

    void delete(Location location);

    boolean existsByLocationCode(String locationCode);
}
