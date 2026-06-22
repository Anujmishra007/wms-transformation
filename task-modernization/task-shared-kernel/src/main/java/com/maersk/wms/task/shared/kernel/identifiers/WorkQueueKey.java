package com.maersk.wms.task.shared.kernel.identifiers;

/**
 * Value object representing a unique WorkQueue identifier.
 */
public record WorkQueueKey(String value) {
    public WorkQueueKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("WorkQueueKey cannot be null or blank");
        }
    }
}
