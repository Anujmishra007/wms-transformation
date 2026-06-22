package com.maersk.wms.masterdata.domain.warehouse_structure.service;

import com.maersk.wms.masterdata.domain.warehouse_structure.model.*;
import com.maersk.wms.masterdata.shared.kernel.identifiers.*;
import com.maersk.wms.masterdata.shared.kernel.valueobjects.*;

import java.util.List;
import java.util.Optional;

/**
 * Warehouse Structure Service - manages warehouse layout and locations.
 */
public interface WarehouseStructureService {

    // Warehouse Management
    Warehouse createWarehouse(CreateWarehouseRequest request);
    Warehouse getWarehouse(WarehouseKey warehouseKey);
    List<Warehouse> getActiveWarehouses();
    void updateWarehouse(WarehouseKey warehouseKey, UpdateWarehouseRequest request);
    void activateWarehouse(WarehouseKey warehouseKey);
    void deactivateWarehouse(WarehouseKey warehouseKey);

    // Zone Management
    Zone createZone(WarehouseKey warehouseKey, CreateZoneRequest request);
    Zone getZone(ZoneKey zoneKey);
    List<Zone> getZonesByWarehouse(WarehouseKey warehouseKey);
    List<Zone> getActiveZonesByWarehouse(WarehouseKey warehouseKey);
    void updateZone(ZoneKey zoneKey, Zone zone);
    void activateZone(ZoneKey zoneKey);
    void deactivateZone(ZoneKey zoneKey);

    // Aisle Management
    Aisle createAisle(ZoneKey zoneKey, CreateAisleRequest request);
    Aisle getAisle(AisleKey aisleKey);
    List<Aisle> getAislesByZone(ZoneKey zoneKey);
    void updateAisle(AisleKey aisleKey, Aisle aisle);

    // Bay Management
    Bay createBay(AisleKey aisleKey, CreateBayRequest request);
    Bay getBay(BayKey bayKey);
    List<Bay> getBaysByAisle(AisleKey aisleKey);
    void updateBay(BayKey bayKey, Bay bay);

    // Level Management
    Level createLevel(BayKey bayKey, CreateLevelRequest request);
    Level getLevel(LevelKey levelKey);
    List<Level> getLevelsByBay(BayKey bayKey);
    void updateLevel(LevelKey levelKey, Level level);

    // Location Management
    Location createLocation(CreateLocationRequest request);
    Location getLocation(LocationKey locationKey);
    Location getLocationByCode(WarehouseKey warehouseKey, String locationCode);
    List<Location> getLocationsByZone(ZoneKey zoneKey);
    List<Location> getAvailableLocations(ZoneKey zoneKey);
    List<Location> searchLocations(LocationSearchCriteria criteria);

    void updateLocation(LocationKey locationKey, Location location);
    void activateLocation(LocationKey locationKey);
    void deactivateLocation(LocationKey locationKey);
    void lockLocation(LocationKey locationKey, String reason);
    void unlockLocation(LocationKey locationKey);

    // Bulk Operations
    void createLocationBatch(List<CreateLocationRequest> requests);
    List<Location> exportLocations(WarehouseKey warehouseKey);

    // Request Records
    record CreateWarehouseRequest(
            String warehouseCode,
            String warehouseName,
            Address address,
            ContactInfo contact,
            String timezone,
            String countryCode
    ) {}

    record UpdateWarehouseRequest(
            String warehouseName,
            Address address,
            ContactInfo contact,
            OperatingHours operatingHours
    ) {}

    record CreateZoneRequest(
            String zoneCode,
            String zoneName,
            Zone.ZoneType zoneType,
            String storageType,
            String temperatureClass
    ) {}

    record CreateAisleRequest(
            String aisleCode,
            String aisleName,
            int aisleNumber,
            String aisleType,
            String direction
    ) {}

    record CreateBayRequest(
            String bayCode,
            String bayName,
            int bayNumber,
            String bayType,
            int bayDepth
    ) {}

    record CreateLevelRequest(
            String levelCode,
            String levelName,
            int levelNumber,
            int heightFromFloorInches,
            int positionCount,
            LocationCapacity capacity
    ) {}

    record CreateLocationRequest(
            WarehouseKey warehouseKey,
            ZoneKey zoneKey,
            AisleKey aisleKey,
            BayKey bayKey,
            LevelKey levelKey,
            String locationCode,
            Location.LocationType locationType,
            String storageType,
            Dimensions dimensions,
            LocationCapacity capacity,
            String abcClass
    ) {}

    record LocationSearchCriteria(
            WarehouseKey warehouseKey,
            ZoneKey zoneKey,
            Location.LocationType locationType,
            String storageType,
            String abcClass,
            boolean availableOnly,
            boolean emptyOnly,
            int limit,
            int offset
    ) {}
}
