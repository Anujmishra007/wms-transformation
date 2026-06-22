package com.maersk.wms.inventory.shared.kernel.exceptions;

import com.maersk.wms.inventory.shared.kernel.valueobjects.Quantity;

/**
 * Exception thrown when there's insufficient inventory for an operation.
 */
public class InsufficientInventoryException extends InventoryException {

    private final Quantity requested;
    private final Quantity available;

    public InsufficientInventoryException(Quantity requested, Quantity available) {
        super("INV_INSUFFICIENT", String.format(
                "Insufficient inventory: requested %s %s, available %s %s",
                requested.value(), requested.uom(),
                available.value(), available.uom()));
        this.requested = requested;
        this.available = available;
    }

    public InsufficientInventoryException(String sku, Quantity requested, Quantity available) {
        super("INV_INSUFFICIENT", String.format(
                "Insufficient inventory for SKU %s: requested %s %s, available %s %s",
                sku, requested.value(), requested.uom(),
                available.value(), available.uom()));
        this.requested = requested;
        this.available = available;
    }

    public Quantity getRequested() {
        return requested;
    }

    public Quantity getAvailable() {
        return available;
    }

    public Quantity getShortage() {
        return requested.subtract(available);
    }
}
