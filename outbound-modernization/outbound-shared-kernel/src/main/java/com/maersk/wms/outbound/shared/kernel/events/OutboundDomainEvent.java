package com.maersk.wms.outbound.shared.kernel.events;

import java.time.LocalDateTime;

/**
 * Base interface for all outbound domain events.
 */
public interface OutboundDomainEvent {

    /**
     * Returns the event type identifier.
     */
    String getEventType();

    /**
     * Returns the bounded context that originated this event.
     */
    OutboundBoundedContext getBoundedContext();

    /**
     * Returns the aggregate ID this event relates to.
     */
    String getAggregateId();

    /**
     * Returns the aggregate type.
     */
    String getAggregateType();

    /**
     * Returns the timestamp when this event occurred.
     */
    LocalDateTime getOccurredAt();

    /**
     * Returns the correlation ID for tracing.
     */
    String getCorrelationId();
}
