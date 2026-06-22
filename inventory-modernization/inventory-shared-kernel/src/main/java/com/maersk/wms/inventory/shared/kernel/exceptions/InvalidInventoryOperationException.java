package com.maersk.wms.inventory.shared.kernel.exceptions;

/**
 * Exception thrown when an invalid inventory operation is attempted.
 */
public class InvalidInventoryOperationException extends InventoryException {

    public InvalidInventoryOperationException(String operation, String reason) {
        super("INV_INVALID_OP", String.format("Invalid operation '%s': %s", operation, reason));
    }

    public InvalidInventoryOperationException(String message) {
        super("INV_INVALID_OP", message);
    }
}
