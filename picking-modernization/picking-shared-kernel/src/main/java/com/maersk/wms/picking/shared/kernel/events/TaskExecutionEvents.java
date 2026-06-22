package com.maersk.wms.picking.shared.kernel.events;

import com.maersk.wms.picking.shared.kernel.identifiers.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Domain events for Task Execution bounded context.
 */
public final class TaskExecutionEvents {

    private TaskExecutionEvents() {}

    /**
     * Event raised when a pick session is started.
     */
    @Data
    @Builder
    @EqualsAndHashCode(callSuper = true)
    public static class SessionStartedEvent extends AbstractPickingEvent {
        private final String sessionId;
        private final UserKey userId;
        private final DeviceKey deviceId;
        private final String zone;
        private final LocalDateTime startTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.TASK_EXECUTION;
        }
    }

    /**
     * Event raised when a pick session is ended.
     */
    @Data
    @Builder
    @EqualsAndHashCode(callSuper = true)
    public static class SessionEndedEvent extends AbstractPickingEvent {
        private final String sessionId;
        private final UserKey userId;
        private final int tasksCompleted;
        private final int tasksShorted;
        private final LocalDateTime endTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.TASK_EXECUTION;
        }
    }

    /**
     * Event raised when a pick task is assigned.
     */
    @Data
    @Builder
    @EqualsAndHashCode(callSuper = true)
    public static class TaskAssignedEvent extends AbstractPickingEvent {
        private final PickTaskKey taskKey;
        private final UserKey userId;
        private final DeviceKey deviceId;
        private final PickListKey listKey;
        private final LocalDateTime assignedTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.TASK_EXECUTION;
        }
    }

    /**
     * Event raised when a pick task is started.
     */
    @Data
    @Builder
    @EqualsAndHashCode(callSuper = true)
    public static class TaskStartedEvent extends AbstractPickingEvent {
        private final PickTaskKey taskKey;
        private final UserKey userId;
        private final LocationKey location;
        private final LocalDateTime startTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.TASK_EXECUTION;
        }
    }

    /**
     * Event raised when a pick is confirmed.
     */
    @Data
    @Builder
    @EqualsAndHashCode(callSuper = true)
    public static class PickConfirmedEvent extends AbstractPickingEvent {
        private final PickTaskKey taskKey;
        private final PickDetailKey pickDetailKey;
        private final UserKey userId;
        private final BigDecimal pickedQty;
        private final LocationKey fromLocation;
        private final LpnKey fromLpn;
        private final LpnKey toLpn;
        private final LocalDateTime confirmTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.TASK_EXECUTION;
        }
    }

    /**
     * Event raised when a pick task is completed.
     */
    @Data
    @Builder
    @EqualsAndHashCode(callSuper = true)
    public static class TaskCompletedEvent extends AbstractPickingEvent {
        private final PickTaskKey taskKey;
        private final PickDetailKey pickDetailKey;
        private final UserKey userId;
        private final BigDecimal totalPicked;
        private final LocalDateTime completeTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.TASK_EXECUTION;
        }
    }

    /**
     * Event raised when a pick task is skipped.
     */
    @Data
    @Builder
    @EqualsAndHashCode(callSuper = true)
    public static class TaskSkippedEvent extends AbstractPickingEvent {
        private final PickTaskKey taskKey;
        private final UserKey userId;
        private final String reason;
        private final LocalDateTime skippedTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.TASK_EXECUTION;
        }
    }
}
