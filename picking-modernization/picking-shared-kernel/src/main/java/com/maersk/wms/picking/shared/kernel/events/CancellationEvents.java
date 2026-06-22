package com.maersk.wms.picking.shared.kernel.events;

import com.maersk.wms.picking.shared.kernel.identifiers.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * Domain events for Cancellation bounded context.
 */
public final class CancellationEvents {

    private CancellationEvents() {}

    /**
     * Event raised when a cancellation is requested.
     */
    @Data
    @Builder
    @EqualsAndHashCode(callSuper = true)
    public static class CancellationRequestedEvent extends AbstractPickingEvent {
        private final String cancellationId;
        private final String scope; // TASK, DETAIL, ORDER, WAVE, LIST
        private final String targetId;
        private final String reasonCode;
        private final UserKey requestedBy;
        private final boolean requiresApproval;
        private final LocalDateTime requestedTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.CANCELLATION;
        }
    }

    /**
     * Event raised when a cancellation is approved.
     */
    @Data
    @Builder
    @EqualsAndHashCode(callSuper = true)
    public static class CancellationApprovedEvent extends AbstractPickingEvent {
        private final String cancellationId;
        private final UserKey approvedBy;
        private final String notes;
        private final LocalDateTime approvedTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.CANCELLATION;
        }
    }

    /**
     * Event raised when a cancellation is rejected.
     */
    @Data
    @Builder
    @EqualsAndHashCode(callSuper = true)
    public static class CancellationRejectedEvent extends AbstractPickingEvent {
        private final String cancellationId;
        private final UserKey rejectedBy;
        private final String rejectionReason;
        private final LocalDateTime rejectedTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.CANCELLATION;
        }
    }

    /**
     * Event raised when a cancellation is executed.
     */
    @Data
    @Builder
    @EqualsAndHashCode(callSuper = true)
    public static class CancellationExecutedEvent extends AbstractPickingEvent {
        private final String cancellationId;
        private final String scope;
        private final String targetId;
        private final int affectedTasks;
        private final LocalDateTime executedTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.CANCELLATION;
        }
    }

    /**
     * Event raised when inventory is deallocated due to cancellation.
     */
    @Data
    @Builder
    @EqualsAndHashCode(callSuper = true)
    public static class InventoryDeallocatedEvent extends AbstractPickingEvent {
        private final String cancellationId;
        private final PickDetailKey pickDetailKey;
        private final SkuKey sku;
        private final LocationKey location;
        private final java.math.BigDecimal deallocatedQty;
        private final LocalDateTime deallocatedTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.CANCELLATION;
        }
    }
}
