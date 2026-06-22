package com.maersk.wms.inbound.kafka;

import com.maersk.wms.inbound.domain.operations_service.Receipt;
import com.maersk.wms.inbound.domain.operations_service.ReceiptDetail;
import com.maersk.wms.inbound.domain.operations_service.PutawayTask;
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
        String receiptKeyValue = receipt.getReceiptKey() != null ? receipt.getReceiptKey().value() : null;
        log.info("Publishing receipt created event: {}", receiptKeyValue);
        ReceiptCreatedEvent event = ReceiptCreatedEvent.builder()
                .receiptKey(receiptKeyValue)
                .storerKey(receipt.getStorerKey() != null ? receipt.getStorerKey().value() : null)
                .receiptType(receipt.getReceiptType() != null ? receipt.getReceiptType().name() : null)
                .poKey(receipt.getPoKey())
                .asnKey(receipt.getAsnKey())
                .status(receipt.getStatus() != null ? receipt.getStatus().getCode() : null)
                .build();
        kafkaTemplate.send(TOPIC_RECEIPT_CREATED, receiptKeyValue, event);
    }

    /**
     * Publish receipt closed event.
     */
    public void publishReceiptClosed(Receipt receipt) {
        String receiptKeyValue = receipt.getReceiptKey() != null ? receipt.getReceiptKey().value() : null;
        log.info("Publishing receipt closed event: {}", receiptKeyValue);
        ReceiptClosedEvent event = ReceiptClosedEvent.builder()
                .receiptKey(receiptKeyValue)
                .storerKey(receipt.getStorerKey() != null ? receipt.getStorerKey().value() : null)
                .totalReceivedQty(receipt.getReceivedQty())
                .closedDate(receipt.getCloseDate())
                .build();
        kafkaTemplate.send(TOPIC_RECEIPT_CLOSED, receiptKeyValue, event);
    }

    /**
     * Publish inventory received event.
     */
    public void publishInventoryReceived(ReceiptDetail detail) {
        String receiptKeyValue = detail.getReceipt() != null && detail.getReceipt().getReceiptKey() != null
                ? detail.getReceipt().getReceiptKey().value() : null;
        String skuValue = detail.getSku() != null ? detail.getSku().value() : null;
        log.info("Publishing inventory received event: {} - {}", receiptKeyValue, skuValue);
        InventoryReceivedEvent event = InventoryReceivedEvent.builder()
                .receiptKey(receiptKeyValue)
                .lineNumber(detail.getLineNumber())
                .sku(skuValue)
                .lot(detail.getLotAttributes() != null ? detail.getLotAttributes().getLot() : null)
                .lpn(detail.getLpn() != null ? detail.getLpn().value() : null)
                .location(detail.getReceiveLocation())
                .receivedQty(detail.getReceivedQty())
                .damagedQty(detail.getDamagedQty())
                .build();
        kafkaTemplate.send(TOPIC_INVENTORY_RECEIVED, receiptKeyValue, event);
    }

    /**
     * Publish putaway task created event.
     */
    public void publishPutawayCreated(PutawayTask task) {
        log.info("Publishing putaway created event: {}", task.getPutawayKey());
        PutawayCreatedEvent event = PutawayCreatedEvent.builder()
                .taskKey(task.getPutawayKey())
                .receiptKey(task.getReceiptKey() != null ? task.getReceiptKey().value() : null)
                .sku(task.getSkuKey() != null ? task.getSkuKey().value() : null)
                .fromLocation(task.getFromLocation() != null ? task.getFromLocation().value() : null)
                .toLocation(task.getToLocation() != null ? task.getToLocation().value() : null)
                .qty(task.getQuantity() != null ? task.getQuantity().getValue() : null)
                .build();
        kafkaTemplate.send(TOPIC_PUTAWAY_CREATED, task.getPutawayKey(), event);
    }

    /**
     * Publish putaway completed event.
     */
    public void publishPutawayCompleted(PutawayTask task) {
        log.info("Publishing putaway completed event: {}", task.getPutawayKey());
        PutawayCompletedEvent event = PutawayCompletedEvent.builder()
                .taskKey(task.getPutawayKey())
                .receiptKey(task.getReceiptKey() != null ? task.getReceiptKey().value() : null)
                .sku(task.getSkuKey() != null ? task.getSkuKey().value() : null)
                .toLocation(task.getToLocation() != null ? task.getToLocation().value() : null)
                .toLpn(task.getTargetLpn() != null ? task.getTargetLpn().value() : null)
                .qty(task.getQuantity() != null ? task.getQuantity().getValue() : null)
                .completedDate(task.getCompletedAt() != null ? java.time.LocalDateTime.ofInstant(task.getCompletedAt(), java.time.ZoneId.systemDefault()) : null)
                .build();
        kafkaTemplate.send(TOPIC_PUTAWAY_COMPLETED, task.getPutawayKey(), event);
    }
}
