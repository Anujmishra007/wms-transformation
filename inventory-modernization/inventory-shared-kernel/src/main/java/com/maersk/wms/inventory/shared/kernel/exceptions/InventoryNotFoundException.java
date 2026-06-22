package com.maersk.wms.inventory.shared.kernel.exceptions;

import com.maersk.wms.inventory.shared.kernel.identifiers.InventoryKey;

/**
 * Exception thrown when inventory record is not found.
 */
public class InventoryNotFoundException extends InventoryException {

    public InventoryNotFoundException(InventoryKey key) {
        super("INV_NOT_FOUND", "Inventory not found: " + key.value());
    }

    public InventoryNotFoundException(String message) {
        super("INV_NOT_FOUND", message);
    }

    public InventoryNotFoundException(String lot, String location, String lpn) {
        super("INV_NOT_FOUND", String.format("Inventory not found for LOT=%s, LOC=%s, LPN=%s", lot, location, lpn));
    }
}
