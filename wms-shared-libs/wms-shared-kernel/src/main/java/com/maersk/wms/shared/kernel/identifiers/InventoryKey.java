package com.maersk.wms.shared.kernel.identifiers;

import java.io.Serializable;

/**
 * Strongly-typed identifier for Inventory record.
 * Primary key for LOTxLOCxID inventory table.
 *
 * @param value The inventory identifier value
 */
public record InventoryKey(String value) implements Serializable {

    public InventoryKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Inventory key cannot be null or blank");
        }
    }

    /**
     * Create from string value.
     */
    public static InventoryKey of(String value) {
        return new InventoryKey(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
