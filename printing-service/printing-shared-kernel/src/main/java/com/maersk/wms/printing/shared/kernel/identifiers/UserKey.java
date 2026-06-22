package com.maersk.wms.printing.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for User (reference from Master Data).
 */
public record UserKey(String value) {
    public UserKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("UserKey cannot be null or blank");
        }
    }
}
