package com.maersk.wms.printing.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for Device (RF device, workstation).
 */
public record DeviceKey(String value) {
    public DeviceKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("DeviceKey cannot be null or blank");
        }
    }
}
