package com.maersk.wms.masterdata.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for Storer (Customer/Inventory Owner).
 */
public record StorerKey(String value) {
    public StorerKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("StorerKey cannot be null or blank");
        }
    }
}
