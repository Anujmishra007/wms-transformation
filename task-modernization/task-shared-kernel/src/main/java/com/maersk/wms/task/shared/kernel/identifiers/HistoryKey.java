package com.maersk.wms.task.shared.kernel.identifiers;

/**
 * Value object representing a unique TaskHistory identifier.
 */
public record HistoryKey(String value) {
    public HistoryKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("HistoryKey cannot be null or blank");
        }
    }
}
