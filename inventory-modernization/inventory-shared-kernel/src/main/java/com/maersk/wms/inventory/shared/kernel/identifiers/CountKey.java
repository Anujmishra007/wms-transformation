package com.maersk.wms.inventory.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for Inventory Counts.
 * Represents a cycle count or physical inventory count.
 */
public record CountKey(String value) {
    public CountKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("CountKey cannot be null or blank");
        }
    }
}
