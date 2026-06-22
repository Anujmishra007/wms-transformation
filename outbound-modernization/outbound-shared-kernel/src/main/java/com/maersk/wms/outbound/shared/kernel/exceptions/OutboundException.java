package com.maersk.wms.outbound.shared.kernel.exceptions;

/**
 * Base exception for all outbound domain exceptions.
 */
public class OutboundException extends RuntimeException {

    private final String errorCode;

    public OutboundException(String message) {
        super(message);
        this.errorCode = "OUTBOUND_ERROR";
    }

    public OutboundException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public OutboundException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
