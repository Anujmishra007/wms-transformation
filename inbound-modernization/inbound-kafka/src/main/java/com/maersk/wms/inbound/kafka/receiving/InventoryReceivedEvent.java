package com.maersk.wms.inbound.kafka.receiving;

import com.maersk.wms.inbound.shared.kernel.events.AbstractInboundEvent;
import com.maersk.wms.inbound.shared.kernel.events.InboundBoundedContext;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Event published when inventory is received.
 * Consumed by inventory-service to create inventory records.
 * Part of Operations bounded context (inbound-operations-service).
 */
@Getter
public class InventoryReceivedEvent extends AbstractInboundEvent {

    private final String receiptKey;
    private final String receiptDetailKey;
    private final String storerKey;
    private final String sku;
    private final BigDecimal quantity;
    private final String uom;
    private final String lpn;
    private final String location;
    private final String lotNumber;
    private final String conditionCode;

    @Builder
    public InventoryReceivedEvent(String receiptKey, String receiptDetailKey, String storerKey,
                                   String sku, BigDecimal quantity, String uom, String lpn,
                                   String location, String lotNumber, String conditionCode) {
        super(InboundBoundedContext.OPERATIONS, receiptKey + "/" + receiptDetailKey, "ReceiptDetail");
        this.receiptKey = receiptKey;
        this.receiptDetailKey = receiptDetailKey;
        this.storerKey = storerKey;
        this.sku = sku;
        this.quantity = quantity;
        this.uom = uom;
        this.lpn = lpn;
        this.location = location;
        this.lotNumber = lotNumber;
        this.conditionCode = conditionCode;
    }

    @Override
    public String getEventType() {
        return "INVENTORY_RECEIVED";
    }
}
