package com.maersk.wms.shared.kernel.identifiers;

import java.io.Serializable;

/**
 * Strongly-typed identifier for User.
 * Used across all WMS microservices for user identification.
 *
 * @param value The user identifier value
 */
public record UserKey(String value) implements Serializable {

    public UserKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("User key cannot be null or blank");
        }
    }

    /**
     * Create from string value.
     */
    public static UserKey of(String value) {
        return new UserKey(value);
    }

    /**
     * Create system user key (for automated operations).
     */
    public static UserKey system() {
        return new UserKey("SYSTEM");
    }

    @Override
    public String toString() {
        return value;
    }
}
