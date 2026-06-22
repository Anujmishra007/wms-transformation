package com.maersk.wms.masterdata.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for User (warehouse personnel).
 */
public record UserKey(String value) {
    public UserKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("UserKey cannot be null or blank");
        }
    }
}
