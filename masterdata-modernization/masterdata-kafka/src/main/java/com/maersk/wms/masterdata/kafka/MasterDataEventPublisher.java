package com.maersk.wms.masterdata.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka event publisher for master data domain events.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MasterDataEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String ITEM_TOPIC = "wms.masterdata.items";
    private static final String LOCATION_TOPIC = "wms.masterdata.locations";
    private static final String CUSTOMER_TOPIC = "wms.masterdata.customers";
    private static final String CARRIER_TOPIC = "wms.masterdata.carriers";

    public void publishItemCreated(ItemCreatedEvent event) {
        log.info("Publishing item created event: {}", event.getSku());
        kafkaTemplate.send(ITEM_TOPIC, event.getSku(), event);
    }

    public void publishItemUpdated(ItemUpdatedEvent event) {
        log.info("Publishing item updated event: {}", event.getSku());
        kafkaTemplate.send(ITEM_TOPIC, event.getSku(), event);
    }

    public void publishLocationCreated(LocationCreatedEvent event) {
        log.info("Publishing location created event: {}", event.getLocationCode());
        kafkaTemplate.send(LOCATION_TOPIC, event.getLocationCode(), event);
    }

    public void publishLocationUpdated(LocationUpdatedEvent event) {
        log.info("Publishing location updated event: {}", event.getLocationCode());
        kafkaTemplate.send(LOCATION_TOPIC, event.getLocationCode(), event);
    }

    public void publishCustomerCreated(CustomerCreatedEvent event) {
        log.info("Publishing customer created event: {}", event.getCustomerCode());
        kafkaTemplate.send(CUSTOMER_TOPIC, event.getCustomerCode(), event);
    }

    public void publishCarrierCreated(CarrierCreatedEvent event) {
        log.info("Publishing carrier created event: {}", event.getCarrierCode());
        kafkaTemplate.send(CARRIER_TOPIC, event.getCarrierCode(), event);
    }
}
