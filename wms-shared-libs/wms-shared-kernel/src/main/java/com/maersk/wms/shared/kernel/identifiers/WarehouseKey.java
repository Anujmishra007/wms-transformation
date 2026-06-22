package com.maersk.wms.shared.kernel.identifiers;

import java.io.Serializable;

/**
 * Strongly-typed identifier for Warehouse.
 * Used across all WMS microservices for multi-warehouse support.
 *
 * @param value The warehouse identifier value
 */
public record WarehouseKey(String value) implements Serializable {

    public WarehouseKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Warehouse key cannot be null or blank");
        }
    }

    /**
     * Create from string value.
     */
    public static WarehouseKey of(String value) {
        return new WarehouseKey(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
