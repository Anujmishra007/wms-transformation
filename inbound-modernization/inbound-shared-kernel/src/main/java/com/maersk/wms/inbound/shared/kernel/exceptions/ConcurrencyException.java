package com.maersk.wms.inbound.shared.kernel.exceptions;

import com.maersk.wms.inbound.shared.kernel.events.InboundBoundedContext;

/**
 * Exception thrown when optimistic locking fails (TrafficCop mismatch).
 *
 * Part of Shared Kernel - used for concurrency control across bounded contexts.
 */
public class ConcurrencyException extends InboundException {

    private final String entityType;
    private final String entityId;
    private final int expectedVersion;
    private final int actualVersion;

    public ConcurrencyException(String entityType, String entityId, int expectedVersion, int actualVersion,
                                InboundBoundedContext sourceContext) {
        super("Concurrency conflict for " + entityType + " " + entityId +
              ": expected version " + expectedVersion + " but found " + actualVersion,
              sourceContext, "CONCURRENCY_ERROR");
        this.entityType = entityType;
        this.entityId = entityId;
        this.expectedVersion = expectedVersion;
        this.actualVersion = actualVersion;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public int getExpectedVersion() {
        return expectedVersion;
    }

    public int getActualVersion() {
        return actualVersion;
    }
}
