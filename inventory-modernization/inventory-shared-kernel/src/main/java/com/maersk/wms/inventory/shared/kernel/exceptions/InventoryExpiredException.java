package com.maersk.wms.inventory.shared.kernel.exceptions;

import java.time.LocalDate;

/**
 * Exception thrown when inventory has expired.
 */
public class InventoryExpiredException extends InventoryException {

    private final LocalDate expiryDate;

    public InventoryExpiredException(String sku, String lot, LocalDate expiryDate) {
        super("INV_EXPIRED", String.format(
                "Inventory expired: SKU=%s, LOT=%s, expired on %s", sku, lot, expiryDate));
        this.expiryDate = expiryDate;
    }

    public InventoryExpiredException(String message, LocalDate expiryDate) {
        super("INV_EXPIRED", message);
        this.expiryDate = expiryDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }
}
