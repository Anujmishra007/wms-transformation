package com.maersk.wms.inventory.shared.kernel.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Abstract base class for inventory domain events.
 * Provides common fields and functionality.
 */
public abstract class AbstractInventoryEvent implements InventoryDomainEvent {

    private final String eventId;
    private final Instant occurredAt;
    private final String correlationId;
    private final String causationId;

    protected AbstractInventoryEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.occurredAt = Instant.now();
        this.correlationId = null;
        this.causationId = null;
    }

    protected AbstractInventoryEvent(String correlationId, String causationId) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredAt = Instant.now();
        this.correlationId = correlationId;
        this.causationId = causationId;
    }

    public String getEventId() {
        return eventId;
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }

    @Override
    public String getCorrelationId() {
        return correlationId;
    }

    public String getCausationId() {
        return causationId;
    }
}
