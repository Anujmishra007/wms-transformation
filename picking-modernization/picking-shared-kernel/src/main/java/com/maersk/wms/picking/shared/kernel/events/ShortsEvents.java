package com.maersk.wms.picking.shared.kernel.events;

import com.maersk.wms.picking.shared.kernel.identifiers.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class ShortPickRecordedEvent extends AbstractPickingEvent {
        private String shortRecordId;
        private PickDetailKey pickDetailKey;
        private SkuKey sku;
        private LocationKey location;
        private BigDecimal shortedQty;
        private String reasonCode;
        private UserKey recordedBy;
        private LocalDateTime recordedTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.SHORTS;
        }
    }

    /**
     * Event raised when a short pick is verified.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class ShortPickVerifiedEvent extends AbstractPickingEvent {
        private String shortRecordId;
        private boolean confirmed;
        private UserKey verifiedBy;
        private LocalDateTime verifiedTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.SHORTS;
        }
    }

    /**
     * Event raised when a short pick is resolved.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class ShortPickResolvedEvent extends AbstractPickingEvent {
        private String shortRecordId;
        private String resolutionAction;
        private String notes;
        private LocalDateTime resolvedTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.SHORTS;
        }
    }

    /**
     * Event raised when reallocation is triggered from a short.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class ReallocationTriggeredEvent extends AbstractPickingEvent {
        private String shortRecordId;
        private PickDetailKey pickDetailKey;
        private BigDecimal requiredQty;
        private LocalDateTime triggeredTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.SHORTS;
        }
    }

    /**
     * Event raised when replenishment is triggered from a short.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class ReplenishmentTriggeredEvent extends AbstractPickingEvent {
        private String shortRecordId;
        private LocationKey location;
        private SkuKey sku;
        private BigDecimal requiredQty;
        private LocalDateTime triggeredTime;

        @Override
        public PickingBoundedContext getBoundedContext() {
            return PickingBoundedContext.SHORTS;
        }
    }
}
