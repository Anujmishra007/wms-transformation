package com.maersk.wms.inbound.kafka.receiving;

import com.maersk.wms.inbound.shared.kernel.events.AbstractInboundEvent;
import com.maersk.wms.inbound.shared.kernel.events.InboundBoundedContext;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Event published when a receipt is completed.
 * Part of Operations bounded context (inbound-operations-service).
 */
@Getter
public class ReceiptCompletedEvent extends AbstractInboundEvent {

    private final String receiptKey;
    private final String storerKey;
    private final int linesReceived;
    private final BigDecimal totalQtyReceived;
    private final LocalDateTime completedAt;
    private final boolean readyForPutaway;

    @Builder
    public ReceiptCompletedEvent(String receiptKey, String storerKey, int linesReceived,
                                  BigDecimal totalQtyReceived, LocalDateTime completedAt,
                                  boolean readyForPutaway) {
        super(InboundBoundedContext.OPERATIONS, receiptKey, "Receipt");
        this.receiptKey = receiptKey;
        this.storerKey = storerKey;
        this.linesReceived = linesReceived;
        this.totalQtyReceived = totalQtyReceived;
        this.completedAt = completedAt;
        this.readyForPutaway = readyForPutaway;
    }

    @Override
    public String getEventType() {
        return "RECEIPT_COMPLETED";
    }
}
