package com.maersk.wms.task.domain.grouping_service.event;

import com.maersk.wms.task.shared.kernel.events.TaskDomainEvent;
import com.maersk.wms.task.shared.kernel.identifiers.*;

import java.time.Instant;
import java.util.List;

/**
 * Domain events for Task Grouping bounded context.
 * Covers wave, batch, zone, route grouping and work queues.
 */
public final class GroupingEvents {

    private GroupingEvents() {}

    // ==================== Task Group Events ====================

    public record TaskGroupCreated(
            TaskGroupKey groupKey,
            String groupType,
            String groupName,
            WaveKey waveKey,
            Instant createdAt,
            String createdBy
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return groupKey.value();
        }

        @Override
        public String eventType() {
            return "TASK_GROUP_CREATED";
        }

        @Override
        public Instant occurredAt() {
            return createdAt;
        }
    }

    public record TaskAddedToGroup(
            TaskGroupKey groupKey,
            TaskKey taskKey,
            int sequenceNumber,
            Instant addedAt,
            String addedBy
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return groupKey.value();
        }

        @Override
        public String eventType() {
            return "TASK_ADDED_TO_GROUP";
        }

        @Override
        public Instant occurredAt() {
            return addedAt;
        }
    }

    public record TaskRemovedFromGroup(
            TaskGroupKey groupKey,
            TaskKey taskKey,
            String reason,
            Instant removedAt,
            String removedBy
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return groupKey.value();
        }

        @Override
        public String eventType() {
            return "TASK_REMOVED_FROM_GROUP";
        }

        @Override
        public Instant occurredAt() {
            return removedAt;
        }
    }

    public record TaskGroupReleased(
            TaskGroupKey groupKey,
            int taskCount,
            Instant releasedAt,
            String releasedBy
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return groupKey.value();
        }

        @Override
        public String eventType() {
            return "TASK_GROUP_RELEASED";
        }

        @Override
        public Instant occurredAt() {
            return releasedAt;
        }
    }

    public record TaskGroupCompleted(
            TaskGroupKey groupKey,
            int completedTasks,
            int totalTasks,
            Instant completedAt
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return groupKey.value();
        }

        @Override
        public String eventType() {
            return "TASK_GROUP_COMPLETED";
        }

        @Override
        public Instant occurredAt() {
            return completedAt;
        }
    }

    public record TaskGroupCancelled(
            TaskGroupKey groupKey,
            String reason,
            Instant cancelledAt,
            String cancelledBy
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return groupKey.value();
        }

        @Override
        public String eventType() {
            return "TASK_GROUP_CANCELLED";
        }

        @Override
        public Instant occurredAt() {
            return cancelledAt;
        }
    }

    // ==================== Work Queue Events ====================

    public record WorkQueueCreated(
            WorkQueueKey queueKey,
            String queueName,
            String queueType,
            ZoneKey zoneKey,
            String selectionStrategy,
            Instant createdAt,
            String createdBy
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return queueKey.value();
        }

        @Override
        public String eventType() {
            return "WORK_QUEUE_CREATED";
        }

        @Override
        public Instant occurredAt() {
            return createdAt;
        }
    }

    public record TaskEnqueued(
            WorkQueueKey queueKey,
            TaskKey taskKey,
            int priority,
            int queuePosition,
            Instant enqueuedAt
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return queueKey.value();
        }

        @Override
        public String eventType() {
            return "TASK_ENQUEUED";
        }

        @Override
        public Instant occurredAt() {
            return enqueuedAt;
        }
    }

    public record TaskDequeued(
            WorkQueueKey queueKey,
            TaskKey taskKey,
            UserKey assignedTo,
            Instant dequeuedAt
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return queueKey.value();
        }

        @Override
        public String eventType() {
            return "TASK_DEQUEUED";
        }

        @Override
        public Instant occurredAt() {
            return dequeuedAt;
        }
    }

    public record TaskReprioritizedInQueue(
            WorkQueueKey queueKey,
            TaskKey taskKey,
            int oldPriority,
            int newPriority,
            int newPosition,
            Instant reprioritizedAt
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return queueKey.value();
        }

        @Override
        public String eventType() {
            return "TASK_REPRIORITIZED_IN_QUEUE";
        }

        @Override
        public Instant occurredAt() {
            return reprioritizedAt;
        }
    }

    public record WorkQueuePaused(
            WorkQueueKey queueKey,
            String reason,
            Instant pausedAt,
            String pausedBy
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return queueKey.value();
        }

        @Override
        public String eventType() {
            return "WORK_QUEUE_PAUSED";
        }

        @Override
        public Instant occurredAt() {
            return pausedAt;
        }
    }

    public record WorkQueueResumed(
            WorkQueueKey queueKey,
            Instant resumedAt,
            String resumedBy
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return queueKey.value();
        }

        @Override
        public String eventType() {
            return "WORK_QUEUE_RESUMED";
        }

        @Override
        public Instant occurredAt() {
            return resumedAt;
        }
    }

    // ==================== Batch Events ====================

    public record TasksBatched(
            TaskGroupKey batchKey,
            List<TaskKey> taskKeys,
            String batchCriteria,
            Instant batchedAt,
            String batchedBy
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return batchKey.value();
        }

        @Override
        public String eventType() {
            return "TASKS_BATCHED";
        }

        @Override
        public Instant occurredAt() {
            return batchedAt;
        }
    }

    public record BatchOptimized(
            TaskGroupKey batchKey,
            String optimizationType,
            int tasksReordered,
            Instant optimizedAt
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return batchKey.value();
        }

        @Override
        public String eventType() {
            return "BATCH_OPTIMIZED";
        }

        @Override
        public Instant occurredAt() {
            return optimizedAt;
        }
    }
}
