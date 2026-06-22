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
                InventoryCreation creation = new InventoryCreation(
                        line.skuKey(),
                        line.lotKey(),
                        line.locationKey(),
                        line.lpnKey(),
                        event.storerKey(),
                        event.warehouseKey(),
                        line.receivedQuantity(),
                        line.lottables(),
                        event.receiptKey(),
                        null, // returnKey
                        event.receivedBy()
                );

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
            InventoryCreation creation = new InventoryCreation(
                    event.skuKey(),
                    event.lotKey(),
                    event.locationKey(),
                    event.lpnKey(),
                    event.storerKey(),
                    event.warehouseKey(),
                    event.receivedQuantity(),
                    event.lottables(),
                    event.receiptKey(),
                    null, // returnKey
                    event.receivedBy()
            );

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
                InventoryCreation creation = new InventoryCreation(
                        line.skuKey(),
                        line.lotKey(),
                        line.locationKey(),
                        line.lpnKey(),
                        event.storerKey(),
                        event.warehouseKey(),
                        line.returnedQuantity(),
                        line.lottables(),
                        null, // receiptKey
                        event.returnKey(),
                        event.receivedBy()
                );

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
