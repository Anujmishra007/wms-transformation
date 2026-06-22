package com.maersk.wms.printing.api.controller;

import com.maersk.wms.printing.domain.printer_management.model.Printer;
import com.maersk.wms.printing.domain.printer_management.model.PrintQueue;
import com.maersk.wms.printing.domain.printer_management.service.PrinterManagementService;
import com.maersk.wms.printing.shared.kernel.identifiers.*;
import com.maersk.wms.printing.shared.kernel.valueobjects.PrinterConnection;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

/**
 * REST controller for printer and queue management operations.
 */
@RestController
@RequestMapping("/api/v1/printers")
public class PrinterController {

    private final PrinterManagementService printerService;

    public PrinterController(PrinterManagementService printerService) {
        this.printerService = printerService;
    }

    // Printer Operations
    @PostMapping
    public ResponseEntity<PrinterResponse> registerPrinter(@RequestBody RegisterPrinterRequest request) {
        PrinterConnection connection = new PrinterConnection(
                PrinterConnection.ConnectionType.valueOf(request.connectionType()),
                request.ipAddress(),
                request.port(),
                request.macAddress()
        );

        Printer printer = printerService.registerPrinter(
                request.printerName(),
                request.printerCode(),
                Printer.PrinterType.valueOf(request.printerType()),
                connection,
                new ZoneKey(request.zoneKey()),
                new WarehouseKey(request.warehouseKey()),
                new UserKey(request.registeredBy())
        );

        return ResponseEntity.ok(mapToResponse(printer));
    }

