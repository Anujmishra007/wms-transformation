package com.maersk.wms.picking.shared.kernel.exceptions;

import java.math.BigDecimal;

/**
 * Exception for short pick related errors.
 */
public class ShortPickException extends PickingException {

    public ShortPickException(String message) {
        super("SHORT_PICK_ERROR", message);
    }

    public ShortPickException(String errorCode, String message) {
        super(errorCode, message);
    }

    public static ShortPickException quantityMismatch(String pickTaskKey, BigDecimal expected, BigDecimal actual) {
        return new ShortPickException("QUANTITY_MISMATCH",
                String.format("Short pick on task %s: expected %s, found %s",
                        pickTaskKey, expected, actual));
    }

    public static ShortPickException inventoryNotFound(String pickTaskKey, String location) {
        return new ShortPickException("INVENTORY_NOT_FOUND",
                String.format("No inventory found for task %s at location %s",
                        pickTaskKey, location));
    }

    public static ShortPickException locationEmpty(String pickTaskKey, String location) {
        return new ShortPickException("LOCATION_EMPTY",
                String.format("Location %s is empty for task %s", location, pickTaskKey));
    }
}
