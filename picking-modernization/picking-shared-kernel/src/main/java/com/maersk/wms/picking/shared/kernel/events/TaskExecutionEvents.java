package com.maersk.wms.picking.shared.kernel.events;

import com.maersk.wms.picking.shared.kernel.identifiers.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class SessionStartedEvent extends AbstractPickingEvent {
        private String sessionId;
        private UserKey userId;
        private DeviceKey deviceId;
        private String zone;
        private LocalDateTime startTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.TASK_EXECUTION;
        }
    }

    /**
     * Event raised when a pick session is ended.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class SessionEndedEvent extends AbstractPickingEvent {
        private String sessionId;
        private UserKey userId;
        private int tasksCompleted;
        private int tasksShorted;
        private LocalDateTime endTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.TASK_EXECUTION;
        }
    }

    /**
     * Event raised when a pick task is assigned.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class TaskAssignedEvent extends AbstractPickingEvent {
        private PickTaskKey taskKey;
        private UserKey userId;
        private DeviceKey deviceId;
        private PickListKey listKey;
        private LocalDateTime assignedTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.TASK_EXECUTION;
        }
    }

    /**
     * Event raised when a pick task is started.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class TaskStartedEvent extends AbstractPickingEvent {
        private PickTaskKey taskKey;
        private UserKey userId;
        private LocationKey location;
        private LocalDateTime startTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.TASK_EXECUTION;
        }
    }

    /**
     * Event raised when a pick is confirmed.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class PickConfirmedEvent extends AbstractPickingEvent {
        private PickTaskKey taskKey;
        private PickDetailKey pickDetailKey;
        private UserKey userId;
        private BigDecimal pickedQty;
        private LocationKey fromLocation;
        private LpnKey fromLpn;
        private LpnKey toLpn;
        private LocalDateTime confirmTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.TASK_EXECUTION;
        }
    }

    /**
     * Event raised when a pick task is completed.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class TaskCompletedEvent extends AbstractPickingEvent {
        private PickTaskKey taskKey;
        private PickDetailKey pickDetailKey;
        private UserKey userId;
        private BigDecimal totalPicked;
        private LocalDateTime completeTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.TASK_EXECUTION;
        }
    }

    /**
     * Event raised when a pick task is skipped.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class TaskSkippedEvent extends AbstractPickingEvent {
        private PickTaskKey taskKey;
        private UserKey userId;
        private String reason;
        private LocalDateTime skippedTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.TASK_EXECUTION;
        }
    }
}
