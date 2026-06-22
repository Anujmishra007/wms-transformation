package com.maersk.wms.inventory.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for Inventory records.
 * Primary identifier for LOTxLOCxID records.
 */
public record InventoryKey(String value) {
    public InventoryKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("InventoryKey cannot be null or blank");
        }
    }
}
