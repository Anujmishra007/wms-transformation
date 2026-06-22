package com.maersk.wms.shared.kernel.exceptions;

/**
 * Exception thrown when a concurrency conflict occurs.
 * Used for optimistic locking violations.
 */
public class ConcurrencyException extends WmsException {

    private final String entityType;
    private final String entityId;
    private final Long expectedVersion;
    private final Long actualVersion;

    public ConcurrencyException(String entityType, String entityId) {
        super("CONCURRENCY_ERROR", "CONFLICT",
                "Concurrent modification detected for " + entityType + ": " + entityId);
        this.entityType = entityType;
        this.entityId = entityId;
        this.expectedVersion = null;
        this.actualVersion = null;
    }

    public ConcurrencyException(String entityType, String entityId,
                                 Long expectedVersion, Long actualVersion) {
        super("CONCURRENCY_ERROR", "CONFLICT",
                "Version mismatch for " + entityType + ": " + entityId +
                        " (expected: " + expectedVersion + ", actual: " + actualVersion + ")");
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

    public Long getExpectedVersion() {
        return expectedVersion;
    }

    public Long getActualVersion() {
        return actualVersion;
    }
}
