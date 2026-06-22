package com.maersk.wms.printing.domain.printer_management.model;

import com.maersk.wms.printing.shared.kernel.identifiers.*;

import lombok.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Print Queue entity.
 * Manages queued print jobs for a printer or zone.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrintQueue {

    private PrintQueueKey queueKey;
    private PrinterKey printerKey; // null = zone-level queue
    private ZoneKey zoneKey;
    private WarehouseKey warehouseKey;

    // Queue Identity
    private String queueName;
    private String queueCode;

    // Configuration
    private int maxSize;
    private int priority; // Queue priority (higher = more important)
    private boolean defaultQueue;

    // Current State
    @Builder.Default
    private List<PrintJobKey> pendingJobs = new ArrayList<>();
    private PrintJobKey currentJob;
    private int jobsProcessedToday;

    // Status
    private QueueStatus status;

    // Audit
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;

    public enum QueueStatus {
        ACTIVE, PAUSED, DISABLED
    }

    // Business Methods
    public void activate() {
        this.status = QueueStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void pause() {
        this.status = QueueStatus.PAUSED;
        this.updatedAt = Instant.now();
    }

    public void disable() {
        this.status = QueueStatus.DISABLED;
        this.updatedAt = Instant.now();
    }

    public boolean enqueue(PrintJobKey jobKey) {
        if (pendingJobs.size() >= maxSize) {
            return false;
        }
        pendingJobs.add(jobKey);
        this.updatedAt = Instant.now();
        return true;
    }

    public PrintJobKey dequeue() {
        if (pendingJobs.isEmpty()) {
            return null;
        }
        PrintJobKey next = pendingJobs.remove(0);
        this.currentJob = next;
        this.updatedAt = Instant.now();
        return next;
    }

    public void completeCurrentJob() {
        this.currentJob = null;
        this.jobsProcessedToday++;
        this.updatedAt = Instant.now();
    }

    public void removeJob(PrintJobKey jobKey) {
        pendingJobs.remove(jobKey);
        this.updatedAt = Instant.now();
    }

    public void prioritize(PrintJobKey jobKey) {
        if (pendingJobs.remove(jobKey)) {
            pendingJobs.add(0, jobKey);
            this.updatedAt = Instant.now();
        }
    }

    public int getQueueDepth() {
        return pendingJobs.size();
    }

    public boolean isEmpty() {
        return pendingJobs.isEmpty() && currentJob == null;
    }

    public boolean isFull() {
        return pendingJobs.size() >= maxSize;
    }

    public boolean isActive() {
        return status == QueueStatus.ACTIVE;
    }

    public int getPosition(PrintJobKey jobKey) {
        int index = pendingJobs.indexOf(jobKey);
        return index >= 0 ? index + 1 : -1;
    }
}
