package com.maersk.wms.task.shared.kernel.identifiers;

/**
 * Value object representing a unique Wave identifier.
 */
public record WaveKey(String value) {
    public WaveKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("WaveKey cannot be null or blank");
        }
    }
}
