package com.maersk.wms.shared.kernel.identifiers;

import java.io.Serializable;

/**
 * Strongly-typed identifier for LPN (License Plate Number).
 * Used across all WMS microservices for container/pallet identification.
 *
 * @param value The LPN identifier value
 */
public record LpnKey(String value) implements Serializable {

    public LpnKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("LPN key cannot be null or blank");
        }
    }

    /**
     * Create from string value.
     */
    public static LpnKey of(String value) {
        return new LpnKey(value);
    }

    /**
     * Create empty LPN (for scenarios where LPN is optional).
     */
    public static LpnKey empty() {
        return new LpnKey("EMPTY");
    }

    @Override
    public String toString() {
        return value;
    }
}
