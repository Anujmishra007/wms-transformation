package com.maersk.wms.printing.domain.printer_management.service;

import com.maersk.wms.printing.domain.printer_management.model.Printer;
import com.maersk.wms.printing.domain.printer_management.model.PrintQueue;
import com.maersk.wms.printing.shared.kernel.identifiers.*;
import com.maersk.wms.printing.shared.kernel.valueobjects.PrinterCapabilities;
import com.maersk.wms.printing.shared.kernel.valueobjects.PrinterConnection;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for printer and queue management operations.
 * Handles printer registration, status, and queue configuration.
 */
public interface PrinterManagementService {

    // Printer Registration
    Printer registerPrinter(String printerName, String printerCode, Printer.PrinterType type,
                            PrinterConnection connection, ZoneKey zoneKey,
                            WarehouseKey warehouseKey, UserKey registeredBy);

    Printer updatePrinter(PrinterKey printerKey, Printer printer);

    void decommissionPrinter(PrinterKey printerKey, String reason, UserKey decommissionedBy);

    // Printer Status
    void setOnline(PrinterKey printerKey);

    void setOffline(PrinterKey printerKey, String reason);

    void setBusy(PrinterKey printerKey);

    void setError(PrinterKey printerKey, String error);

    void pausePrinter(PrinterKey printerKey);

    void resumePrinter(PrinterKey printerKey);

    void disablePrinter(PrinterKey printerKey);

    void enablePrinter(PrinterKey printerKey);

    void setOutOfMedia(PrinterKey printerKey);

    void setOutOfRibbon(PrinterKey printerKey);

    void clearError(PrinterKey printerKey);

    // Printer Capabilities
    void updateCapabilities(PrinterKey printerKey, PrinterCapabilities capabilities);

    void assignLabelTypes(PrinterKey printerKey, List<String> labelTypes);

    void removeLabelTypeAssignment(PrinterKey printerKey, String labelType);

    // Printer Assignment
    void assignToZone(PrinterKey printerKey, ZoneKey zoneKey);

    void assignToUser(PrinterKey printerKey, UserKey userKey);

    void setDefaultDevice(PrinterKey printerKey, DeviceKey deviceKey);

    void unassignFromUser(PrinterKey printerKey);

    // Printer Retrieval
    Optional<Printer> findByKey(PrinterKey printerKey);

    Optional<Printer> findByCode(String printerCode, WarehouseKey warehouseKey);

    List<Printer> findByWarehouse(WarehouseKey warehouseKey);

    List<Printer> findByZone(ZoneKey zoneKey);

    List<Printer> findByStatus(Printer.PrinterStatus status, WarehouseKey warehouseKey);

    List<Printer> findAvailablePrinters(WarehouseKey warehouseKey);

    List<Printer> findByType(Printer.PrinterType type, WarehouseKey warehouseKey);

    List<Printer> findByLabelType(String labelType, WarehouseKey warehouseKey);

    List<Printer> findPrintersNeedingMaintenance(WarehouseKey warehouseKey);

    // Printer Selection
    Optional<Printer> selectBestPrinter(String labelType, ZoneKey zoneKey, WarehouseKey warehouseKey);

    Optional<Printer> selectNearestPrinter(String locationCode, WarehouseKey warehouseKey);

    List<Printer> findCompatiblePrinters(TemplateKey templateKey, WarehouseKey warehouseKey);

    // Printer Metrics
    void recordJobCompleted(PrinterKey printerKey, int labels);

    void resetDailyCounters(PrinterKey printerKey);

    void resetAllDailyCounters(WarehouseKey warehouseKey);

    // Printer Maintenance
    void scheduleMaintenance(PrinterKey printerKey, java.time.Instant maintenanceDate);

    void recordMaintenanceCompleted(PrinterKey printerKey);

    void updatePrintHeadLife(PrinterKey printerKey, int percentRemaining);

    // Queue Management
    PrintQueue createQueue(String queueName, String queueCode, PrinterKey printerKey,
                           ZoneKey zoneKey, WarehouseKey warehouseKey, UserKey createdBy);

    PrintQueue createZoneLevelQueue(String queueName, String queueCode, ZoneKey zoneKey,
                                     WarehouseKey warehouseKey, UserKey createdBy);

    PrintQueue updateQueue(PrintQueueKey queueKey, PrintQueue queue);

    void deleteQueue(PrintQueueKey queueKey);

    // Queue Status
    void activateQueue(PrintQueueKey queueKey);

    void pauseQueue(PrintQueueKey queueKey);

    void disableQueue(PrintQueueKey queueKey);

    // Queue Configuration
    void setQueueMaxSize(PrintQueueKey queueKey, int maxSize);

    void setQueuePriority(PrintQueueKey queueKey, int priority);

    void setAsDefaultQueue(PrintQueueKey queueKey);

    // Queue Operations
    boolean enqueueJob(PrintQueueKey queueKey, PrintJobKey jobKey);

    PrintJobKey dequeueNextJob(PrintQueueKey queueKey);

    void removeJobFromQueue(PrintQueueKey queueKey, PrintJobKey jobKey);

    void prioritizeJobInQueue(PrintQueueKey queueKey, PrintJobKey jobKey);

    void completeCurrentJob(PrintQueueKey queueKey);

    // Queue Retrieval
    Optional<PrintQueue> findQueueByKey(PrintQueueKey queueKey);

    Optional<PrintQueue> findQueueByCode(String queueCode, WarehouseKey warehouseKey);

    List<PrintQueue> findQueuesByPrinter(PrinterKey printerKey);

    List<PrintQueue> findQueuesByZone(ZoneKey zoneKey);

    List<PrintQueue> findActiveQueues(WarehouseKey warehouseKey);

    Optional<PrintQueue> findDefaultQueue(PrinterKey printerKey);

    Optional<PrintQueue> findDefaultQueueForZone(ZoneKey zoneKey);

    // Queue Monitoring
    int getQueueDepth(PrintQueueKey queueKey);

    List<PrintJobKey> getPendingJobs(PrintQueueKey queueKey);

    int getJobPosition(PrintQueueKey queueKey, PrintJobKey jobKey);
}
