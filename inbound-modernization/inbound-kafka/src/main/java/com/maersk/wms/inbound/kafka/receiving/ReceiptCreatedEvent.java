package com.maersk.wms.inbound.kafka.receiving;

import com.maersk.wms.inbound.shared.kernel.events.AbstractInboundEvent;
import com.maersk.wms.inbound.shared.kernel.events.InboundBoundedContext;
import lombok.Builder;
import lombok.Getter;

/**
 * Event published when a receipt is created.
 * Part of Operations bounded context (inbound-operations-service).
 */
@Getter
public class ReceiptCreatedEvent extends AbstractInboundEvent {

    private final String receiptKey;
    private final String receiptType;
    private final String storerKey;
    private final String asnKey;
    private final String poKey;
    private final String facility;
    private final int expectedLines;

    @Builder
    public ReceiptCreatedEvent(String receiptKey, String receiptType, String storerKey,
                               String asnKey, String poKey, String facility, int expectedLines) {
        super(InboundBoundedContext.OPERATIONS, receiptKey, "Receipt");
        this.receiptKey = receiptKey;
        this.receiptType = receiptType;
        this.storerKey = storerKey;
        this.asnKey = asnKey;
        this.poKey = poKey;
        this.facility = facility;
        this.expectedLines = expectedLines;
    }

    @Override
    public String getEventType() {
        return "RECEIPT_CREATED";
    }
}
