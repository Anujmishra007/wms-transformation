package com.maersk.wms.printing.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for Warehouse (reference from Master Data).
 */
public record WarehouseKey(String value) {
    public WarehouseKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("WarehouseKey cannot be null or blank");
        }
    }
}
