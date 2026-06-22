package com.maersk.wms.shared.kernel.identifiers;

import java.io.Serializable;

/**
 * Strongly-typed identifier for Device (RF scanner, etc.).
 * Used across picking, task, and printing microservices.
 *
 * @param value The device identifier value
 */
public record DeviceKey(String value) implements Serializable {

    public DeviceKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Device key cannot be null or blank");
        }
    }

    /**
     * Create from string value.
     */
    public static DeviceKey of(String value) {
        return new DeviceKey(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
