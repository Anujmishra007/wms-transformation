package com.maersk.wms.printing.service.printer;

import com.maersk.wms.printing.domain.printer_management.model.Printer;
import com.maersk.wms.printing.domain.printer_management.model.PrintQueue;
import com.maersk.wms.printing.domain.printer_management.repository.PrinterRepository;
import com.maersk.wms.printing.domain.printer_management.repository.PrintQueueRepository;
import com.maersk.wms.printing.domain.printer_management.service.PrinterManagementService;
import com.maersk.wms.printing.domain.printer_management.event.PrinterEvents.*;
import com.maersk.wms.printing.domain.printer_management.event.PrintQueueEvents.*;
import com.maersk.wms.printing.shared.kernel.identifiers.*;
import com.maersk.wms.printing.shared.kernel.valueobjects.PrinterCapabilities;
import com.maersk.wms.printing.shared.kernel.valueobjects.PrinterConnection;
import com.maersk.wms.printing.shared.kernel.exceptions.*;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of PrinterManagementService.
 * Handles printer registration, status, and queue configuration.
 */
@Service
@Transactional
public class PrinterManagementServiceImpl implements PrinterManagementService {

    private final PrinterRepository printerRepository;
    private final PrintQueueRepository queueRepository;
    private final ApplicationEventPublisher eventPublisher;

    public PrinterManagementServiceImpl(
            PrinterRepository printerRepository,
            PrintQueueRepository queueRepository,
            ApplicationEventPublisher eventPublisher) {
        this.printerRepository = printerRepository;
        this.queueRepository = queueRepository;
        this.eventPublisher = eventPublisher;
    }

    // Printer Registration
    @Override
    public Printer registerPrinter(String printerName, String printerCode, Printer.PrinterType type,
                                    PrinterConnection connection, ZoneKey zoneKey,
                                    WarehouseKey warehouseKey, UserKey registeredBy) {
        if (printerRepository.existsByCode(printerCode, warehouseKey)) {
            throw new PrintingException("Printer code already exists: " + printerCode);
        }

        Printer printer = Printer.builder()
                .printerKey(new PrinterKey(UUID.randomUUID().toString()))
                .printerName(printerName)
                .printerCode(printerCode)
                .printerType(type)
                .connection(connection)
                .zoneKey(zoneKey)
                .warehouseKey(warehouseKey)
                .status(Printer.PrinterStatus.OFFLINE)
                .statusMessage("Newly registered")
                .createdAt(Instant.now())
                .createdBy(registeredBy.value())
                .build();

        Printer saved = printerRepository.save(printer);

        eventPublisher.publishEvent(new PrinterRegistered(
                saved.getPrinterKey(),
                printerCode,
                printerName,
                type.name(),
                zoneKey,
                warehouseKey,
                registeredBy,
                Instant.now()
        ));

        return saved;
    }

    @Override
    public Printer updatePrinter(PrinterKey printerKey, Printer printer) {
        Printer existing = findByKeyOrThrow(printerKey);

        existing.setPrinterName(printer.getPrinterName());
        existing.setSerialNumber(printer.getSerialNumber());
        existing.setAssetTag(printer.getAssetTag());
        existing.setManufacturer(printer.getManufacturer());
        existing.setModel(printer.getModel());
        existing.setFirmwareVersion(printer.getFirmwareVersion());
        existing.setConnection(printer.getConnection());
        existing.setLocationDescription(printer.getLocationDescription());
        existing.setBuilding(printer.getBuilding());
        existing.setFloor(printer.getFloor());
        existing.setArea(printer.getArea());
        existing.setUpdatedAt(Instant.now());

        Printer saved = printerRepository.save(existing);

        eventPublisher.publishEvent(new PrinterUpdated(
                printerKey,
                List.of("printerName", "serialNumber", "connection", "location"),
                Instant.now()
        ));

        return saved;
    }

    @Override
    public void decommissionPrinter(PrinterKey printerKey, String reason, UserKey decommissionedBy) {
        Printer printer = findByKeyOrThrow(printerKey);
        printer.disable();
        printerRepository.save(printer);

        eventPublisher.publishEvent(new PrinterDecommissioned(
                printerKey,
                reason,
                decommissionedBy,
                Instant.now()
        ));
    }

