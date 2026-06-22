package com.maersk.wms.task.shared.kernel.identifiers;

/**
 * Value object representing a unique Device identifier.
 */
public record DeviceKey(String value) {
    public DeviceKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("DeviceKey cannot be null or blank");
        }
    }
}
