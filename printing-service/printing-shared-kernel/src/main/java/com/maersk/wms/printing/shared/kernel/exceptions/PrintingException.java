package com.maersk.wms.printing.shared.kernel.exceptions;

/**
 * Base exception for all printing domain errors.
 */
public class PrintingException extends RuntimeException {

    private final String errorCode;

    public PrintingException(String message) {
        super(message);
        this.errorCode = "PRINTING_ERROR";
    }

    public PrintingException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public PrintingException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
