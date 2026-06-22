package com.maersk.wms.events.contracts;

import java.time.Instant;
import java.util.UUID;

/**
 * Base interface for all domain events in the WMS system.
 * All cross-service events must implement this interface.
 */
public interface DomainEvent {

    /**
     * Unique identifier for this event instance.
     */
    UUID getEventId();

    /**
     * Type of the event (e.g., "InventoryAdjusted", "OrderCreated").
     */
    String getEventType();

    /**
     * Timestamp when the event occurred.
     */
    Instant getOccurredAt();

    /**
     * Client code for multi-tenant isolation.
     */
    String getClientCode();

    /**
     * Country code for regional routing.
     */
    String getCountryCode();

    /**
     * Warehouse code where the event originated.
     */
    String getWarehouseCode();

    /**
     * Version of the event schema for evolution.
     */
    default int getSchemaVersion() {
        return 1;
    }

    /**
     * Correlation ID for distributed tracing.
     */
    String getCorrelationId();
}
