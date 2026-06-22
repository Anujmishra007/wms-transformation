package com.maersk.wms.task.shared.kernel.identifiers;

/**
 * Value object representing a unique LPN (License Plate Number) identifier.
 */
public record LpnKey(String value) {
    public LpnKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("LpnKey cannot be null or blank");
        }
    }
}
