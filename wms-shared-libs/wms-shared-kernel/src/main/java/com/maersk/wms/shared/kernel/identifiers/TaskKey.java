package com.maersk.wms.shared.kernel.identifiers;

import java.io.Serializable;

/**
 * Strongly-typed identifier for Task.
 * Used across task management and operations microservices.
 *
 * @param value The task identifier value
 */
public record TaskKey(String value) implements Serializable {

    public TaskKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Task key cannot be null or blank");
        }
    }

    /**
     * Create from string value.
     */
    public static TaskKey of(String value) {
        return new TaskKey(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
