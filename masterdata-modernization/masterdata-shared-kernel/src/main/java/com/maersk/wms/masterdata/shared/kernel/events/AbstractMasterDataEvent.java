package com.maersk.wms.masterdata.shared.kernel.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Abstract base class for master data events with common metadata.
 */
public abstract class AbstractMasterDataEvent implements MasterDataDomainEvent {

    private final String eventId;
    private final Instant occurredAt;
    private final String correlationId;
    private final String causationId;

    protected AbstractMasterDataEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.occurredAt = Instant.now();
        this.correlationId = null;
        this.causationId = null;
    }

    protected AbstractMasterDataEvent(String correlationId, String causationId) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredAt = Instant.now();
        this.correlationId = correlationId;
        this.causationId = causationId;
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

    public String getCausationId() {
        return causationId;
    }
}
