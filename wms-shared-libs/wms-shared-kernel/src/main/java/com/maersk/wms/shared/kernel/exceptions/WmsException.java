package com.maersk.wms.shared.kernel.exceptions;

/**
 * Base exception for all WMS exceptions.
 * Provides common error handling across all microservices.
 */
public class WmsException extends RuntimeException {

    private final String errorCode;
    private final String errorType;

    public WmsException(String message) {
        super(message);
        this.errorCode = "WMS_ERROR";
        this.errorType = "GENERAL";
    }

    public WmsException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.errorType = "GENERAL";
    }

    public WmsException(String errorCode, String errorType, String message) {
        super(message);
        this.errorCode = errorCode;
        this.errorType = errorType;
    }

    public WmsException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "WMS_ERROR";
        this.errorType = "GENERAL";
    }

    public WmsException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.errorType = "GENERAL";
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorType() {
        return errorType;
    }
}
