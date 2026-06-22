package com.maersk.wms.printing.domain.printer_management.repository;

import com.maersk.wms.printing.domain.printer_management.model.PrintQueue;
import com.maersk.wms.printing.shared.kernel.identifiers.*;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for PrintQueue persistence operations.
 */
public interface PrintQueueRepository {

    PrintQueue save(PrintQueue queue);

    List<PrintQueue> saveAll(List<PrintQueue> queues);

    Optional<PrintQueue> findByKey(PrintQueueKey queueKey);

    Optional<PrintQueue> findByCode(String queueCode, WarehouseKey warehouseKey);

    List<PrintQueue> findByPrinterKey(PrinterKey printerKey);

    List<PrintQueue> findByZoneKey(ZoneKey zoneKey);

    List<PrintQueue> findByWarehouseKey(WarehouseKey warehouseKey);

    List<PrintQueue> findByStatus(PrintQueue.QueueStatus status, WarehouseKey warehouseKey);

    List<PrintQueue> findActiveQueues(WarehouseKey warehouseKey);

    List<PrintQueue> findZoneLevelQueues(WarehouseKey warehouseKey);

    List<PrintQueue> findPrinterLevelQueues(WarehouseKey warehouseKey);

    Optional<PrintQueue> findDefaultQueue(PrinterKey printerKey);

    Optional<PrintQueue> findDefaultQueueForZone(ZoneKey zoneKey);

    List<PrintQueue> findNonEmptyQueues(WarehouseKey warehouseKey);

    List<PrintQueue> findByPriorityGreaterThan(int priority, WarehouseKey warehouseKey);

    void delete(PrintQueueKey queueKey);

    void deleteByPrinterKey(PrinterKey printerKey);

    boolean existsByKey(PrintQueueKey queueKey);

    boolean existsByCode(String queueCode, WarehouseKey warehouseKey);

    long countByStatus(PrintQueue.QueueStatus status, WarehouseKey warehouseKey);

    long countByPrinterKey(PrinterKey printerKey);

    long countByZoneKey(ZoneKey zoneKey);

    int getTotalPendingJobs(WarehouseKey warehouseKey);

    int getTotalJobsProcessedToday(WarehouseKey warehouseKey);
}
