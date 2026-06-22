package com.maersk.wms.printing.domain.printer_management.repository;

import com.maersk.wms.printing.domain.printer_management.model.Printer;
import com.maersk.wms.printing.shared.kernel.identifiers.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Printer persistence operations.
 */
public interface PrinterRepository {

    Printer save(Printer printer);

    List<Printer> saveAll(List<Printer> printers);

    Optional<Printer> findByKey(PrinterKey printerKey);

    Optional<Printer> findByCode(String printerCode, WarehouseKey warehouseKey);

    Optional<Printer> findBySerialNumber(String serialNumber);

    Optional<Printer> findByAssetTag(String assetTag);

    List<Printer> findByWarehouseKey(WarehouseKey warehouseKey);

    List<Printer> findByZoneKey(ZoneKey zoneKey);

    List<Printer> findByStatus(Printer.PrinterStatus status, WarehouseKey warehouseKey);

    List<Printer> findByStatusIn(List<Printer.PrinterStatus> statuses, WarehouseKey warehouseKey);

    List<Printer> findAvailable(WarehouseKey warehouseKey);

    List<Printer> findOnline(WarehouseKey warehouseKey);

    List<Printer> findByType(Printer.PrinterType type, WarehouseKey warehouseKey);

    List<Printer> findByAssignedLabelType(String labelType, WarehouseKey warehouseKey);

    List<Printer> findByAssignedUser(UserKey userKey);

    List<Printer> findByDefaultDevice(DeviceKey deviceKey);

    List<Printer> findByManufacturer(String manufacturer, WarehouseKey warehouseKey);

    List<Printer> findByModel(String model, WarehouseKey warehouseKey);

    List<Printer> findNeedingMaintenance(WarehouseKey warehouseKey);

    List<Printer> findByNextMaintenanceBefore(Instant date);

    List<Printer> findByBuilding(String building, WarehouseKey warehouseKey);

    List<Printer> findByFloor(String floor, WarehouseKey warehouseKey);

    List<Printer> findByArea(String area, WarehouseKey warehouseKey);

    void delete(PrinterKey printerKey);

    boolean existsByKey(PrinterKey printerKey);

    boolean existsByCode(String printerCode, WarehouseKey warehouseKey);

    boolean existsBySerialNumber(String serialNumber);

    long countByStatus(Printer.PrinterStatus status, WarehouseKey warehouseKey);

    long countByType(Printer.PrinterType type, WarehouseKey warehouseKey);

    long countByZoneKey(ZoneKey zoneKey);

    long countOnline(WarehouseKey warehouseKey);

    long countOffline(WarehouseKey warehouseKey);
}
