package com.maersk.wms.inbound.shared.kernel.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Abstract base class for inbound domain events.
 * Provides common event infrastructure.
 *
 * Part of Shared Kernel - extend this for specific domain events.
 */
public abstract class AbstractInboundEvent implements InboundDomainEvent {

    private final String eventId;
    private final Instant occurredAt;
    private final InboundBoundedContext sourceContext;
    private final String aggregateId;
    private final String aggregateType;

    protected AbstractInboundEvent(InboundBoundedContext sourceContext, String aggregateId, String aggregateType) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredAt = Instant.now();
        this.sourceContext = sourceContext;
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
    }

    protected AbstractInboundEvent(String eventId, Instant occurredAt, InboundBoundedContext sourceContext,
                                   String aggregateId, String aggregateType) {
        this.eventId = eventId;
        this.occurredAt = occurredAt;
        this.sourceContext = sourceContext;
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }

    @Override
    public InboundBoundedContext getSourceContext() {
        return sourceContext;
    }

    @Override
    public String getAggregateId() {
        return aggregateId;
    }

    @Override
    public String getAggregateType() {
        return aggregateType;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
               "eventId='" + eventId + '\'' +
               ", eventType='" + getEventType() + '\'' +
               ", aggregateId='" + aggregateId + '\'' +
               ", occurredAt=" + occurredAt +
               '}';
    }
}