    // Printer Status
    @Override
    public void setOnline(PrinterKey printerKey) {
        Printer printer = findByKeyOrThrow(printerKey);
        printer.goOnline();
        printerRepository.save(printer);

        eventPublisher.publishEvent(new PrinterWentOnline(printerKey, Instant.now()));
    }

    @Override
    public void setOffline(PrinterKey printerKey, String reason) {
        Printer printer = findByKeyOrThrow(printerKey);
        printer.goOffline(reason);
        printerRepository.save(printer);

        eventPublisher.publishEvent(new PrinterWentOffline(printerKey, reason, Instant.now()));
    }

    @Override
    public void setBusy(PrinterKey printerKey) {
        Printer printer = findByKeyOrThrow(printerKey);
        printer.setBusy();
        printerRepository.save(printer);

        eventPublisher.publishEvent(new PrinterBecameBusy(printerKey, null, Instant.now()));
    }

    @Override
    public void setError(PrinterKey printerKey, String error) {
        Printer printer = findByKeyOrThrow(printerKey);
        String previousStatus = printer.getStatus().name();
        printer.setError(error);
        printerRepository.save(printer);

        eventPublisher.publishEvent(new PrinterErrorOccurred(
                printerKey,
                error,
                previousStatus,
                Instant.now()
        ));
    }

    @Override
    public void pausePrinter(PrinterKey printerKey) {
        Printer printer = findByKeyOrThrow(printerKey);
        printer.pause();
        printerRepository.save(printer);

        eventPublisher.publishEvent(new PrinterPaused(printerKey, Instant.now()));
    }

    @Override
    public void resumePrinter(PrinterKey printerKey) {
        Printer printer = findByKeyOrThrow(printerKey);
        printer.resume();
        printerRepository.save(printer);

        eventPublisher.publishEvent(new PrinterResumed(printerKey, Instant.now()));
    }

    @Override
    public void disablePrinter(PrinterKey printerKey) {
        Printer printer = findByKeyOrThrow(printerKey);
        printer.disable();
        printerRepository.save(printer);

        eventPublisher.publishEvent(new PrinterDisabled(printerKey, Instant.now()));
    }

    @Override
    public void enablePrinter(PrinterKey printerKey) {
        Printer printer = findByKeyOrThrow(printerKey);
        printer.goOnline();
        printerRepository.save(printer);

        eventPublisher.publishEvent(new PrinterEnabled(printerKey, Instant.now()));
    }

    @Override
    public void setOutOfMedia(PrinterKey printerKey) {
        Printer printer = findByKeyOrThrow(printerKey);
        printer.setOutOfMedia();
        printerRepository.save(printer);

        eventPublisher.publishEvent(new PrinterOutOfMedia(printerKey, Instant.now()));
    }

    @Override
    public void setOutOfRibbon(PrinterKey printerKey) {
        Printer printer = findByKeyOrThrow(printerKey);
        printer.setOutOfRibbon();
        printerRepository.save(printer);

        eventPublisher.publishEvent(new PrinterOutOfRibbon(printerKey, Instant.now()));
    }

    @Override
    public void clearError(PrinterKey printerKey) {
        Printer printer = findByKeyOrThrow(printerKey);
        String previousError = printer.getLastError();
        printer.goOnline();
        printerRepository.save(printer);

        eventPublisher.publishEvent(new PrinterErrorCleared(printerKey, previousError, Instant.now()));
    }

    // Printer Capabilities
    @Override
    public void updateCapabilities(PrinterKey printerKey, PrinterCapabilities capabilities) {
        Printer printer = findByKeyOrThrow(printerKey);
        printer.setCapabilities(capabilities);
        printerRepository.save(printer);
    }

    @Override
    public void assignLabelTypes(PrinterKey printerKey, List<String> labelTypes) {
        Printer printer = findByKeyOrThrow(printerKey);
        List<String> previousTypes = new ArrayList<>(printer.getAssignedLabelTypes());
        printer.setAssignedLabelTypes(labelTypes);
        printerRepository.save(printer);

        eventPublisher.publishEvent(new PrinterLabelTypesUpdated(
                printerKey,
                previousTypes,
                labelTypes,
                Instant.now()
        ));
    }

