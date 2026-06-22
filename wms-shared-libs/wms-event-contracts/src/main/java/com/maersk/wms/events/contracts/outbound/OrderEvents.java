package com.maersk.wms.events.contracts.outbound;

import com.maersk.wms.events.contracts.BaseDomainEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Event contracts for Outbound/Order operations.
 * Published by: order-service, outbound operations
 * Consumed by: inventory-service, picking-operations-service, packing-operations-service
 */
public final class OrderEvents {

    private OrderEvents() {}

    /**
     * Published when an order is created.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class OrderCreated extends BaseDomainEvent {
        private String orderKey;
        private String orderNumber;
        private String storerKey;
        private String orderType;
        private int priority;
        private Instant requiredDate;
        private List<OrderLineDetail> orderLines;
    }

    /**
     * Published when an order is released for processing.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class OrderReleased extends BaseDomainEvent {
        private String orderKey;
        private String orderNumber;
        private String storerKey;
        private String waveKey;
        private Instant releasedAt;
    }

    /**
     * Published when an order is allocated.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class OrderAllocated extends BaseDomainEvent {
        private String orderKey;
        private String allocationKey;
        private boolean fullyAllocated;
        private BigDecimal allocatedQuantity;
        private BigDecimal shortageQuantity;
        private List<AllocationLineDetail> allocations;
    }

    /**
     * Published when an order is picked.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class OrderPicked extends BaseDomainEvent {
        private String orderKey;
        private String waveKey;
        private boolean fullyPicked;
        private BigDecimal pickedQuantity;
        private String pickedBy;
    }

    /**
     * Published when an order is shipped.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class OrderShipped extends BaseDomainEvent {
        private String orderKey;
        private String shipmentKey;
        private String carrier;
        private String trackingNumber;
        private List<String> cartonKeys;
        private Instant shippedAt;
    }

    /**
     * Order line detail.
     */
    @Data
    @NoArgsConstructor
    public static class OrderLineDetail {
        private String lineNumber;
        private String skuKey;
        private BigDecimal orderedQuantity;
        private String uom;
    }

    /**
     * Allocation line detail.
     */
    @Data
    @NoArgsConstructor
    public static class AllocationLineDetail {
        private String inventoryKey;
        private String locationKey;
        private String lotKey;
        private String lpnKey;
        private BigDecimal allocatedQuantity;
    }
}
