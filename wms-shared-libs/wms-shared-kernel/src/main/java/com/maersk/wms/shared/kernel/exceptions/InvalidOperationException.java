package com.maersk.wms.shared.kernel.exceptions;

/**
 * Exception thrown when an operation is invalid.
 * Used for business rule violations.
 */
public class InvalidOperationException extends WmsException {

    private final String operation;
    private final String reason;

    public InvalidOperationException(String message) {
        super("INVALID_OPERATION", "BUSINESS", message);
        this.operation = null;
        this.reason = message;
    }

    public InvalidOperationException(String operation, String reason) {
        super("INVALID_OPERATION", "BUSINESS",
                "Invalid operation '" + operation + "': " + reason);
        this.operation = operation;
        this.reason = reason;
    }

    public String getOperation() {
        return operation;
    }

    public String getReason() {
        return reason;
    }
}
