package com.maersk.wms.inbound.service;

/**
 * Exception for inbound operation failures.
 */
public class InboundOperationException extends RuntimeException {

    private final String errorCode;

    public InboundOperationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public InboundOperationException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
