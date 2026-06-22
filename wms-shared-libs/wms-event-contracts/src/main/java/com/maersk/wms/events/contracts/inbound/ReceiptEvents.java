package com.maersk.wms.events.contracts.inbound;

import com.maersk.wms.events.contracts.BaseDomainEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Event contracts for Inbound/Receipt operations.
 * Published by: inbound-service, inbound-operations-service
 * Consumed by: inventory-service, task-management-service
 */
public final class ReceiptEvents {

    private ReceiptEvents() {}

    /**
     * Published when a receipt is created.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class ReceiptCreated extends BaseDomainEvent {
        private String receiptKey;
        private String receiptNumber;
        private String storerKey;
        private String receiptType;
        private String poKey;
        private String asnKey;
        private int expectedLines;
        private Instant expectedDate;
    }

    /**
     * Published when a receipt line is received.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class ReceiptLineReceived extends BaseDomainEvent {
        private String receiptKey;
        private String lineNumber;
        private String skuKey;
        private String lotKey;
        private String lpnKey;
        private String locationKey;
        private BigDecimal receivedQuantity;
        private String uom;
        private String receivedBy;
    }

    /**
     * Published when a receipt is completed.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class ReceiptCompleted extends BaseDomainEvent {
        private String receiptKey;
        private String receiptNumber;
        private String storerKey;
        private int totalLines;
        private BigDecimal totalQuantity;
        private List<ReceivedLine> receivedLines;
        private String completedBy;
        private Instant completedAt;
    }

    /**
     * Published when putaway is completed.
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class PutawayCompleted extends BaseDomainEvent {
        private String receiptKey;
        private String lpnKey;
        private String fromLocationKey;
        private String toLocationKey;
        private String skuKey;
        private BigDecimal quantity;
        private String putawayBy;
    }

    /**
     * Line detail for receipt events.
     */
    @Data
    @NoArgsConstructor
    public static class ReceivedLine {
        private String lineNumber;
        private String skuKey;
        private String lotKey;
        private String lpnKey;
        private String locationKey;
        private BigDecimal quantity;
        private String uom;
    }
}
