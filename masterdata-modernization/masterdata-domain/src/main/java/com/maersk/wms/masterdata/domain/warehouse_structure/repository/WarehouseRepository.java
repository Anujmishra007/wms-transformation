package com.maersk.wms.masterdata.domain.warehouse_structure.repository;

import com.maersk.wms.masterdata.domain.warehouse_structure.model.Warehouse;
import com.maersk.wms.masterdata.shared.kernel.identifiers.*;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Warehouse aggregate.
 */
public interface WarehouseRepository {

    Warehouse save(Warehouse warehouse);

    Optional<Warehouse> findByWarehouseKey(WarehouseKey warehouseKey);

    Optional<Warehouse> findByWarehouseCode(String warehouseCode);

    List<Warehouse> findAll();

    List<Warehouse> findByStatus(Warehouse.WarehouseStatus status);

    List<Warehouse> findByCountryCode(String countryCode);

    List<Warehouse> findByRegionCode(String regionCode);

    boolean existsByWarehouseCode(String warehouseCode);

    void delete(Warehouse warehouse);
}
