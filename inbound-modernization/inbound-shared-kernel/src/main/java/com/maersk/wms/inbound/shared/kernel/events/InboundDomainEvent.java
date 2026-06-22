package com.maersk.wms.inbound.shared.kernel.events;

import java.time.Instant;

/**
 * Base interface for all inbound domain events.
 * Events are used for communication between bounded contexts and external services.
 *
 * Part of Shared Kernel - defines the contract for domain events.
 */
public interface InboundDomainEvent {

    /**
     * Unique identifier for this event instance.
     */
    String getEventId();

    /**
     * Type of event (e.g., "RECEIPT_CREATED", "PUTAWAY_COMPLETED").
     */
    String getEventType();

    /**
     * Timestamp when the event occurred.
     */
    Instant getOccurredAt();

    /**
     * Source bounded context that produced this event.
     */
    InboundBoundedContext getSourceContext();

    /**
     * Aggregate root ID that this event relates to.
     */
    String getAggregateId();

    /**
     * Aggregate type (e.g., "Receipt", "PurchaseOrder").
     */
    String getAggregateType();
}
