package com.maersk.wms.inventory.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for Storer/Owner.
 * Represents the inventory owner in a multi-tenant environment.
 */
public record StorerKey(String value) {
    public StorerKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("StorerKey cannot be null or blank");
        }
    }
}
