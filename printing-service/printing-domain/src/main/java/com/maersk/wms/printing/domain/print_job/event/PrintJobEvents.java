package com.maersk.wms.printing.domain.print_job.event;

import com.maersk.wms.printing.shared.kernel.events.PrintingDomainEvent;
import com.maersk.wms.printing.shared.kernel.identifiers.*;

import java.time.Instant;
import java.util.List;

/**
 * Domain events for Print Job Management bounded context.
 */
public final class PrintJobEvents {

    private PrintJobEvents() {}

    // Job Lifecycle Events
    public record PrintJobCreated(
            PrintJobKey jobKey,
            String jobName,
            PrinterKey printerKey,
            int itemCount,
            String sourceType,
            WarehouseKey warehouseKey,
            UserKey requestedBy,
            DeviceKey requestedFrom,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "print_job.created";
        }
    }

    public record PrintJobQueued(
            PrintJobKey jobKey,
            PrintQueueKey queueKey,
            int queuePosition,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "print_job.queued";
        }
    }

    public record PrintJobStarted(
            PrintJobKey jobKey,
            PrinterKey printerKey,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "print_job.started";
        }
    }

    public record PrintJobCompleted(
            PrintJobKey jobKey,
            int printedItems,
            int failedItems,
            long durationMs,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "print_job.completed";
        }
    }

    public record PrintJobPartiallyCompleted(
            PrintJobKey jobKey,
            int printedItems,
            int failedItems,
            List<LabelKey> failedLabelKeys,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "print_job.partially_completed";
        }
    }

    public record PrintJobFailed(
            PrintJobKey jobKey,
            String error,
            int retryCount,
            boolean retriable,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "print_job.failed";
        }
    }

    public record PrintJobCancelled(
            PrintJobKey jobKey,
            String reason,
            UserKey cancelledBy,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "print_job.cancelled";
        }
    }

    public record PrintJobPaused(
            PrintJobKey jobKey,
            int printedSoFar,
            int remainingItems,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "print_job.paused";
        }
    }

    public record PrintJobResumed(
            PrintJobKey jobKey,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "print_job.resumed";
        }
    }

    public record PrintJobRetried(
            PrintJobKey jobKey,
            int retryCount,
            int maxRetries,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "print_job.retried";
        }
    }

    // Job Item Events
    public record PrintJobItemAdded(
            PrintJobKey jobKey,
            LabelKey labelKey,
            int sequenceNumber,
            int copies,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "print_job.item_added";
        }
    }

    public record PrintJobItemRemoved(
            PrintJobKey jobKey,
            LabelKey labelKey,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "print_job.item_removed";
        }
    }

    public record PrintJobItemPrinted(
            PrintJobKey jobKey,
            LabelKey labelKey,
            int sequenceNumber,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "print_job.item_printed";
        }
    }

    public record PrintJobItemFailed(
            PrintJobKey jobKey,
            LabelKey labelKey,
            String error,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "print_job.item_failed";
        }
    }

    public record PrintJobItemSkipped(
            PrintJobKey jobKey,
            LabelKey labelKey,
            String reason,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "print_job.item_skipped";
        }
    }

    // Job Queue Events
    public record PrintJobMovedToQueue(
            PrintJobKey jobKey,
            PrintQueueKey fromQueueKey,
            PrintQueueKey toQueueKey,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "print_job.moved_to_queue";
        }
    }

    public record PrintJobPrioritized(
            PrintJobKey jobKey,
            int previousPriority,
            int newPriority,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "print_job.prioritized";
        }
    }

    // Job Progress Events
    public record PrintJobProgressUpdated(
            PrintJobKey jobKey,
            int printedItems,
            int totalItems,
            double progressPercent,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "print_job.progress_updated";
        }
    }
}
