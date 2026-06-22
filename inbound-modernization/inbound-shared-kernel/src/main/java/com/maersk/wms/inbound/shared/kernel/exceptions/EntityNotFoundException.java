package com.maersk.wms.inbound.shared.kernel.exceptions;

import com.maersk.wms.inbound.shared.kernel.events.InboundBoundedContext;

/**
 * Exception thrown when an entity is not found.
 *
 * Part of Shared Kernel - used when aggregate/entity lookup fails.
 */
public class EntityNotFoundException extends InboundException {

    private final String entityType;
    private final String entityId;

    public EntityNotFoundException(String entityType, String entityId, InboundBoundedContext sourceContext) {
        super(entityType + " not found: " + entityId, sourceContext, "ENTITY_NOT_FOUND");
        this.entityType = entityType;
        this.entityId = entityId;
    }

    public EntityNotFoundException(String entityType, String entityId, InboundBoundedContext sourceContext, Throwable cause) {
        super(entityType + " not found: " + entityId, cause, sourceContext, "ENTITY_NOT_FOUND");
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
