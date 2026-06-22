package com.maersk.wms.task.shared.kernel.identifiers;

/**
 * Value object representing a unique TaskAssignment identifier.
 */
public record AssignmentKey(String value) {
    public AssignmentKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("AssignmentKey cannot be null or blank");
        }
    }
}
