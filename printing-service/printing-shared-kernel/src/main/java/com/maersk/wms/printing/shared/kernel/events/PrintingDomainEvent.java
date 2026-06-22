package com.maersk.wms.printing.shared.kernel.events;

import java.time.Instant;

/**
 * Base interface for all Printing domain events.
 */
public interface PrintingDomainEvent {

    Instant occurredAt();

    default String aggregateId() {
        return "unknown";
    }

    default String eventType() {
        return getClass().getSimpleName();
    }

    default String getEventType() {
        return eventType();
    }

    default String boundedContext() {
        return "PRINTING";
    }
}
