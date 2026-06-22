package com.maersk.wms.printing.domain.printer_management.model;

import com.maersk.wms.printing.shared.kernel.identifiers.*;
import com.maersk.wms.printing.shared.kernel.valueobjects.*;

import lombok.*;
import java.time.Instant;
import java.util.List;

/**
 * Printer aggregate root.
 * Represents a physical or logical printer.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Printer {

    private PrinterKey printerKey;
    private WarehouseKey warehouseKey;
    private ZoneKey zoneKey;

    // Identification
    private String printerName;
    private String printerCode;
    private String serialNumber;
    private String assetTag;

    // Type
    private PrinterType printerType;
    private String manufacturer;
    private String model;
    private String firmwareVersion;

    // Connection
    private PrinterConnection connection;

    // Capabilities
    private PrinterCapabilities capabilities;

    // Physical Location
    private String locationDescription;
    private String building;
    private String floor;
    private String area;

    // Assignment
    @Builder.Default
    private List<String> assignedLabelTypes = List.of();
    private DeviceKey defaultDevice;
    private UserKey assignedUser;

    // Status
    private PrinterStatus status;
    private String statusMessage;
    private Instant statusChangedAt;

    // Metrics
    private int jobsToday;
    private int labelsToday;
    private int errorsToday;
    private Instant lastJobAt;
    private Instant lastErrorAt;
    private String lastError;

    // Maintenance
    private Instant lastMaintenanceAt;
    private Instant nextMaintenanceAt;
    private int printHeadLifePercent;

    // Audit
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;

    public enum PrinterType {
        THERMAL_TRANSFER,   // Zebra, SATO thermal transfer
        DIRECT_THERMAL,     // Direct thermal labels
        LASER,              // Laser printer
        INKJET,             // Inkjet printer
        RFID,               // RFID encoder/printer
        MOBILE,             // Mobile/handheld printer
        VIRTUAL             // Virtual/PDF printer
    }

    public enum PrinterStatus {
        ONLINE,         // Ready to print
        OFFLINE,        // Not connected
        BUSY,           // Currently printing
        ERROR,          // Error state
        PAUSED,         // Paused by user
        OUT_OF_MEDIA,   // Out of labels/paper
        OUT_OF_RIBBON,  // Out of ribbon
        MAINTENANCE,    // Under maintenance
        DISABLED        // Administratively disabled
    }

    // Business Methods
    public void goOnline() {
        this.status = PrinterStatus.ONLINE;
        this.statusMessage = "Ready";
        this.statusChangedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void goOffline(String reason) {
        this.status = PrinterStatus.OFFLINE;
        this.statusMessage = reason;
        this.statusChangedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void setBusy() {
        this.status = PrinterStatus.BUSY;
        this.statusChangedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void setError(String error) {
        this.status = PrinterStatus.ERROR;
        this.statusMessage = error;
        this.lastError = error;
        this.lastErrorAt = Instant.now();
        this.errorsToday++;
        this.statusChangedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void pause() {
        this.status = PrinterStatus.PAUSED;
        this.statusChangedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void resume() {
        this.status = PrinterStatus.ONLINE;
        this.statusMessage = "Resumed";
        this.statusChangedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void disable() {
        this.status = PrinterStatus.DISABLED;
        this.statusChangedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void recordJobCompleted(int labels) {
        this.jobsToday++;
        this.labelsToday += labels;
        this.lastJobAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void setOutOfMedia() {
        this.status = PrinterStatus.OUT_OF_MEDIA;
        this.statusMessage = "Out of labels/paper";
        this.statusChangedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void setOutOfRibbon() {
        this.status = PrinterStatus.OUT_OF_RIBBON;
        this.statusMessage = "Out of ribbon";
        this.statusChangedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public boolean isAvailable() {
        return status == PrinterStatus.ONLINE;
    }

    public boolean canPrint() {
        return status == PrinterStatus.ONLINE || status == PrinterStatus.BUSY;
    }

    public boolean supportsLabelType(String labelType) {
        return assignedLabelTypes.isEmpty() || assignedLabelTypes.contains(labelType);
    }

    public boolean needsMaintenance() {
        if (nextMaintenanceAt != null && Instant.now().isAfter(nextMaintenanceAt)) {
            return true;
        }
        return printHeadLifePercent < 10;
    }
}
