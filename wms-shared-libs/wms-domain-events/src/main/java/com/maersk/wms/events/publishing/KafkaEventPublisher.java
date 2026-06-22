package com.maersk.wms.events.publishing;

import com.maersk.wms.events.contracts.DomainEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka-based implementation of EventPublisher.
 * Routes events to topics based on event type and tenant context.
 */
public class KafkaEventPublisher implements EventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final EventTopicResolver topicResolver;

    public KafkaEventPublisher(KafkaTemplate<String, Object> kafkaTemplate, EventTopicResolver topicResolver) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicResolver = topicResolver;
    }

    @Override
    public CompletableFuture<Void> publish(DomainEvent event) {
        String topic = topicResolver.resolveTopic(event);
        return publish(topic, event);
    }

    @Override
    public CompletableFuture<Void> publish(String topic, DomainEvent event) {
        String key = buildPartitionKey(event);

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, key, event);

        return future
                .thenApply(result -> (Void) null)
                .exceptionally(ex -> {
                    throw new EventPublishException(
                            event.getEventType(),
                            event.getEventId().toString(),
                            "Kafka send failed",
                            ex
                    );
                });
    }

    @Override
    public void publishSync(DomainEvent event) {
        try {
            publish(event).get();
        } catch (Exception e) {
            throw new EventPublishException(
                    event.getEventType(),
                    event.getEventId().toString(),
                    "Synchronous publish failed",
                    e
            );
        }
    }

    /**
     * Build partition key for consistent ordering.
     * Events for the same tenant/warehouse go to the same partition.
     */
    private String buildPartitionKey(DomainEvent event) {
        return String.format("%s:%s:%s",
                event.getClientCode(),
                event.getCountryCode(),
                event.getWarehouseCode()
        );
    }
}
