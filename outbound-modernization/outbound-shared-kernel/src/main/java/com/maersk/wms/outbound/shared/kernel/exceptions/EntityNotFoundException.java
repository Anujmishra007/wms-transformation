package com.maersk.wms.outbound.shared.kernel.exceptions;

/**
 * Exception thrown when an entity is not found.
 */
public class EntityNotFoundException extends OutboundException {

    public EntityNotFoundException(String entityType, String identifier) {
        super("ENTITY_NOT_FOUND",
                String.format("%s not found: %s", entityType, identifier));
    }

    public static EntityNotFoundException order(String orderKey) {
        return new EntityNotFoundException("Order", orderKey);
    }

    public static EntityNotFoundException wave(String waveKey) {
        return new EntityNotFoundException("Wave", waveKey);
    }

    public static EntityNotFoundException pickHeader(String pickHeaderKey) {
        return new EntityNotFoundException("PickHeader", pickHeaderKey);
    }

    public static EntityNotFoundException pickDetail(String pickDetailKey) {
        return new EntityNotFoundException("PickDetail", pickDetailKey);
    }
}
