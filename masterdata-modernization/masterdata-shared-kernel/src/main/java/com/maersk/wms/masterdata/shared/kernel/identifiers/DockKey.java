package com.maersk.wms.masterdata.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for Dock (receiving/shipping dock).
 */
public record DockKey(String value) {
    public DockKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("DockKey cannot be null or blank");
        }
    }
}
