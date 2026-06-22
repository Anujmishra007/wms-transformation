package com.maersk.wms.picking.shared.kernel.events;

import com.maersk.wms.picking.shared.kernel.identifiers.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * Domain events for List Management bounded context.
 */
public final class ListManagementEvents {

    private ListManagementEvents() {}

    /**
     * Event raised when a pick list is created.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class PickListCreatedEvent extends AbstractPickingEvent {
        private PickListKey listKey;
        private String listType;
        private String zone;
        private int taskCount;
        private WaveKey waveKey;
        private LocalDateTime createdTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.LIST_MANAGEMENT;
        }
    }

    /**
     * Event raised when a pick list is assigned.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class PickListAssignedEvent extends AbstractPickingEvent {
        private PickListKey listKey;
        private UserKey userId;
        private DeviceKey deviceId;
        private LocalDateTime assignedTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.LIST_MANAGEMENT;
        }
    }

    /**
     * Event raised when a pick list is started.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class PickListStartedEvent extends AbstractPickingEvent {
        private PickListKey listKey;
        private UserKey userId;
        private LocalDateTime startTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.LIST_MANAGEMENT;
        }
    }

    /**
     * Event raised when a pick list is completed.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class PickListCompletedEvent extends AbstractPickingEvent {
        private PickListKey listKey;
        private UserKey userId;
        private int completedTasks;
        private int shortedTasks;
        private int skippedTasks;
        private LocalDateTime completeTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.LIST_MANAGEMENT;
        }
    }

    /**
     * Event raised when list progress is updated.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class ListProgressUpdatedEvent extends AbstractPickingEvent {
        private PickListKey listKey;
        private int totalTasks;
        private int completedTasks;
        private double completionPercentage;
        private LocalDateTime updateTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.LIST_MANAGEMENT;
        }
    }

    /**
     * Event raised when lists are merged.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class ListsMergedEvent extends AbstractPickingEvent {
        private PickListKey newListKey;
        private java.util.List<PickListKey> sourceListKeys;
        private int totalTasksMerged;
        private LocalDateTime mergedTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.LIST_MANAGEMENT;
        }
    }

    /**
     * Event raised when a list is split.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class ListSplitEvent extends AbstractPickingEvent {
        private PickListKey sourceListKey;
        private java.util.List<PickListKey> newListKeys;
        private String splitCriteria;
        private LocalDateTime splitTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.LIST_MANAGEMENT;
        }
    }
}
