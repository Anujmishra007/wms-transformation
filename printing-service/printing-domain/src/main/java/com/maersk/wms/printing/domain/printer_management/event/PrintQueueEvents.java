package com.maersk.wms.printing.domain.printer_management.event;

import com.maersk.wms.printing.shared.kernel.events.PrintingDomainEvent;
import com.maersk.wms.printing.shared.kernel.identifiers.*;

import java.time.Instant;
import java.util.List;

/**
 * Domain events for Print Queue within Printer Management bounded context.
 */
public final class PrintQueueEvents {

    private PrintQueueEvents() {}

    // Queue Lifecycle Events
    public record QueueCreated(
            PrintQueueKey queueKey,
            String queueCode,
            String queueName,
            PrinterKey printerKey,
            ZoneKey zoneKey,
            WarehouseKey warehouseKey,
            UserKey createdBy,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "print_queue.created";
        }
    }

    public record QueueUpdated(
            PrintQueueKey queueKey,
            List<String> updatedFields,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "print_queue.updated";
        }
    }

    public record QueueDeleted(
            PrintQueueKey queueKey,
            int pendingJobsCount,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "print_queue.deleted";
        }
    }

    // Queue Status Events
    public record QueueActivated(
            PrintQueueKey queueKey,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "print_queue.activated";
        }
    }

    public record QueuePaused(
            PrintQueueKey queueKey,
            int pendingJobsCount,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "print_queue.paused";
        }
    }

    public record QueueDisabled(
            PrintQueueKey queueKey,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "print_queue.disabled";
        }
    }

    // Queue Configuration Events
    public record QueueMaxSizeChanged(
            PrintQueueKey queueKey,
            int previousMaxSize,
            int newMaxSize,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "print_queue.max_size_changed";
        }
    }

    public record QueuePriorityChanged(
            PrintQueueKey queueKey,
            int previousPriority,
            int newPriority,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "print_queue.priority_changed";
        }
    }

    public record QueueSetAsDefault(
            PrintQueueKey queueKey,
            PrinterKey printerKey,
            PrintQueueKey previousDefaultKey,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "print_queue.set_as_default";
        }
    }

    // Queue Job Events
    public record JobEnqueued(
            PrintQueueKey queueKey,
            PrintJobKey jobKey,
            int position,
            int queueDepth,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "print_queue.job_enqueued";
        }
    }

    public record JobDequeued(
            PrintQueueKey queueKey,
            PrintJobKey jobKey,
            int remainingJobs,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "print_queue.job_dequeued";
        }
    }

    public record JobRemovedFromQueue(
            PrintQueueKey queueKey,
            PrintJobKey jobKey,
            String reason,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "print_queue.job_removed";
        }
    }

    public record JobPrioritizedInQueue(
            PrintQueueKey queueKey,
            PrintJobKey jobKey,
            int previousPosition,
            int newPosition,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "print_queue.job_prioritized";
        }
    }

    public record CurrentJobCompleted(
            PrintQueueKey queueKey,
            PrintJobKey completedJobKey,
            int jobsProcessedToday,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "print_queue.current_job_completed";
        }
    }

    // Queue Capacity Events
    public record QueueBecameFull(
            PrintQueueKey queueKey,
            int maxSize,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "print_queue.became_full";
        }
    }

    public record QueueBecameEmpty(
            PrintQueueKey queueKey,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "print_queue.became_empty";
        }
    }

    public record QueueDepthThresholdReached(
            PrintQueueKey queueKey,
            int currentDepth,
            int threshold,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "print_queue.depth_threshold_reached";
        }
    }
}
