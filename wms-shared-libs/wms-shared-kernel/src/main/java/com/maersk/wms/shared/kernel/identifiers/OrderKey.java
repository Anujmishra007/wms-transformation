package com.maersk.wms.shared.kernel.identifiers;

import java.io.Serializable;

/**
 * Strongly-typed identifier for Order.
 * Used across outbound, picking, inventory, and task microservices.
 *
 * @param value The order identifier value
 */
public record OrderKey(String value) implements Serializable {

    public OrderKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Order key cannot be null or blank");
        }
    }

    /**
     * Create from string value.
     */
    public static OrderKey of(String value) {
        return new OrderKey(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
