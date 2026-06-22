package com.maersk.wms.shared.kernel.identifiers;

import java.io.Serializable;

/**
 * Strongly-typed identifier for Allocation.
 * Used across inventory and picking microservices.
 *
 * @param value The allocation identifier value
 */
public record AllocationKey(String value) implements Serializable {

    public AllocationKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Allocation key cannot be null or blank");
        }
    }

    /**
     * Create from string value.
     */
    public static AllocationKey of(String value) {
        return new AllocationKey(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
