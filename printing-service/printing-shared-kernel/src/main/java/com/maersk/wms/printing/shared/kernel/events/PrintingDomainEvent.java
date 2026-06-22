package com.maersk.wms.printing.shared.kernel.events;

import java.time.Instant;

/**
 * Base interface for all Printing domain events.
 */
public interface PrintingDomainEvent {

    String aggregateId();

    String eventType();

    Instant occurredAt();

    default String boundedContext() {
        return "PRINTING";
    }
}
