package com.maersk.wms.inventory.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for Warehouse.
 * Represents a physical warehouse/distribution center.
 */
public record WarehouseKey(String value) {
    public WarehouseKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("WarehouseKey cannot be null or blank");
        }
    }
}
