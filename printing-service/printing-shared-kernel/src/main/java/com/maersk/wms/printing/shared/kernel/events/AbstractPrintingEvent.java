package com.maersk.wms.printing.shared.kernel.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Abstract base class for printing events with common metadata.
 */
public abstract class AbstractPrintingEvent implements PrintingDomainEvent {

    private final String eventId;
    private final Instant occurredAt;
    private final String correlationId;

    protected AbstractPrintingEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.occurredAt = Instant.now();
        this.correlationId = null;
    }

    protected AbstractPrintingEvent(String correlationId) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredAt = Instant.now();
        this.correlationId = correlationId;
    }

    public String getEventId() {
        return eventId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    public String getCorrelationId() {
        return correlationId;
    }
}
