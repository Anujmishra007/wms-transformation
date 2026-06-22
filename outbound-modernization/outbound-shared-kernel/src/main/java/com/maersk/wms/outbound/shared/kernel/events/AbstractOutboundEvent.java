package com.maersk.wms.outbound.shared.kernel.events;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Abstract base class for outbound domain events.
 */
@Getter
public abstract class AbstractOutboundEvent implements OutboundDomainEvent {

    private final OutboundBoundedContext boundedContext;
    private final String aggregateId;
    private final String aggregateType;
    private final LocalDateTime occurredAt;
    private final String correlationId;

    protected AbstractOutboundEvent(OutboundBoundedContext boundedContext,
                                     String aggregateId,
                                     String aggregateType) {
        this.boundedContext = boundedContext;
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
        this.occurredAt = LocalDateTime.now();
        this.correlationId = UUID.randomUUID().toString();
    }

    protected AbstractOutboundEvent(OutboundBoundedContext boundedContext,
                                     String aggregateId,
                                     String aggregateType,
                                     String correlationId) {
        this.boundedContext = boundedContext;
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
        this.occurredAt = LocalDateTime.now();
        this.correlationId = correlationId;
    }
}
