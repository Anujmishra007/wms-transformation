package com.maersk.wms.inventory.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for User.
 * Represents a system user performing inventory operations.
 */
public record UserKey(String value) {
    public UserKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("UserKey cannot be null or blank");
        }
    }
}
