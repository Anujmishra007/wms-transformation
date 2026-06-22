package com.maersk.wms.printing.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for Storer (reference from Master Data).
 */
public record StorerKey(String value) {
    public StorerKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("StorerKey cannot be null or blank");
        }
    }
}
