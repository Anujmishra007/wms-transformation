package com.maersk.wms.task.shared.kernel.identifiers;

/**
 * Value object representing a unique Zone identifier.
 */
public record ZoneKey(String value) {
    public ZoneKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ZoneKey cannot be null or blank");
        }
    }
}
