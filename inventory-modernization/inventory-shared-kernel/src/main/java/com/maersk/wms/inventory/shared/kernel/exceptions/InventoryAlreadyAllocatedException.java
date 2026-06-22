package com.maersk.wms.inventory.shared.kernel.exceptions;

import com.maersk.wms.inventory.shared.kernel.identifiers.InventoryKey;
import com.maersk.wms.inventory.shared.kernel.valueobjects.Quantity;

/**
 * Exception thrown when trying to allocate already-allocated inventory.
 */
public class InventoryAlreadyAllocatedException extends InventoryException {

    public InventoryAlreadyAllocatedException(InventoryKey key, Quantity allocatedQty) {
        super("INV_ALREADY_ALLOCATED", String.format(
                "Inventory %s already has allocation of %s %s",
                key.value(), allocatedQty.value(), allocatedQty.uom()));
    }

    public InventoryAlreadyAllocatedException(String message) {
        super("INV_ALREADY_ALLOCATED", message);
    }
}
