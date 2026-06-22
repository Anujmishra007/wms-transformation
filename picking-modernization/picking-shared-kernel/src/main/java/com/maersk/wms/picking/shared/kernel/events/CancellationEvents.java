package com.maersk.wms.picking.shared.kernel.events;

import com.maersk.wms.picking.shared.kernel.identifiers.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class CancellationRequestedEvent extends AbstractPickingEvent {
        private String cancellationId;
        private String scope; // TASK, DETAIL, ORDER, WAVE, LIST
        private String targetId;
        private String reasonCode;
        private UserKey requestedBy;
        private boolean requiresApproval;
        private LocalDateTime requestedTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.CANCELLATION;
        }
    }

    /**
     * Event raised when a cancellation is approved.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class CancellationApprovedEvent extends AbstractPickingEvent {
        private String cancellationId;
        private UserKey approvedBy;
        private String notes;
        private LocalDateTime approvedTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.CANCELLATION;
        }
    }

    /**
     * Event raised when a cancellation is rejected.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class CancellationRejectedEvent extends AbstractPickingEvent {
        private String cancellationId;
        private UserKey rejectedBy;
        private String rejectionReason;
        private LocalDateTime rejectedTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.CANCELLATION;
        }
    }

    /**
     * Event raised when a cancellation is executed.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class CancellationExecutedEvent extends AbstractPickingEvent {
        private String cancellationId;
        private String scope;
        private String targetId;
        private int affectedTasks;
        private LocalDateTime executedTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.CANCELLATION;
        }
    }

    /**
     * Event raised when inventory is deallocated due to cancellation.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class InventoryDeallocatedEvent extends AbstractPickingEvent {
        private String cancellationId;
        private PickDetailKey pickDetailKey;
        private SkuKey sku;
        private LocationKey location;
        private java.math.BigDecimal deallocatedQty;
        private LocalDateTime deallocatedTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.CANCELLATION;
        }
    }
}
