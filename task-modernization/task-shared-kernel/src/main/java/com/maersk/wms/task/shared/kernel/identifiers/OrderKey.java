package com.maersk.wms.task.shared.kernel.identifiers;

/**
 * Value object representing a unique Order identifier.
 */
public record OrderKey(String value) {
    public OrderKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("OrderKey cannot be null or blank");
        }
    }
}
