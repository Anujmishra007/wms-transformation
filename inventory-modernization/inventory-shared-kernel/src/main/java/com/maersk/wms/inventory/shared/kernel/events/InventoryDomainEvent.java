package com.maersk.wms.inventory.shared.kernel.events;

import java.time.Instant;

/**
 * Base interface for all inventory domain events.
 * Supports event-driven architecture with upstream and downstream services.
 */
public interface InventoryDomainEvent {

    /**
     * Get the event type identifier.
     */
    String getEventType();

    /**
     * Get the timestamp when the event occurred.
     */
    default Instant getOccurredAt() {
        return Instant.now();
    }

    /**
     * Get the aggregate ID this event relates to.
     */
    default String getAggregateId() {
        return null;
    }

    /**
     * Get the correlation ID for distributed tracing.
     */
    default String getCorrelationId() {
        return null;
    }
}
