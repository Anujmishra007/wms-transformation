package com.maersk.wms.inventory.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for Location.
 * Represents a physical storage location in the warehouse.
 */
public record LocationKey(String value) {
    public LocationKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("LocationKey cannot be null or blank");
        }
    }
}
