package com.maersk.wms.shared.kernel.exceptions;

/**
 * Exception thrown when an entity is not found.
 * Common across all microservices.
 */
public class EntityNotFoundException extends WmsException {

    private final String entityType;
    private final String entityId;

    public EntityNotFoundException(String entityType, String entityId) {
        super("NOT_FOUND", "ENTITY", entityType + " not found: " + entityId);
        this.entityType = entityType;
        this.entityId = entityId;
    }

    public EntityNotFoundException(String entityType, String entityId, String message) {
        super("NOT_FOUND", "ENTITY", message);
        this.entityType = entityType;
        this.entityId = entityId;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getEntityId() {
        return entityId;
    }
}
