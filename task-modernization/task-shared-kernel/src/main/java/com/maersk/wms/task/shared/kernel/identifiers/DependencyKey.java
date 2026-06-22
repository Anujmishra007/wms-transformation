package com.maersk.wms.task.shared.kernel.identifiers;

/**
 * Value object representing a unique TaskDependency identifier.
 */
public record DependencyKey(String value) {
    public DependencyKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("DependencyKey cannot be null or blank");
        }
    }
}
