package com.maersk.wms.picking.shared.kernel.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Abstract base class for picking domain events.
 */
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractPickingEvent implements PickingDomainEvent {

    private PickingBoundedContext boundedContext;
    private String aggregateId;
    private String aggregateType;
    private LocalDateTime occurredAt;
    private String correlationId;

    /**
     * Initialize default values for event metadata.
     */
    protected void initializeDefaults(PickingBoundedContext context, String aggId, String aggType) {
        this.boundedContext = context;
        this.aggregateId = aggId;
        this.aggregateType = aggType;
        this.occurredAt = LocalDateTime.now();
        this.correlationId = UUID.randomUUID().toString();
    }

    /**
     * Returns the event type based on the class name.
     */
    @Override
    public String getEventType() {
        return this.getClass().getSimpleName();
    }
}
