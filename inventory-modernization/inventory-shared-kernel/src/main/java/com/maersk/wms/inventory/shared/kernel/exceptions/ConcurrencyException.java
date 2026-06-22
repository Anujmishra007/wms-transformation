package com.maersk.wms.inventory.shared.kernel.exceptions;

import com.maersk.wms.inventory.shared.kernel.identifiers.InventoryKey;

/**
 * Exception thrown when a concurrency conflict occurs (TrafficCop/RowVersion mismatch).
 */
public class ConcurrencyException extends InventoryException {

    public ConcurrencyException(InventoryKey key) {
        super("INV_CONCURRENCY", String.format(
                "Concurrency conflict on inventory %s - record was modified by another transaction", key.value()));
    }

    public ConcurrencyException(String message) {
        super("INV_CONCURRENCY", message);
    }
}
