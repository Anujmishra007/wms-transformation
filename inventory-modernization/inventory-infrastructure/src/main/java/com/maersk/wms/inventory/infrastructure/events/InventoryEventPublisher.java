package com.maersk.wms.inventory.infrastructure.events;

import com.maersk.wms.inventory.domain.events.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Publishes inventory domain events to Kafka for downstream consumers.
 * Bridges Spring application events to Kafka topics.
 */
@Component
public class InventoryEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(InventoryEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    // Topic names - should be externalized to configuration
    private static final String TOPIC_INVENTORY_CREATED = "inventory.created";
    private static final String TOPIC_INVENTORY_CHANGED = "inventory.changed";
    private static final String TOPIC_INVENTORY_DEPLETED = "inventory.depleted";
    private static final String TOPIC_INVENTORY_SHIPPED = "inventory.shipped";
    private static final String TOPIC_INVENTORY_ALLOCATED = "inventory.allocated";
    private static final String TOPIC_INVENTORY_DEALLOCATED = "inventory.deallocated";
    private static final String TOPIC_INVENTORY_SHORTAGE = "inventory.shortage";
    private static final String TOPIC_INVENTORY_HOLD = "inventory.hold";
    private static final String TOPIC_INVENTORY_MOVEMENT = "inventory.movement";
    private static final String TOPIC_INVENTORY_COUNT = "inventory.count";
    private static final String TOPIC_INVENTORY_NESTING = "inventory.nesting";

    public InventoryEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // ═══════════════════════════════════════════════════════════════
    // LIFECYCLE EVENTS
    // ═══════════════════════════════════════════════════════════════

    @EventListener
    public void handleInventoryCreated(InventoryLifecycleEvents.InventoryCreated event) {
        publishEvent(TOPIC_INVENTORY_CREATED, event.inventoryKey().value(), event);
    }

    @EventListener
    public void handleInventoryQuantityChanged(InventoryLifecycleEvents.InventoryQuantityChanged event) {
        publishEvent(TOPIC_INVENTORY_CHANGED, event.inventoryKey().value(), event);
    }

    @EventListener
    public void handleInventoryDepleted(InventoryLifecycleEvents.InventoryDepleted event) {
        publishEvent(TOPIC_INVENTORY_DEPLETED, event.inventoryKey().value(), event);
    }

    @EventListener
    public void handleInventoryShipped(InventoryLifecycleEvents.InventoryShipped event) {
        publishEvent(TOPIC_INVENTORY_SHIPPED, event.inventoryKey().value(), event);
    }

    // ═══════════════════════════════════════════════════════════════
    // ALLOCATION EVENTS
    // ═══════════════════════════════════════════════════════════════

    @EventListener
    public void handleInventoryAllocated(AllocationEvents.InventoryAllocated event) {
        publishEvent(TOPIC_INVENTORY_ALLOCATED, event.allocationKey().value(), event);
    }

    @EventListener
    public void handleInventoryDeallocated(AllocationEvents.InventoryDeallocated event) {
        publishEvent(TOPIC_INVENTORY_DEALLOCATED, event.allocationKey().value(), event);
    }

    @EventListener
    public void handleAllocationShortage(AllocationEvents.AllocationShortage event) {
        String key = event.orderKey() != null ? event.orderKey().value() :
                event.skuKey().value();
        publishEvent(TOPIC_INVENTORY_SHORTAGE, key, event);
    }

    // ═══════════════════════════════════════════════════════════════
    // HOLD EVENTS
    // ═══════════════════════════════════════════════════════════════

    @EventListener
    public void handleHoldApplied(HoldEvents.HoldApplied event) {
        publishEvent(TOPIC_INVENTORY_HOLD, event.inventoryKey().value(), event);
    }

    @EventListener
    public void handleHoldReleased(HoldEvents.HoldReleased event) {
        publishEvent(TOPIC_INVENTORY_HOLD, event.inventoryKey().value(), event);
    }

    // ═══════════════════════════════════════════════════════════════
    // MOVEMENT EVENTS
    // ═══════════════════════════════════════════════════════════════

    @EventListener
    public void handleInventoryTransferred(MovementEvents.InventoryTransferred event) {
        publishEvent(TOPIC_INVENTORY_MOVEMENT, event.inventoryKey().value(), event);
    }

    @EventListener
    public void handlePutawayCompleted(MovementEvents.PutawayCompleted event) {
        publishEvent(TOPIC_INVENTORY_MOVEMENT, event.inventoryKey().value(), event);
    }

    @EventListener
    public void handleReplenishmentCompleted(MovementEvents.ReplenishmentCompleted event) {
        publishEvent(TOPIC_INVENTORY_MOVEMENT, event.toInventoryKey().value(), event);
    }

    // ═══════════════════════════════════════════════════════════════
    // COUNT EVENTS
    // ═══════════════════════════════════════════════════════════════

    @EventListener
    public void handleCycleCountCompleted(CountEvents.CycleCountCompleted event) {
        publishEvent(TOPIC_INVENTORY_COUNT, event.countKey().value(), event);
    }

    @EventListener
    public void handleCountVarianceDetected(CountEvents.CountVarianceDetected event) {
        publishEvent(TOPIC_INVENTORY_COUNT, event.countKey().value(), event);
    }

    @EventListener
    public void handleCountTypeCreated(CountEvents.CountTypeCreated event) {
        publishEvent(TOPIC_INVENTORY_COUNT, event.countTypeKey().value(), event);
    }

    // ═══════════════════════════════════════════════════════════════
    // NESTING EVENTS
    // ═══════════════════════════════════════════════════════════════

    @EventListener
    public void handleInventoryNested(NestingEvents.InventoryNested event) {
        publishEvent(TOPIC_INVENTORY_NESTING, event.parentLpnKey().value(), event);
    }

    @EventListener
    public void handleInventoryUnnested(NestingEvents.InventoryUnnested event) {
        publishEvent(TOPIC_INVENTORY_NESTING, event.parentLpnKey().value(), event);
    }

    @EventListener
    public void handlePalletBuilt(NestingEvents.PalletBuilt event) {
        publishEvent(TOPIC_INVENTORY_NESTING, event.palletLpnKey().value(), event);
    }

    @EventListener
    public void handlePalletBroken(NestingEvents.PalletBroken event) {
        publishEvent(TOPIC_INVENTORY_NESTING, event.palletLpnKey().value(), event);
    }

    // ═══════════════════════════════════════════════════════════════
    // HELPER METHODS
    // ═══════════════════════════════════════════════════════════════

    private void publishEvent(String topic, String key, Object event) {
        try {
            kafkaTemplate.send(topic, key, event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish event to topic {}: key={}, event={}",
                                    topic, key, event.getClass().getSimpleName(), ex);
                        } else {
                            log.debug("Published event to topic {}: key={}, event={}, partition={}, offset={}",
                                    topic, key, event.getClass().getSimpleName(),
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset());
                        }
                    });
        } catch (Exception e) {
            log.error("Error sending event to Kafka: topic={}, key={}", topic, key, e);
            throw e;
        }
    }
}
