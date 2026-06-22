package com.maersk.wms.picking.shared.kernel.events;

import com.maersk.wms.picking.shared.kernel.identifiers.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Domain events for Shorts Handling bounded context.
 */
public final class ShortsEvents {

    private ShortsEvents() {}

    /**
     * Event raised when a short pick is recorded.
     */
    @Data
    @Builder
    @EqualsAndHashCode(callSuper = true)
    public static class ShortPickRecordedEvent extends AbstractPickingEvent {
        private final String shortRecordId;
        private final PickDetailKey pickDetailKey;
        private final SkuKey sku;
        private final LocationKey location;
        private final BigDecimal shortedQty;
        private final String reasonCode;
        private final UserKey recordedBy;
        private final LocalDateTime recordedTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.SHORTS;
        }
    }

    /**
     * Event raised when a short pick is verified.
     */
    @Data
    @Builder
    @EqualsAndHashCode(callSuper = true)
    public static class ShortPickVerifiedEvent extends AbstractPickingEvent {
        private final String shortRecordId;
        private final boolean confirmed;
        private final UserKey verifiedBy;
        private final LocalDateTime verifiedTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.SHORTS;
        }
    }

    /**
     * Event raised when a short pick is resolved.
     */
    @Data
    @Builder
    @EqualsAndHashCode(callSuper = true)
    public static class ShortPickResolvedEvent extends AbstractPickingEvent {
        private final String shortRecordId;
        private final String resolutionAction;
        private final String notes;
        private final LocalDateTime resolvedTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.SHORTS;
        }
    }

    /**
     * Event raised when reallocation is triggered from a short.
     */
    @Data
    @Builder
    @EqualsAndHashCode(callSuper = true)
    public static class ReallocationTriggeredEvent extends AbstractPickingEvent {
        private final String shortRecordId;
        private final PickDetailKey pickDetailKey;
        private final BigDecimal requiredQty;
        private final LocalDateTime triggeredTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.SHORTS;
        }
    }

    /**
     * Event raised when replenishment is triggered from a short.
     */
    @Data
    @Builder
    @EqualsAndHashCode(callSuper = true)
    public static class ReplenishmentTriggeredEvent extends AbstractPickingEvent {
        private final String shortRecordId;
        private final LocationKey location;
        private final SkuKey sku;
        private final BigDecimal requiredQty;
        private final LocalDateTime triggeredTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.SHORTS;
        }
    }
}
