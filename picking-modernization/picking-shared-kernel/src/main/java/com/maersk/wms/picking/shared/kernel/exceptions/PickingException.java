package com.maersk.wms.picking.shared.kernel.exceptions;

/**
 * Base exception for all picking domain exceptions.
 */
public class PickingException extends RuntimeException {

    private final String errorCode;

    public PickingException(String message) {
        super(message);
        this.errorCode = "PICKING_ERROR";
    }

    public PickingException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public PickingException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
