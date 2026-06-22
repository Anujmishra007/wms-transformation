package com.maersk.wms.inventory.infrastructure.events;

import com.maersk.wms.inventory.domain.events.upstream.PackingOperationsEvents;
import com.maersk.wms.inventory.domain.lifecycle.model.InventoryRemoval;
import com.maersk.wms.inventory.domain.lifecycle.service.InventoryLifecycleService;
import com.maersk.wms.inventory.domain.core.model.InventoryTransaction;
import com.maersk.wms.inventory.shared.kernel.identifiers.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer for Packing Operations events.
 * Handles pack completion events to update/consume inventory.
 */
@Component
public class PackingEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(PackingEventConsumer.class);

    private final InventoryLifecycleService lifecycleService;

    public PackingEventConsumer(InventoryLifecycleService lifecycleService) {
        this.lifecycleService = lifecycleService;
    }

    @KafkaListener(
            topics = "${kafka.topics.packing-operations.pack-completed:packing-operations.pack-completed}",
            groupId = "${kafka.consumer-groups.inventory:inventory-service}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handlePackCompleted(PackingOperationsEvents.PackCompleted event) {
        log.info("Received PackCompleted event: packKey={}, orderKey={}, cartons={}",
                event.packKey(), event.orderKey(), event.cartons().size());

        try {
            for (PackingOperationsEvents.PackedCartonDetail carton : event.cartons()) {
                for (PackingOperationsEvents.PackedItemDetail item : carton.items()) {
                    // Record inventory consumption for packed items
                    InventoryRemoval removal = InventoryRemoval.builder()
                            .inventoryKey(item.sourceInventoryKey())
                            .removalQuantity(item.packedQuantity())
                            .sourceType(InventoryTransaction.TransactionSource.ORDER)
                            .sourceKey(event.orderKey().value())
                            .removalReason("Packed into carton " + carton.cartonLpn())
                            .requestedBy(event.packedBy())
                            .removalType(InventoryRemoval.RemovalType.PICK)
                            .status(InventoryRemoval.RemovalStatus.PENDING)
                            .requestedAt(event.occurredAt())
                            .build();

                    lifecycleService.remove(removal);
                    log.debug("Removed inventory for pack: inventoryKey={}, quantity={}",
                            item.sourceInventoryKey(), item.packedQuantity());
                }
            }
        } catch (Exception e) {
            log.error("Failed to process PackCompleted event: packKey={}",
                    event.packKey(), e);
            throw e;
        }
    }

    @KafkaListener(
            topics = "${kafka.topics.packing-operations.carton-closed:packing-operations.carton-closed}",
            groupId = "${kafka.consumer-groups.inventory:inventory-service}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleCartonClosed(PackingOperationsEvents.CartonClosed event) {
        log.info("Received CartonClosed event: cartonLpn={}, orderKey={}",
                event.cartonLpn(), event.orderKey());

        // Carton closure might trigger inventory status updates
        // or downstream shipping notifications
    }

    @KafkaListener(
            topics = "${kafka.topics.packing-operations.pallet-completed:packing-operations.pallet-completed}",
            groupId = "${kafka.consumer-groups.inventory:inventory-service}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handlePalletCompleted(PackingOperationsEvents.PalletCompleted event) {
        log.info("Received PalletCompleted event: palletLpn={}, cartons={}",
                event.palletLpn(), event.cartonLpns().size());

        // Pallet completion might trigger inventory hierarchy updates
        // This is handled by InventoryNestingService if needed
    }
}
