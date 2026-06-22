package com.maersk.wms.inventory.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for Inventory Nesting/Hierarchy.
 * Represents parent-child relationships (Pallet → Case → Pack).
 */
public record NestingKey(String value) {
    public NestingKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("NestingKey cannot be null or blank");
        }
    }
}
