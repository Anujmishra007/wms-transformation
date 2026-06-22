package com.maersk.wms.events.publishing;

import com.maersk.wms.events.contracts.DomainEvent;

import java.util.concurrent.CompletableFuture;

/**
 * Interface for publishing domain events to the message broker.
 * Implementations handle the actual transport (Kafka, RabbitMQ, etc.)
 */
public interface EventPublisher {

    /**
     * Publish a domain event asynchronously.
     *
     * @param event The domain event to publish
     * @return CompletableFuture that completes when the event is acknowledged
     */
    CompletableFuture<Void> publish(DomainEvent event);

    /**
     * Publish a domain event to a specific topic.
     *
     * @param topic The target topic name
     * @param event The domain event to publish
     * @return CompletableFuture that completes when the event is acknowledged
     */
    CompletableFuture<Void> publish(String topic, DomainEvent event);

    /**
     * Publish a domain event synchronously (blocking).
     *
     * @param event The domain event to publish
     * @throws EventPublishException if publishing fails
     */
    void publishSync(DomainEvent event);
}
