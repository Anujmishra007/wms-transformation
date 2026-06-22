package com.maersk.wms.task.shared.kernel.identifiers;

/**
 * Value object representing a unique User identifier.
 */
public record UserKey(String value) {
    public UserKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("UserKey cannot be null or blank");
        }
    }
}
