package com.maersk.wms.events.contracts.picking;

import com.maersk.wms.events.contracts.BaseDomainEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Event contracts for Picking operations.
 * Published by: picking-operations-service
 * Consumed by: inventory-service, packing-operations-service, task-management-service
 */
public final class PickingEvents {

    private PickingEvents() {}

    /**
     * Published when a pick task is created.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class PickTaskCreated extends BaseDomainEvent {
        private String pickTaskKey;
        private String orderKey;
        private String waveKey;
        private String skuKey;
        private String fromLocationKey;
        private String toLocationKey;
        private BigDecimal quantity;
        private int priority;
    }

    /**
     * Published when a pick task is assigned.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class PickTaskAssigned extends BaseDomainEvent {
        private String pickTaskKey;
        private String userKey;
        private String deviceKey;
        private Instant assignedAt;
    }

    /**
     * Published when a pick is confirmed.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class PickConfirmed extends BaseDomainEvent {
        private String pickTaskKey;
        private String orderKey;
        private String inventoryKey;
        private String skuKey;
        private String locationKey;
        private String lpnKey;
        private String toLpnKey;
        private BigDecimal pickedQuantity;
        private String pickedBy;
        private Instant pickedAt;
    }

    /**
     * Published when a short pick occurs.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class ShortPickReported extends BaseDomainEvent {
        private String pickTaskKey;
        private String orderKey;
        private String skuKey;
        private String locationKey;
        private BigDecimal expectedQuantity;
        private BigDecimal actualQuantity;
        private BigDecimal shortQuantity;
        private String reason;
        private String reportedBy;
    }

    /**
     * Published when a pick list is completed.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class PickListCompleted extends BaseDomainEvent {
        private String pickListKey;
        private String waveKey;
        private int totalPicks;
        private int completedPicks;
        private int shortPicks;
        private String completedBy;
        private Instant completedAt;
    }
}
