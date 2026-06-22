package com.maersk.wms.masterdata.domain.operational_masters.repository;

import com.maersk.wms.masterdata.domain.operational_masters.model.Dock;
import com.maersk.wms.masterdata.shared.kernel.identifiers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Dock entity.
 */
public interface DockRepository {

    Dock save(Dock dock);

    Optional<Dock> findByDockKey(DockKey dockKey);

    Optional<Dock> findByWarehouseAndCode(WarehouseKey warehouseKey, String dockCode);

    List<Dock> findByWarehouse(WarehouseKey warehouseKey);

    List<Dock> findByZone(ZoneKey zoneKey);

    List<Dock> findByWarehouseAndType(WarehouseKey warehouseKey, Dock.DockType dockType);

    List<Dock> findByWarehouseAndStatus(WarehouseKey warehouseKey, Dock.DockStatus status);

    List<Dock> findAvailableByWarehouse(WarehouseKey warehouseKey);

    List<Dock> findAvailableByWarehouseAndType(WarehouseKey warehouseKey, Dock.DockType type);

    List<Dock> findByAssignedCarrier(String carrierCode);

    List<Dock> findAvailableForTimeSlot(WarehouseKey warehouseKey, LocalDateTime start, LocalDateTime end);

    boolean existsByWarehouseAndCode(WarehouseKey warehouseKey, String dockCode);

    void delete(Dock dock);
}
