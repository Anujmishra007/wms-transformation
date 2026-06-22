package com.maersk.wms.inventory.infrastructure.events;

import com.maersk.wms.inventory.domain.events.upstream.InboundOperationsEvents;
import com.maersk.wms.inventory.domain.lifecycle.model.InventoryCreation;
import com.maersk.wms.inventory.domain.lifecycle.service.InventoryLifecycleService;
import com.maersk.wms.inventory.shared.kernel.identifiers.*;
import com.maersk.wms.inventory.shared.kernel.valueobjects.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer for Inbound Operations events.
 * Handles receipt completion events to create inventory.
 */
@Component
public class InboundEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(InboundEventConsumer.class);

    private final InventoryLifecycleService lifecycleService;

    public InboundEventConsumer(InventoryLifecycleService lifecycleService) {
        this.lifecycleService = lifecycleService;
    }

    @KafkaListener(
            topics = "${kafka.topics.inbound-operations.receipt-completed:inbound-operations.receipt-completed}",
            groupId = "${kafka.consumer-groups.inventory:inventory-service}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleReceiptCompleted(InboundOperationsEvents.ReceiptCompleted event) {
        log.info("Received ReceiptCompleted event: receiptKey={}, lines={}",
                event.receiptKey(), event.receiptLines().size());

        try {
            for (InboundOperationsEvents.ReceiptLineDetail line : event.receiptLines()) {
                InventoryCreation creation = InventoryCreation.builder()
                        .source(InventoryCreation.CreationSource.RECEIPT)
                        .sourceKey(event.receiptKey().value())
                        .sourceLineNumber(line.lineNumber())
                        .skuKey(line.skuKey())
                        .lotKey(line.lotKey())
                        .locationKey(line.locationKey())
                        .lpnKey(line.lpnKey())
                        .storerKey(event.storerKey())
                        .warehouseKey(event.warehouseKey())
                        .quantity(line.receivedQuantity())
                        .lottables(line.lottables())
                        .receiptKey(event.receiptKey())
                        .requestedBy(event.receivedBy())
                        .requestedAt(event.occurredAt())
                        .status(InventoryCreation.CreationStatus.PENDING)
                        .build();

                lifecycleService.createFromReceipt(creation);
                log.debug("Created inventory from receipt line: receiptKey={}, line={}",
                        event.receiptKey(), line.lineNumber());
            }
        } catch (Exception e) {
            log.error("Failed to process ReceiptCompleted event: receiptKey={}",
                    event.receiptKey(), e);
            throw e; // Re-throw to trigger Kafka retry
        }
    }

    @KafkaListener(
            topics = "${kafka.topics.inbound-operations.receipt-line-received:inbound-operations.receipt-line-received}",
            groupId = "${kafka.consumer-groups.inventory:inventory-service}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleReceiptLineReceived(InboundOperationsEvents.ReceiptLineReceived event) {
        log.info("Received ReceiptLineReceived event: receiptKey={}, lineNumber={}, quantity={}",
                event.receiptKey(), event.lineNumber(), event.receivedQuantity());

        try {
            InventoryCreation creation = InventoryCreation.builder()
                    .source(InventoryCreation.CreationSource.RECEIPT)
                    .sourceKey(event.receiptKey().value())
                    .sourceLineNumber(event.lineNumber())
                    .skuKey(event.skuKey())
                    .lotKey(event.lotKey())
                    .locationKey(event.locationKey())
                    .lpnKey(event.lpnKey())
                    .storerKey(event.storerKey())
                    .warehouseKey(event.warehouseKey())
                    .quantity(event.receivedQuantity())
                    .lottables(event.lottables())
                    .receiptKey(event.receiptKey())
                    .requestedBy(event.receivedBy())
                    .requestedAt(event.occurredAt())
                    .status(InventoryCreation.CreationStatus.PENDING)
                    .build();

            lifecycleService.createFromReceipt(creation);
            log.debug("Created inventory from receipt line: receiptKey={}, line={}",
                    event.receiptKey(), event.lineNumber());
        } catch (Exception e) {
            log.error("Failed to process ReceiptLineReceived event: receiptKey={}, line={}",
                    event.receiptKey(), event.lineNumber(), e);
            throw e;
        }
    }

    @KafkaListener(
            topics = "${kafka.topics.inbound-operations.putaway-completed:inbound-operations.putaway-completed}",
            groupId = "${kafka.consumer-groups.inventory:inventory-service}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handlePutawayCompleted(InboundOperationsEvents.PutawayCompleted event) {
        log.info("Received PutawayCompleted event: inventoryKey={}, toLocation={}",
                event.inventoryKey(), event.toLocation());

        // Putaway completion might trigger location update or movement tracking
        // The actual inventory was already created during receipt
        // This event can be used for audit or downstream notifications
    }

    @KafkaListener(
            topics = "${kafka.topics.inbound-operations.return-received:inbound-operations.return-received}",
            groupId = "${kafka.consumer-groups.inventory:inventory-service}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleReturnReceived(InboundOperationsEvents.ReturnReceived event) {
        log.info("Received ReturnReceived event: returnKey={}, originalOrder={}",
                event.returnKey(), event.originalOrderKey());

        try {
            for (InboundOperationsEvents.ReturnLineDetail line : event.returnLines()) {
                InventoryCreation creation = InventoryCreation.builder()
                        .source(InventoryCreation.CreationSource.RETURN_RECEIPT)
                        .sourceKey(event.returnKey())
                        .sourceLineNumber(line.lineNumber())
                        .skuKey(line.skuKey())
                        .lotKey(line.lotKey())
                        .locationKey(line.locationKey())
                        .lpnKey(line.lpnKey())
                        .storerKey(event.storerKey())
                        .warehouseKey(event.warehouseKey())
                        .quantity(line.returnedQuantity())
                        .lottables(line.lottables())
                        .requestedBy(event.receivedBy())
                        .requestedAt(event.occurredAt())
                        .status(InventoryCreation.CreationStatus.PENDING)
                        .build();

                lifecycleService.createFromReturn(creation);
                log.debug("Created inventory from return: returnKey={}, line={}",
                        event.returnKey(), line.lineNumber());
            }
        } catch (Exception e) {
            log.error("Failed to process ReturnReceived event: returnKey={}",
                    event.returnKey(), e);
            throw e;
        }
    }
}
