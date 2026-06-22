package com.maersk.wms.inbound.kafka;

import com.maersk.wms.inbound.domain.Receipt;
import com.maersk.wms.inbound.domain.ReceiptDetail;
import com.maersk.wms.inbound.domain.PutawayTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka event publisher for inbound domain events.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InboundEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC_RECEIPT_CREATED = "wms.inbound.receipt.created";
    private static final String TOPIC_RECEIPT_CLOSED = "wms.inbound.receipt.closed";
    private static final String TOPIC_INVENTORY_RECEIVED = "wms.inbound.inventory.received";
    private static final String TOPIC_PUTAWAY_CREATED = "wms.inbound.putaway.created";
    private static final String TOPIC_PUTAWAY_COMPLETED = "wms.inbound.putaway.completed";

    /**
     * Publish receipt created event.
     */
    public void publishReceiptCreated(Receipt receipt) {
        log.info("Publishing receipt created event: {}", receipt.getReceiptKey());
        ReceiptCreatedEvent event = ReceiptCreatedEvent.builder()
                .receiptKey(receipt.getReceiptKey())
                .storerKey(receipt.getStorerKey())
                .receiptType(receipt.getReceiptType())
                .poKey(receipt.getPoKey())
                .asnKey(receipt.getAsnKey())
                .status(receipt.getStatus().getCode())
                .build();
        kafkaTemplate.send(TOPIC_RECEIPT_CREATED, receipt.getReceiptKey(), event);
    }

    /**
     * Publish receipt closed event.
     */
    public void publishReceiptClosed(Receipt receipt) {
        log.info("Publishing receipt closed event: {}", receipt.getReceiptKey());
        ReceiptClosedEvent event = ReceiptClosedEvent.builder()
                .receiptKey(receipt.getReceiptKey())
                .storerKey(receipt.getStorerKey())
                .totalReceivedQty(receipt.getTotalReceivedQty())
                .totalDamagedQty(receipt.getTotalDamagedQty())
                .variance(receipt.getTotalVariance())
                .closedDate(receipt.getClosedDate())
                .build();
        kafkaTemplate.send(TOPIC_RECEIPT_CLOSED, receipt.getReceiptKey(), event);
    }

    /**
     * Publish inventory received event.
     */
    public void publishInventoryReceived(ReceiptDetail detail) {
        log.info("Publishing inventory received event: {} - {}", detail.getReceiptKey(), detail.getSku());
        InventoryReceivedEvent event = InventoryReceivedEvent.builder()
                .receiptKey(detail.getReceiptKey())
                .lineNumber(detail.getReceiptLineNumber())
                .sku(detail.getSku())
                .lot(detail.getLot())
                .lpn(detail.getId())
                .location(detail.getLocation())
                .receivedQty(detail.getReceivedQty())
                .damagedQty(detail.getDamagedQty())
                .build();
        kafkaTemplate.send(TOPIC_INVENTORY_RECEIVED, detail.getReceiptKey(), event);
    }

    /**
     * Publish putaway task created event.
     */
    public void publishPutawayCreated(PutawayTask task) {
        log.info("Publishing putaway created event: {}", task.getTaskKey());
        PutawayCreatedEvent event = PutawayCreatedEvent.builder()
                .taskKey(task.getTaskKey())
                .receiptKey(task.getReceiptKey())
                .sku(task.getSku())
                .fromLocation(task.getFromLocation())
                .toLocation(task.getToLocation())
                .qty(task.getQty())
                .build();
        kafkaTemplate.send(TOPIC_PUTAWAY_CREATED, task.getTaskKey(), event);
    }

    /**
     * Publish putaway completed event.
     */
    public void publishPutawayCompleted(PutawayTask task) {
        log.info("Publishing putaway completed event: {}", task.getTaskKey());
        PutawayCompletedEvent event = PutawayCompletedEvent.builder()
                .taskKey(task.getTaskKey())
                .receiptKey(task.getReceiptKey())
                .sku(task.getSku())
                .toLocation(task.getToLocation())
                .toLpn(task.getToLpn())
                .qty(task.getQty())
                .completedDate(task.getCompletedDate())
                .build();
        kafkaTemplate.send(TOPIC_PUTAWAY_COMPLETED, task.getTaskKey(), event);
    }
}
