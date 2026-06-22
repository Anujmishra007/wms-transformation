package com.maersk.wms.shared.kernel.identifiers;

import java.io.Serializable;

/**
 * Strongly-typed identifier for Shipment.
 * Used across outbound and packing microservices.
 *
 * @param value The shipment identifier value
 */
public record ShipmentKey(String value) implements Serializable {

    public ShipmentKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Shipment key cannot be null or blank");
        }
    }

    /**
     * Create from string value.
     */
    public static ShipmentKey of(String value) {
        return new ShipmentKey(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
