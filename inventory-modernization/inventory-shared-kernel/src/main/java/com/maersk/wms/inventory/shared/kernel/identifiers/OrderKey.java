package com.maersk.wms.inventory.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for Order.
 * Represents an outbound order that consumes inventory.
 */
public record OrderKey(String value) {
    public OrderKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("OrderKey cannot be null or blank");
        }
    }
}
