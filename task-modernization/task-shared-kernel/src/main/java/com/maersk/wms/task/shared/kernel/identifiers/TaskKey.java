package com.maersk.wms.task.shared.kernel.identifiers;

/**
 * Value object representing a unique Task identifier.
 */
public record TaskKey(String value) {
    public TaskKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("TaskKey cannot be null or blank");
        }
    }
}
