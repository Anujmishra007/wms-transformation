package com.maersk.wms.shared.kernel.identifiers;

import java.io.Serializable;

/**
 * Strongly-typed identifier for Work Queue (task queues).
 *
 * @param value The work queue identifier value
 */
public record WorkQueueKey(String value) implements Serializable {

    public WorkQueueKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Work queue key cannot be null or blank");
        }
    }

    public static WorkQueueKey of(String value) {
        return new WorkQueueKey(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
