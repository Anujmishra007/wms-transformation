package com.maersk.wms.task.shared.kernel.identifiers;

/**
 * Value object representing a unique TaskGroup identifier.
 */
public record TaskGroupKey(String value) {
    public TaskGroupKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("TaskGroupKey cannot be null or blank");
        }
    }
}