    @Override
    public void removeLabelTypeAssignment(PrinterKey printerKey, String labelType) {
        Printer printer = findByKeyOrThrow(printerKey);
        List<String> types = new ArrayList<>(printer.getAssignedLabelTypes());
        types.remove(labelType);
        printer.setAssignedLabelTypes(types);
        printerRepository.save(printer);
    }

    // Printer Assignment
    @Override
    public void assignToZone(PrinterKey printerKey, ZoneKey zoneKey) {
        Printer printer = findByKeyOrThrow(printerKey);
        ZoneKey previousZone = printer.getZoneKey();
        printer.setZoneKey(zoneKey);
        printerRepository.save(printer);

        eventPublisher.publishEvent(new PrinterAssignedToZone(
                printerKey,
                previousZone,
                zoneKey,
                Instant.now()
        ));
    }

    @Override
    public void assignToUser(PrinterKey printerKey, UserKey userKey) {
        Printer printer = findByKeyOrThrow(printerKey);
        printer.setAssignedUser(userKey);
        printerRepository.save(printer);

        eventPublisher.publishEvent(new PrinterAssignedToUser(printerKey, userKey, Instant.now()));
    }

    @Override
    public void setDefaultDevice(PrinterKey printerKey, DeviceKey deviceKey) {
        Printer printer = findByKeyOrThrow(printerKey);
        printer.setDefaultDevice(deviceKey);
        printerRepository.save(printer);
    }

    @Override
    public void unassignFromUser(PrinterKey printerKey) {
        Printer printer = findByKeyOrThrow(printerKey);
        UserKey previousUser = printer.getAssignedUser();
        printer.setAssignedUser(null);
        printerRepository.save(printer);

        eventPublisher.publishEvent(new PrinterUnassignedFromUser(printerKey, previousUser, Instant.now()));
    }

