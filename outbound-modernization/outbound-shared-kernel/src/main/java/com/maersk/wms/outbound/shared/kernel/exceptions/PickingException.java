package com.maersk.wms.outbound.shared.kernel.exceptions;

/**
 * Exception for picking-related errors.
 */
public class PickingException extends OutboundException {

    public PickingException(String message) {
        super("PICKING_ERROR", message);
    }

    public PickingException(String errorCode, String message) {
        super(errorCode, message);
    }

    public static PickingException shortPick(String pickDetailKey, String expectedQty, String actualQty) {
        return new PickingException("SHORT_PICK",
                String.format("Short pick on %s: expected %s, picked %s",
                        pickDetailKey, expectedQty, actualQty));
    }

    public static PickingException invalidStatus(String pickDetailKey, String currentStatus, String expectedStatus) {
        return new PickingException("INVALID_PICK_STATUS",
                String.format("Pick detail %s has invalid status %s, expected %s",
                        pickDetailKey, currentStatus, expectedStatus));
    }

    public static PickingException locationMismatch(String pickDetailKey, String expectedLocation, String actualLocation) {
        return new PickingException("LOCATION_MISMATCH",
                String.format("Location mismatch for pick %s: expected %s, scanned %s",
                        pickDetailKey, expectedLocation, actualLocation));
    }

    public static PickingException pickAlreadyCancelled(String pickDetailKey) {
        return new PickingException("PICK_ALREADY_CANCELLED",
                String.format("Pick detail %s is already cancelled", pickDetailKey));
    }
}
