package com.maersk.wms.outbound.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka event publisher for outbound domain events.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OutboundEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String ORDER_TOPIC = "wms.outbound.orders";
    private static final String ALLOCATION_TOPIC = "wms.outbound.allocations";
    private static final String WAVE_TOPIC = "wms.outbound.waves";
    private static final String SHIPMENT_TOPIC = "wms.outbound.shipments";

    /**
     * Publish order created event.
     */
    public void publishOrderCreated(OrderCreatedEvent event) {
        log.info("Publishing order created event: {}", event.getOrderNumber());
        kafkaTemplate.send(ORDER_TOPIC, event.getOrderNumber(), event);
    }

    /**
     * Publish order allocated event.
     */
    public void publishOrderAllocated(OrderAllocatedEvent event) {
        log.info("Publishing order allocated event: {}", event.getOrderNumber());
        kafkaTemplate.send(ALLOCATION_TOPIC, event.getOrderNumber(), event);
    }

    /**
     * Publish order released event.
     */
    public void publishOrderReleased(OrderReleasedEvent event) {
        log.info("Publishing order released event: {}", event.getOrderNumber());
        kafkaTemplate.send(ORDER_TOPIC, event.getOrderNumber(), event);
    }

    /**
     * Publish wave created event.
     */
    public void publishWaveCreated(WaveCreatedEvent event) {
        log.info("Publishing wave created event: {}", event.getWaveNumber());
        kafkaTemplate.send(WAVE_TOPIC, event.getWaveNumber(), event);
    }

    /**
     * Publish wave released event.
     */
    public void publishWaveReleased(WaveReleasedEvent event) {
        log.info("Publishing wave released event: {}", event.getWaveNumber());
        kafkaTemplate.send(WAVE_TOPIC, event.getWaveNumber(), event);
    }

    /**
     * Publish shipment created event.
     */
    public void publishShipmentCreated(ShipmentCreatedEvent event) {
        log.info("Publishing shipment created event: {}", event.getShipmentId());
        kafkaTemplate.send(SHIPMENT_TOPIC, event.getShipmentId(), event);
    }

    /**
     * Publish shipment shipped event.
     */
    public void publishShipmentShipped(ShipmentShippedEvent event) {
        log.info("Publishing shipment shipped event: {}", event.getShipmentId());
        kafkaTemplate.send(SHIPMENT_TOPIC, event.getShipmentId(), event);
    }
}
