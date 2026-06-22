package com.maersk.wms.picking.shared.kernel.events;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Abstract base class for picking domain events.
 */
@Getter
public abstract class AbstractPickingEvent implements PickingDomainEvent {

    private final PickingBoundedContext boundedContext;
    private final String aggregateId;
    private final String aggregateType;
    private final LocalDateTime occurredAt;
    private final String correlationId;

    protected AbstractPickingEvent(PickingBoundedContext boundedContext,
                                    String aggregateId,
                                    String aggregateType) {
        this.boundedContext = boundedContext;
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
        this.occurredAt = LocalDateTime.now();
        this.correlationId = UUID.randomUUID().toString();
    }

    protected AbstractPickingEvent(PickingBoundedContext boundedContext,
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
