package com.maersk.wms.task.shared.kernel.identifiers;

/**
 * Value object representing a unique Location identifier.
 */
public record LocationKey(String value) {
    public LocationKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("LocationKey cannot be null or blank");
        }
    }
}
