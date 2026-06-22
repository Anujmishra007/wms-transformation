package com.maersk.wms.task.domain.lifecycle_service.event;

import com.maersk.wms.task.shared.kernel.events.TaskDomainEvent;
import com.maersk.wms.task.shared.kernel.identifiers.*;
import com.maersk.wms.task.shared.kernel.valueobjects.TaskPriorityValue;

import java.time.Instant;
import java.util.Map;

/**
 * Domain events for Task Lifecycle bounded context.
 * Covers the complete task state machine transitions.
 */
public final class LifecycleEvents {

    private LifecycleEvents() {}

    // ==================== Task Creation Events ====================

    public record TaskCreated(
            TaskKey taskKey,
            String taskType,
            String sourceType,
            String sourceKey,
            LocationKey fromLocation,
            LocationKey toLocation,
            TaskPriorityValue priority,
            Instant createdAt,
            String createdBy
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return taskKey.value();
        }

        @Override
        public String eventType() {
            return "TASK_CREATED";
        }

        @Override
        public Instant occurredAt() {
            return createdAt;
        }
    }

    public record TaskReleased(
            TaskKey taskKey,
            Instant releasedAt,
            String releasedBy
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return taskKey.value();
        }

        @Override
        public String eventType() {
            return "TASK_RELEASED";
        }

        @Override
        public Instant occurredAt() {
            return releasedAt;
        }
    }

    // ==================== Task Assignment Events ====================

    public record TaskAssigned(
            TaskKey taskKey,
            AssignmentKey assignmentKey,
            UserKey userId,
            DeviceKey deviceId,
            Instant assignedAt,
            String assignedBy
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return taskKey.value();
        }

        @Override
        public String eventType() {
            return "TASK_ASSIGNED";
        }

        @Override
        public Instant occurredAt() {
            return assignedAt;
        }
    }

    public record TaskUnassigned(
            TaskKey taskKey,
            AssignmentKey assignmentKey,
            UserKey previousUserId,
            String reason,
            Instant unassignedAt,
            String unassignedBy
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return taskKey.value();
        }

        @Override
        public String eventType() {
            return "TASK_UNASSIGNED";
        }

        @Override
        public Instant occurredAt() {
            return unassignedAt;
        }
    }

    public record TaskReassigned(
            TaskKey taskKey,
            UserKey fromUserId,
            UserKey toUserId,
            String reason,
            Instant reassignedAt,
            String reassignedBy
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return taskKey.value();
        }

        @Override
        public String eventType() {
            return "TASK_REASSIGNED";
        }

        @Override
        public Instant occurredAt() {
            return reassignedAt;
        }
    }

    // ==================== Task Execution Events ====================

    public record TaskStarted(
            TaskKey taskKey,
            UserKey userId,
            DeviceKey deviceId,
            LocationKey startLocation,
            Instant startedAt
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return taskKey.value();
        }

        @Override
        public String eventType() {
            return "TASK_STARTED";
        }

        @Override
        public Instant occurredAt() {
            return startedAt;
        }
    }

    public record TaskSuspended(
            TaskKey taskKey,
            String suspendReason,
            Instant suspendedAt,
            String suspendedBy
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return taskKey.value();
        }

        @Override
        public String eventType() {
            return "TASK_SUSPENDED";
        }

        @Override
        public Instant occurredAt() {
            return suspendedAt;
        }
    }

    public record TaskResumed(
            TaskKey taskKey,
            Instant resumedAt,
            String resumedBy
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return taskKey.value();
        }

        @Override
        public String eventType() {
            return "TASK_RESUMED";
        }

        @Override
        public Instant occurredAt() {
            return resumedAt;
        }
    }

    // ==================== Task Completion Events ====================

    public record TaskCompleted(
            TaskKey taskKey,
            UserKey completedByUser,
            LocationKey completionLocation,
            Map<String, Object> completionData,
            Instant completedAt
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return taskKey.value();
        }

        @Override
        public String eventType() {
            return "TASK_COMPLETED";
        }

        @Override
        public Instant occurredAt() {
            return completedAt;
        }
    }

    public record TaskPartiallyCompleted(
            TaskKey taskKey,
            int completedQuantity,
            int remainingQuantity,
            String reason,
            Instant partialCompletedAt
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return taskKey.value();
        }

        @Override
        public String eventType() {
            return "TASK_PARTIALLY_COMPLETED";
        }

        @Override
        public Instant occurredAt() {
            return partialCompletedAt;
        }
    }

    public record TaskCancelled(
            TaskKey taskKey,
            String cancellationReason,
            Instant cancelledAt,
            String cancelledBy
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return taskKey.value();
        }

        @Override
        public String eventType() {
            return "TASK_CANCELLED";
        }

        @Override
        public Instant occurredAt() {
            return cancelledAt;
        }
    }

    public record TaskClosed(
            TaskKey taskKey,
            Instant closedAt,
            String closedBy
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return taskKey.value();
        }

        @Override
        public String eventType() {
            return "TASK_CLOSED";
        }

        @Override
        public Instant occurredAt() {
            return closedAt;
        }
    }

    // ==================== Task Error Events ====================

    public record TaskFailed(
            TaskKey taskKey,
            String errorCode,
            String errorMessage,
            Instant failedAt
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return taskKey.value();
        }

        @Override
        public String eventType() {
            return "TASK_FAILED";
        }

        @Override
        public Instant occurredAt() {
            return failedAt;
        }
    }

    public record TaskRetried(
            TaskKey taskKey,
            int retryAttempt,
            int maxRetries,
            Instant retriedAt
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return taskKey.value();
        }

        @Override
        public String eventType() {
            return "TASK_RETRIED";
        }

        @Override
        public Instant occurredAt() {
            return retriedAt;
        }
    }

    // ==================== Task History Events ====================

    public record TaskHistoryRecorded(
            TaskKey taskKey,
            HistoryKey historyKey,
            String previousStatus,
            String newStatus,
            String action,
            String performedBy,
            Instant recordedAt
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return taskKey.value();
        }

        @Override
        public String eventType() {
            return "TASK_HISTORY_RECORDED";
        }

        @Override
        public Instant occurredAt() {
            return recordedAt;
        }
    }
}
