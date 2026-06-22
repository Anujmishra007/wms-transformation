package com.maersk.wms.inventory.shared.kernel.exceptions;

/**
 * Base exception for all inventory-related errors.
 */
public class InventoryException extends RuntimeException {

    private final String errorCode;

    public InventoryException(String message) {
        super(message);
        this.errorCode = "INV_ERROR";
    }

    public InventoryException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public InventoryException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "INV_ERROR";
    }

    public InventoryException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
