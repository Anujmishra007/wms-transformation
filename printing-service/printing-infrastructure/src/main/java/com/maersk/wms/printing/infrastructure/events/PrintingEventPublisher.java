package com.maersk.wms.printing.infrastructure.events;

import com.maersk.wms.printing.shared.kernel.events.PrintingDomainEvent;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka-based event publisher for printing domain events.
 */
@Component
public class PrintingEventPublisher {

    private static final String TOPIC_PREFIX = "printing.";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PrintingEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publish a domain event to Kafka.
     */
    public void publish(PrintingDomainEvent event) {
        String topic = TOPIC_PREFIX + event.getEventType();
        kafkaTemplate.send(topic, event);
    }

    /**
     * Publish a domain event to Kafka with a specific key.
     */
    public void publish(String key, PrintingDomainEvent event) {
        String topic = TOPIC_PREFIX + event.getEventType();
        kafkaTemplate.send(topic, key, event);
    }

    /**
     * Publish a label event.
     */
    public void publishLabelEvent(String labelKey, PrintingDomainEvent event) {
        publish(labelKey, event);
    }

    /**
     * Publish a print job event.
     */
    public void publishPrintJobEvent(String jobKey, PrintingDomainEvent event) {
        publish(jobKey, event);
    }

    /**
     * Publish a printer event.
     */
    public void publishPrinterEvent(String printerKey, PrintingDomainEvent event) {
        publish(printerKey, event);
    }
}
