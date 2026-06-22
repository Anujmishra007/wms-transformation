package com.maersk.wms.picking.shared.kernel.events;

import java.time.LocalDateTime;

/**
 * Base interface for all picking domain events.
 */
public interface PickingDomainEvent {

    String getEventType();

    PickingBoundedContext getBoundedContext();

    String getAggregateId();

    String getAggregateType();

    LocalDateTime getOccurredAt();

    String getCorrelationId();
}
