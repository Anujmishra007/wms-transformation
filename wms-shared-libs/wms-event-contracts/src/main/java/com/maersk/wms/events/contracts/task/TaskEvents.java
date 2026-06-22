package com.maersk.wms.events.contracts.task;

import com.maersk.wms.events.contracts.BaseDomainEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.Map;

/**
 * Event contracts for Task Management operations.
 * Published by: task-management-service
 * Consumed by: picking-operations-service, packing-operations-service,
 *              inbound-operations-service
 */
public final class TaskEvents {

    private TaskEvents() {}

    /**
     * Published when a task is created.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class TaskCreated extends BaseDomainEvent {
        private String taskKey;
        private String taskType;
        private String sourceType;
        private String sourceKey;
        private int priority;
        private String zoneKey;
        private Map<String, String> attributes;
    }

    /**
     * Published when a task is assigned.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class TaskAssigned extends BaseDomainEvent {
        private String taskKey;
        private String userKey;
        private String deviceKey;
        private Instant assignedAt;
    }

    /**
     * Published when a task is started.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class TaskStarted extends BaseDomainEvent {
        private String taskKey;
        private String userKey;
        private Instant startedAt;
    }

    /**
     * Published when a task is completed.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class TaskCompleted extends BaseDomainEvent {
        private String taskKey;
        private String userKey;
        private String result;
        private long durationMs;
        private Instant completedAt;
    }

    /**
     * Published when a task is cancelled.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class TaskCancelled extends BaseDomainEvent {
        private String taskKey;
        private String reason;
        private String cancelledBy;
        private Instant cancelledAt;
    }

    /**
     * Published when a task is interleaved.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class TaskInterleaved extends BaseDomainEvent {
        private String primaryTaskKey;
        private String interleavedTaskKey;
        private String userKey;
        private String reason;
    }
}