    // Printer Retrieval
    @Override
    @Transactional(readOnly = true)
    public Optional<Printer> findByKey(PrinterKey printerKey) {
        return printerRepository.findByKey(printerKey);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Printer> findByCode(String printerCode, WarehouseKey warehouseKey) {
        return printerRepository.findByCode(printerCode, warehouseKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Printer> findByWarehouse(WarehouseKey warehouseKey) {
        return printerRepository.findByWarehouseKey(warehouseKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Printer> findByZone(ZoneKey zoneKey) {
        return printerRepository.findByZoneKey(zoneKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Printer> findByStatus(Printer.PrinterStatus status, WarehouseKey warehouseKey) {
        return printerRepository.findByStatus(status, warehouseKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Printer> findAvailablePrinters(WarehouseKey warehouseKey) {
        return printerRepository.findAvailable(warehouseKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Printer> findByType(Printer.PrinterType type, WarehouseKey warehouseKey) {
        return printerRepository.findByType(type, warehouseKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Printer> findByLabelType(String labelType, WarehouseKey warehouseKey) {
        return printerRepository.findByAssignedLabelType(labelType, warehouseKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Printer> findPrintersNeedingMaintenance(WarehouseKey warehouseKey) {
        return printerRepository.findNeedingMaintenance(warehouseKey);
    }

    // Printer Selection
    @Override
    @Transactional(readOnly = true)
    public Optional<Printer> selectBestPrinter(String labelType, ZoneKey zoneKey, WarehouseKey warehouseKey) {
        List<Printer> candidates = printerRepository.findByZoneKey(zoneKey).stream()
                .filter(Printer::isAvailable)
                .filter(p -> p.supportsLabelType(labelType))
                .collect(Collectors.toList());

        // Select printer with least queue depth
        // TODO: Implement more sophisticated selection algorithm
        return candidates.stream().findFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Printer> selectNearestPrinter(String locationCode, WarehouseKey warehouseKey) {
        // TODO: Implement location-based printer selection
        return printerRepository.findAvailable(warehouseKey).stream().findFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Printer> findCompatiblePrinters(TemplateKey templateKey, WarehouseKey warehouseKey) {
        // TODO: Implement based on template's compatible printer types
        return printerRepository.findAvailable(warehouseKey);
    }

    // Printer Metrics
    @Override
    public void recordJobCompleted(PrinterKey printerKey, int labels) {
        Printer printer = findByKeyOrThrow(printerKey);
        printer.recordJobCompleted(labels);
        printerRepository.save(printer);

        eventPublisher.publishEvent(new PrinterJobCompleted(
                printerKey,
                null,
                labels,
                Instant.now()
        ));
    }

    @Override
    public void resetDailyCounters(PrinterKey printerKey) {
        Printer printer = findByKeyOrThrow(printerKey);
        int prevJobs = printer.getJobsToday();
        int prevLabels = printer.getLabelsToday();
        int prevErrors = printer.getErrorsToday();

        printer.setJobsToday(0);
        printer.setLabelsToday(0);
        printer.setErrorsToday(0);
        printerRepository.save(printer);

        eventPublisher.publishEvent(new PrinterDailyCountersReset(
                printerKey,
                prevJobs,
                prevLabels,
                prevErrors,
                Instant.now()
        ));
    }

    @Override
    public void resetAllDailyCounters(WarehouseKey warehouseKey) {
        printerRepository.findByWarehouseKey(warehouseKey)
                .forEach(printer -> resetDailyCounters(printer.getPrinterKey()));
    }

    // Printer Maintenance
    @Override
    public void scheduleMaintenance(PrinterKey printerKey, Instant maintenanceDate) {
        Printer printer = findByKeyOrThrow(printerKey);
        printer.setNextMaintenanceAt(maintenanceDate);
        printerRepository.save(printer);

        eventPublisher.publishEvent(new PrinterMaintenanceScheduled(
                printerKey,
                maintenanceDate,
                Instant.now()
        ));
    }

    @Override
    public void recordMaintenanceCompleted(PrinterKey printerKey) {
        Printer printer = findByKeyOrThrow(printerKey);
        printer.setLastMaintenanceAt(Instant.now());
        printer.setNextMaintenanceAt(null);
        printer.setPrintHeadLifePercent(100);
        printerRepository.save(printer);

        eventPublisher.publishEvent(new PrinterMaintenanceCompleted(printerKey, Instant.now()));
    }

    @Override
    public void updatePrintHeadLife(PrinterKey printerKey, int percentRemaining) {
        Printer printer = findByKeyOrThrow(printerKey);
        int previousPercent = printer.getPrintHeadLifePercent();
        printer.setPrintHeadLifePercent(percentRemaining);
        printerRepository.save(printer);

        eventPublisher.publishEvent(new PrinterPrintHeadLifeUpdated(
                printerKey,
                previousPercent,
                percentRemaining,
                Instant.now()
        ));

        if (printer.needsMaintenance()) {
            eventPublisher.publishEvent(new PrinterMaintenanceRequired(
                    printerKey,
                    "Print head life below threshold",
                    Instant.now()
            ));
        }
    }

    // Queue Management
    @Override
    public PrintQueue createQueue(String queueName, String queueCode, PrinterKey printerKey,
                                   ZoneKey zoneKey, WarehouseKey warehouseKey, UserKey createdBy) {
        if (queueRepository.existsByCode(queueCode, warehouseKey)) {
            throw new PrintingException("Queue code already exists: " + queueCode);
        }

        PrintQueue queue = PrintQueue.builder()
                .queueKey(new PrintQueueKey(UUID.randomUUID().toString()))
                .queueName(queueName)
                .queueCode(queueCode)
                .printerKey(printerKey)
                .zoneKey(zoneKey)
                .warehouseKey(warehouseKey)
                .maxSize(100)
                .priority(5)
                .status(PrintQueue.QueueStatus.ACTIVE)
                .createdAt(Instant.now())
                .createdBy(createdBy.value())
                .build();

        PrintQueue saved = queueRepository.save(queue);

        eventPublisher.publishEvent(new QueueCreated(
                saved.getQueueKey(),
                queueCode,
                queueName,
                printerKey,
                zoneKey,
                warehouseKey,
                createdBy,
                Instant.now()
        ));

        return saved;
    }

    @Override
    public PrintQueue createZoneLevelQueue(String queueName, String queueCode, ZoneKey zoneKey,
                                            WarehouseKey warehouseKey, UserKey createdBy) {
        return createQueue(queueName, queueCode, null, zoneKey, warehouseKey, createdBy);
    }

    @Override
    public PrintQueue updateQueue(PrintQueueKey queueKey, PrintQueue queue) {
        PrintQueue existing = findQueueByKeyOrThrow(queueKey);
        existing.setQueueName(queue.getQueueName());
        existing.setMaxSize(queue.getMaxSize());
        existing.setPriority(queue.getPriority());
        existing.setUpdatedAt(Instant.now());

        return queueRepository.save(existing);
    }

    @Override
    public void deleteQueue(PrintQueueKey queueKey) {
        PrintQueue queue = findQueueByKeyOrThrow(queueKey);
        int pendingJobs = queue.getQueueDepth();

        queueRepository.delete(queueKey);

        eventPublisher.publishEvent(new QueueDeleted(queueKey, pendingJobs, Instant.now()));
    }

    // Queue Status
    @Override
    public void activateQueue(PrintQueueKey queueKey) {
        PrintQueue queue = findQueueByKeyOrThrow(queueKey);
        queue.activate();
        queueRepository.save(queue);

        eventPublisher.publishEvent(new QueueActivated(queueKey, Instant.now()));
    }

    @Override
    public void pauseQueue(PrintQueueKey queueKey) {
        PrintQueue queue = findQueueByKeyOrThrow(queueKey);
        queue.pause();
        queueRepository.save(queue);

        eventPublisher.publishEvent(new QueuePaused(queueKey, queue.getQueueDepth(), Instant.now()));
    }

    @Override
    public void disableQueue(PrintQueueKey queueKey) {
        PrintQueue queue = findQueueByKeyOrThrow(queueKey);
        queue.disable();
        queueRepository.save(queue);

        eventPublisher.publishEvent(new QueueDisabled(queueKey, Instant.now()));
    }

    // Queue Configuration
    @Override
    public void setQueueMaxSize(PrintQueueKey queueKey, int maxSize) {
        PrintQueue queue = findQueueByKeyOrThrow(queueKey);
        int previousMaxSize = queue.getMaxSize();
        queue.setMaxSize(maxSize);
        queueRepository.save(queue);

        eventPublisher.publishEvent(new QueueMaxSizeChanged(queueKey, previousMaxSize, maxSize, Instant.now()));
    }

    @Override
    public void setQueuePriority(PrintQueueKey queueKey, int priority) {
        PrintQueue queue = findQueueByKeyOrThrow(queueKey);
        int previousPriority = queue.getPriority();
        queue.setPriority(priority);
        queueRepository.save(queue);

        eventPublisher.publishEvent(new QueuePriorityChanged(queueKey, previousPriority, priority, Instant.now()));
    }

    @Override
    public void setAsDefaultQueue(PrintQueueKey queueKey) {
        PrintQueue queue = findQueueByKeyOrThrow(queueKey);

        // Clear default from other queues for this printer
        PrintQueueKey previousDefaultKey = null;
        if (queue.getPrinterKey() != null) {
            Optional<PrintQueue> previousDefault = queueRepository.findDefaultQueue(queue.getPrinterKey());
            if (previousDefault.isPresent()) {
                previousDefaultKey = previousDefault.get().getQueueKey();
                previousDefault.get().setDefaultQueue(false);
                queueRepository.save(previousDefault.get());
            }
        }

        queue.setDefaultQueue(true);
        queueRepository.save(queue);

        eventPublisher.publishEvent(new QueueSetAsDefault(
                queueKey,
                queue.getPrinterKey(),
                previousDefaultKey,
                Instant.now()
        ));
    }

    // Queue Operations
    @Override
    public boolean enqueueJob(PrintQueueKey queueKey, PrintJobKey jobKey) {
        PrintQueue queue = findQueueByKeyOrThrow(queueKey);
        boolean success = queue.enqueue(jobKey);

        if (success) {
            queueRepository.save(queue);
            eventPublisher.publishEvent(new JobEnqueued(
                    queueKey,
                    jobKey,
                    queue.getPosition(jobKey),
                    queue.getQueueDepth(),
                    Instant.now()
            ));

            if (queue.isFull()) {
                eventPublisher.publishEvent(new QueueBecameFull(queueKey, queue.getMaxSize(), Instant.now()));
            }
        }

        return success;
    }

    @Override
    public PrintJobKey dequeueNextJob(PrintQueueKey queueKey) {
        PrintQueue queue = findQueueByKeyOrThrow(queueKey);
        PrintJobKey jobKey = queue.dequeue();

        if (jobKey != null) {
            queueRepository.save(queue);
            eventPublisher.publishEvent(new JobDequeued(
                    queueKey,
                    jobKey,
                    queue.getQueueDepth(),
                    Instant.now()
            ));
        }

        return jobKey;
    }

    @Override
    public void removeJobFromQueue(PrintQueueKey queueKey, PrintJobKey jobKey) {
        PrintQueue queue = findQueueByKeyOrThrow(queueKey);
        queue.removeJob(jobKey);
        queueRepository.save(queue);

        eventPublisher.publishEvent(new JobRemovedFromQueue(queueKey, jobKey, "Manually removed", Instant.now()));

        if (queue.isEmpty()) {
            eventPublisher.publishEvent(new QueueBecameEmpty(queueKey, Instant.now()));
        }
    }

    @Override
    public void prioritizeJobInQueue(PrintQueueKey queueKey, PrintJobKey jobKey) {
        PrintQueue queue = findQueueByKeyOrThrow(queueKey);
        int previousPosition = queue.getPosition(jobKey);
        queue.prioritize(jobKey);
        queueRepository.save(queue);

        eventPublisher.publishEvent(new JobPrioritizedInQueue(
                queueKey,
                jobKey,
                previousPosition,
                1,
                Instant.now()
        ));
    }

    @Override
    public void completeCurrentJob(PrintQueueKey queueKey) {
        PrintQueue queue = findQueueByKeyOrThrow(queueKey);
        PrintJobKey completedJobKey = queue.getCurrentJob();
        queue.completeCurrentJob();
        queueRepository.save(queue);

        eventPublisher.publishEvent(new CurrentJobCompleted(
                queueKey,
                completedJobKey,
                queue.getJobsProcessedToday(),
                Instant.now()
        ));
    }

    // Queue Retrieval
    @Override
    @Transactional(readOnly = true)
    public Optional<PrintQueue> findQueueByKey(PrintQueueKey queueKey) {
        return queueRepository.findByKey(queueKey);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PrintQueue> findQueueByCode(String queueCode, WarehouseKey warehouseKey) {
        return queueRepository.findByCode(queueCode, warehouseKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrintQueue> findQueuesByPrinter(PrinterKey printerKey) {
        return queueRepository.findByPrinterKey(printerKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrintQueue> findQueuesByZone(ZoneKey zoneKey) {
        return queueRepository.findByZoneKey(zoneKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrintQueue> findActiveQueues(WarehouseKey warehouseKey) {
        return queueRepository.findActiveQueues(warehouseKey);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PrintQueue> findDefaultQueue(PrinterKey printerKey) {
        return queueRepository.findDefaultQueue(printerKey);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PrintQueue> findDefaultQueueForZone(ZoneKey zoneKey) {
        return queueRepository.findDefaultQueueForZone(zoneKey);
    }

    // Queue Monitoring
    @Override
    @Transactional(readOnly = true)
    public int getQueueDepth(PrintQueueKey queueKey) {
        return findQueueByKeyOrThrow(queueKey).getQueueDepth();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrintJobKey> getPendingJobs(PrintQueueKey queueKey) {
        return findQueueByKeyOrThrow(queueKey).getPendingJobs();
    }

    @Override
    @Transactional(readOnly = true)
    public int getJobPosition(PrintQueueKey queueKey, PrintJobKey jobKey) {
        return findQueueByKeyOrThrow(queueKey).getPosition(jobKey);
    }

    // Helper Methods
    private Printer findByKeyOrThrow(PrinterKey printerKey) {
        return printerRepository.findByKey(printerKey)
                .orElseThrow(() -> new PrinterNotFoundException("Printer not found: " + printerKey.value()));
    }

    private PrintQueue findQueueByKeyOrThrow(PrintQueueKey queueKey) {
        return queueRepository.findByKey(queueKey)
                .orElseThrow(() -> new PrintingException("Print queue not found: " + queueKey.value()));
    }
}