    @GetMapping("/{printerKey}")
    public ResponseEntity<PrinterResponse> getPrinter(@PathVariable String printerKey) {
        return printerService.findByKey(new PrinterKey(printerKey))
                .map(this::mapToResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{printerKey}/online")
    public ResponseEntity<Void> setOnline(@PathVariable String printerKey) {
        printerService.setOnline(new PrinterKey(printerKey));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{printerKey}/offline")
    public ResponseEntity<Void> setOffline(
            @PathVariable String printerKey,
            @RequestBody SetOfflineRequest request) {
        printerService.setOffline(new PrinterKey(printerKey), request.reason());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{printerKey}/error")
    public ResponseEntity<Void> setError(
            @PathVariable String printerKey,
            @RequestBody SetErrorRequest request) {
        printerService.setError(new PrinterKey(printerKey), request.error());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{printerKey}/pause")
    public ResponseEntity<Void> pausePrinter(@PathVariable String printerKey) {
        printerService.pausePrinter(new PrinterKey(printerKey));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{printerKey}/resume")
    public ResponseEntity<Void> resumePrinter(@PathVariable String printerKey) {
        printerService.resumePrinter(new PrinterKey(printerKey));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{printerKey}/out-of-media")
    public ResponseEntity<Void> setOutOfMedia(@PathVariable String printerKey) {
        printerService.setOutOfMedia(new PrinterKey(printerKey));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{printerKey}/out-of-ribbon")
    public ResponseEntity<Void> setOutOfRibbon(@PathVariable String printerKey) {
        printerService.setOutOfRibbon(new PrinterKey(printerKey));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{printerKey}/label-types")
    public ResponseEntity<Void> assignLabelTypes(
            @PathVariable String printerKey,
            @RequestBody AssignLabelTypesRequest request) {
        printerService.assignLabelTypes(new PrinterKey(printerKey), request.labelTypes());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{printerKey}/maintenance")
    public ResponseEntity<Void> scheduleMaintenance(
            @PathVariable String printerKey,
            @RequestBody ScheduleMaintenanceRequest request) {
        printerService.scheduleMaintenance(new PrinterKey(printerKey), request.maintenanceDate());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{printerKey}/maintenance/complete")
    public ResponseEntity<Void> recordMaintenanceCompleted(@PathVariable String printerKey) {
        printerService.recordMaintenanceCompleted(new PrinterKey(printerKey));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/warehouse/{warehouseKey}")
    public ResponseEntity<List<PrinterResponse>> getPrintersByWarehouse(@PathVariable String warehouseKey) {
        List<Printer> printers = printerService.findByWarehouse(new WarehouseKey(warehouseKey));
        return ResponseEntity.ok(printers.stream().map(this::mapToResponse).toList());
    }

    @GetMapping("/zone/{zoneKey}")
    public ResponseEntity<List<PrinterResponse>> getPrintersByZone(@PathVariable String zoneKey) {
        List<Printer> printers = printerService.findByZone(new ZoneKey(zoneKey));
        return ResponseEntity.ok(printers.stream().map(this::mapToResponse).toList());
    }

    @GetMapping("/available/{warehouseKey}")
    public ResponseEntity<List<PrinterResponse>> getAvailablePrinters(@PathVariable String warehouseKey) {
        List<Printer> printers = printerService.findAvailablePrinters(new WarehouseKey(warehouseKey));
        return ResponseEntity.ok(printers.stream().map(this::mapToResponse).toList());
    }

    @GetMapping("/maintenance-needed/{warehouseKey}")
    public ResponseEntity<List<PrinterResponse>> getPrintersNeedingMaintenance(@PathVariable String warehouseKey) {
        List<Printer> printers = printerService.findPrintersNeedingMaintenance(new WarehouseKey(warehouseKey));
        return ResponseEntity.ok(printers.stream().map(this::mapToResponse).toList());
    }

    // Queue Operations
    @PostMapping("/queues")
    public ResponseEntity<PrintQueueResponse> createQueue(@RequestBody CreateQueueRequest request) {
        PrintQueue queue = printerService.createQueue(
                request.queueName(),
                request.queueCode(),
                request.printerKey() != null ? new PrinterKey(request.printerKey()) : null,
                new ZoneKey(request.zoneKey()),
                new WarehouseKey(request.warehouseKey()),
                new UserKey(request.createdBy())
        );

        return ResponseEntity.ok(mapToQueueResponse(queue));
    }

    @GetMapping("/queues/{queueKey}")
    public ResponseEntity<PrintQueueResponse> getQueue(@PathVariable String queueKey) {
        return printerService.findQueueByKey(new PrintQueueKey(queueKey))
                .map(this::mapToQueueResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/queues/{queueKey}/activate")
    public ResponseEntity<Void> activateQueue(@PathVariable String queueKey) {
        printerService.activateQueue(new PrintQueueKey(queueKey));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/queues/{queueKey}/pause")
    public ResponseEntity<Void> pauseQueue(@PathVariable String queueKey) {
        printerService.pauseQueue(new PrintQueueKey(queueKey));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/queues/{queueKey}/disable")
    public ResponseEntity<Void> disableQueue(@PathVariable String queueKey) {
        printerService.disableQueue(new PrintQueueKey(queueKey));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/queues/printer/{printerKey}")
    public ResponseEntity<List<PrintQueueResponse>> getQueuesByPrinter(@PathVariable String printerKey) {
        List<PrintQueue> queues = printerService.findQueuesByPrinter(new PrinterKey(printerKey));
        return ResponseEntity.ok(queues.stream().map(this::mapToQueueResponse).toList());
    }

    @GetMapping("/queues/active/{warehouseKey}")
    public ResponseEntity<List<PrintQueueResponse>> getActiveQueues(@PathVariable String warehouseKey) {
        List<PrintQueue> queues = printerService.findActiveQueues(new WarehouseKey(warehouseKey));
        return ResponseEntity.ok(queues.stream().map(this::mapToQueueResponse).toList());
    }

    // DTOs
    public record RegisterPrinterRequest(
            String printerName,
            String printerCode,
            String printerType,
            String connectionType,
            String ipAddress,
            Integer port,
            String macAddress,
            String zoneKey,
            String warehouseKey,
            String registeredBy
    ) {}

    public record SetOfflineRequest(String reason) {}

    public record SetErrorRequest(String error) {}

    public record AssignLabelTypesRequest(List<String> labelTypes) {}

    public record ScheduleMaintenanceRequest(Instant maintenanceDate) {}

    public record CreateQueueRequest(
            String queueName,
            String queueCode,
            String printerKey,
            String zoneKey,
            String warehouseKey,
            String createdBy
    ) {}

    public record PrinterResponse(
            String printerKey,
            String printerName,
            String printerCode,
            String printerType,
            String status,
            String statusMessage,
            String zoneKey,
            String warehouseKey,
            List<String> assignedLabelTypes,
            int jobsToday,
            int labelsToday,
            int errorsToday,
            int printHeadLifePercent,
            boolean needsMaintenance
    ) {}

    public record PrintQueueResponse(
            String queueKey,
            String queueName,
            String queueCode,
            String printerKey,
            String zoneKey,
            String status,
            int maxSize,
            int priority,
            boolean defaultQueue,
            int queueDepth,
            int jobsProcessedToday
    ) {}

    // Helper methods
    private PrinterResponse mapToResponse(Printer printer) {
        return new PrinterResponse(
                printer.getPrinterKey().value(),
                printer.getPrinterName(),
                printer.getPrinterCode(),
                printer.getPrinterType().name(),
                printer.getStatus().name(),
                printer.getStatusMessage(),
                printer.getZoneKey() != null ? printer.getZoneKey().value() : null,
                printer.getWarehouseKey().value(),
                printer.getAssignedLabelTypes(),
                printer.getJobsToday(),
                printer.getLabelsToday(),
                printer.getErrorsToday(),
                printer.getPrintHeadLifePercent(),
                printer.needsMaintenance()
        );
    }

    private PrintQueueResponse mapToQueueResponse(PrintQueue queue) {
        return new PrintQueueResponse(
                queue.getQueueKey().value(),
                queue.getQueueName(),
                queue.getQueueCode(),
                queue.getPrinterKey() != null ? queue.getPrinterKey().value() : null,
                queue.getZoneKey() != null ? queue.getZoneKey().value() : null,
                queue.getStatus().name(),
                queue.getMaxSize(),
                queue.getPriority(),
                queue.isDefaultQueue(),
                queue.getQueueDepth(),
                queue.getJobsProcessedToday()
        );
    }
}
