package com.maersk.wms.masterdata.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for Warehouse.
 */
public record WarehouseKey(String value) {
    public WarehouseKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("WarehouseKey cannot be null or blank");
        }
    }
}
