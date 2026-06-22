package com.maersk.wms.printing.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for Print Queue.
 */
public record PrintQueueKey(String value) {
    public PrintQueueKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("PrintQueueKey cannot be null or blank");
        }
    }
}
