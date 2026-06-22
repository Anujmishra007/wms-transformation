package com.maersk.wms.inventory.shared.kernel.exceptions;

import com.maersk.wms.inventory.shared.kernel.identifiers.InventoryKey;

/**
 * Exception thrown when trying to operate on inventory that is on hold.
 */
public class InventoryOnHoldException extends InventoryException {

    private final String holdCode;

    public InventoryOnHoldException(InventoryKey key, String holdCode) {
        super("INV_ON_HOLD", String.format("Inventory %s is on hold: %s", key.value(), holdCode));
        this.holdCode = holdCode;
    }

    public InventoryOnHoldException(String message, String holdCode) {
        super("INV_ON_HOLD", message);
        this.holdCode = holdCode;
    }

    public String getHoldCode() {
        return holdCode;
    }
}
