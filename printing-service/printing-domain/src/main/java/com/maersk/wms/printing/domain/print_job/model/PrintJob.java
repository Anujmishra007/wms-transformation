package com.maersk.wms.printing.domain.print_job.model;

import com.maersk.wms.printing.shared.kernel.identifiers.*;
import com.maersk.wms.printing.shared.kernel.valueobjects.*;

import lombok.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Print Job aggregate root.
 * Represents a print request that may contain multiple labels.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrintJob {

    private PrintJobKey printJobKey;
    private PrinterKey printerKey;
    private PrintQueueKey queueKey;
    private WarehouseKey warehouseKey;

    // Source
    private String sourceType; // RECEIVING, SHIPPING, INVENTORY, MANUAL
    private String sourceKey;
    private UserKey requestedBy;
    private DeviceKey requestedFrom;

    // Job Details
    private String jobName;
    private int priority; // Higher = more urgent
    private PrintSettings settings;

    // Items
    @Builder.Default
    private List<PrintJobItem> items = new ArrayList<>();

    // Progress
    private int totalItems;
    private int printedItems;
    private int failedItems;

    // Timing
    private Instant scheduledAt;
    private Instant startedAt;
    private Instant completedAt;

    // Retry
    private int retryCount;
    private int maxRetries;
    private String lastError;

    // Status
    private PrintJobStatus status;

    // Audit
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;

    public enum PrintJobStatus {
        CREATED,        // Job created
        QUEUED,         // In print queue
        PROCESSING,     // Being processed
        PRINTING,       // Printing in progress
        COMPLETED,      // All items printed
        PARTIAL,        // Some items printed
        FAILED,         // Job failed
        CANCELLED,      // Job cancelled
        PAUSED          // Job paused
    }

    // Business Methods
    public void queue() {
        this.status = PrintJobStatus.QUEUED;
        this.updatedAt = Instant.now();
    }

    public void start() {
        this.status = PrintJobStatus.PROCESSING;
        this.startedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void startPrinting() {
        this.status = PrintJobStatus.PRINTING;
        this.updatedAt = Instant.now();
    }

    public void complete() {
        if (failedItems > 0 && printedItems > 0) {
            this.status = PrintJobStatus.PARTIAL;
        } else if (failedItems > 0) {
            this.status = PrintJobStatus.FAILED;
        } else {
            this.status = PrintJobStatus.COMPLETED;
        }
        this.completedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void fail(String error) {
        this.status = PrintJobStatus.FAILED;
        this.lastError = error;
        this.completedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void cancel() {
        this.status = PrintJobStatus.CANCELLED;
        this.completedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void pause() {
        this.status = PrintJobStatus.PAUSED;
        this.updatedAt = Instant.now();
    }

    public void resume() {
        this.status = PrintJobStatus.QUEUED;
        this.updatedAt = Instant.now();
    }

    public void addItem(PrintJobItem item) {
        this.items.add(item);
        this.totalItems = items.size();
        this.updatedAt = Instant.now();
    }

    public void recordItemPrinted(LabelKey labelKey) {
        items.stream()
                .filter(i -> i.getLabelKey().equals(labelKey))
                .findFirst()
                .ifPresent(PrintJobItem::markPrinted);
        this.printedItems++;
        this.updatedAt = Instant.now();
    }

    public void recordItemFailed(LabelKey labelKey, String error) {
        items.stream()
                .filter(i -> i.getLabelKey().equals(labelKey))
                .findFirst()
                .ifPresent(i -> i.markFailed(error));
        this.failedItems++;
        this.updatedAt = Instant.now();
    }

    public boolean canRetry() {
        return retryCount < maxRetries &&
               (status == PrintJobStatus.FAILED || status == PrintJobStatus.PARTIAL);
    }

    public void incrementRetry() {
        this.retryCount++;
        this.status = PrintJobStatus.QUEUED;
        this.failedItems = 0;
        this.printedItems = 0;
        this.updatedAt = Instant.now();
    }

    public double getProgress() {
        if (totalItems == 0) return 0.0;
        return (double) (printedItems + failedItems) / totalItems * 100;
    }

    public boolean isComplete() {
        return status == PrintJobStatus.COMPLETED ||
               status == PrintJobStatus.PARTIAL ||
               status == PrintJobStatus.FAILED ||
               status == PrintJobStatus.CANCELLED;
    }
}
