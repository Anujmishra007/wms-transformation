package com.maersk.wms.picking.shared.kernel.events;

import com.maersk.wms.picking.shared.kernel.identifiers.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
    @Builder
    @EqualsAndHashCode(callSuper = true)
    public static class PickListCreatedEvent extends AbstractPickingEvent {
        private final PickListKey listKey;
        private final String listType;
        private final String zone;
        private final int taskCount;
        private final WaveKey waveKey;
        private final LocalDateTime createdTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.LIST_MANAGEMENT;
        }
    }

    /**
     * Event raised when a pick list is assigned.
     */
    @Data
    @Builder
    @EqualsAndHashCode(callSuper = true)
    public static class PickListAssignedEvent extends AbstractPickingEvent {
        private final PickListKey listKey;
        private final UserKey userId;
        private final DeviceKey deviceId;
        private final LocalDateTime assignedTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.LIST_MANAGEMENT;
        }
    }

    /**
     * Event raised when a pick list is started.
     */
    @Data
    @Builder
    @EqualsAndHashCode(callSuper = true)
    public static class PickListStartedEvent extends AbstractPickingEvent {
        private final PickListKey listKey;
        private final UserKey userId;
        private final LocalDateTime startTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.LIST_MANAGEMENT;
        }
    }

    /**
     * Event raised when a pick list is completed.
     */
    @Data
    @Builder
    @EqualsAndHashCode(callSuper = true)
    public static class PickListCompletedEvent extends AbstractPickingEvent {
        private final PickListKey listKey;
        private final UserKey userId;
        private final int completedTasks;
        private final int shortedTasks;
        private final int skippedTasks;
        private final LocalDateTime completeTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.LIST_MANAGEMENT;
        }
    }

    /**
     * Event raised when list progress is updated.
     */
    @Data
    @Builder
    @EqualsAndHashCode(callSuper = true)
    public static class ListProgressUpdatedEvent extends AbstractPickingEvent {
        private final PickListKey listKey;
        private final int totalTasks;
        private final int completedTasks;
        private final double completionPercentage;
        private final LocalDateTime updateTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.LIST_MANAGEMENT;
        }
    }

    /**
     * Event raised when lists are merged.
     */
    @Data
    @Builder
    @EqualsAndHashCode(callSuper = true)
    public static class ListsMergedEvent extends AbstractPickingEvent {
        private final PickListKey newListKey;
        private final java.util.List<PickListKey> sourceListKeys;
        private final int totalTasksMerged;
        private final LocalDateTime mergedTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.LIST_MANAGEMENT;
        }
    }

    /**
     * Event raised when a list is split.
     */
    @Data
    @Builder
    @EqualsAndHashCode(callSuper = true)
    public static class ListSplitEvent extends AbstractPickingEvent {
        private final PickListKey sourceListKey;
        private final java.util.List<PickListKey> newListKeys;
        private final String splitCriteria;
        private final LocalDateTime splitTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.LIST_MANAGEMENT;
        }
    }
}
