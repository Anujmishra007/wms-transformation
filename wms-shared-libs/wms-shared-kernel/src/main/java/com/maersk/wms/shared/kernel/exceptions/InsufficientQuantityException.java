package com.maersk.wms.shared.kernel.exceptions;

import com.maersk.wms.shared.kernel.valueobjects.Quantity;

/**
 * Exception thrown when there is insufficient quantity.
 * Common across inventory, picking, and allocation operations.
 */
public class InsufficientQuantityException extends WmsException {

    private final String entityId;
    private final Quantity requested;
    private final Quantity available;

    public InsufficientQuantityException(String entityId, Quantity requested, Quantity available) {
        super("INSUFFICIENT_QUANTITY", "BUSINESS",
                "Insufficient quantity for " + entityId +
                        ": requested " + requested + ", available " + available);
        this.entityId = entityId;
        this.requested = requested;
        this.available = available;
    }

    public String getEntityId() {
        return entityId;
    }

    public Quantity getRequested() {
        return requested;
    }

    public Quantity getAvailable() {
        return available;
    }
}
