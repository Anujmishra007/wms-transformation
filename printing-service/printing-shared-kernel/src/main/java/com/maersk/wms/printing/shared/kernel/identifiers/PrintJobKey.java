package com.maersk.wms.printing.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for PrintJob.
 */
public record PrintJobKey(String value) {
    public PrintJobKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("PrintJobKey cannot be null or blank");
        }
    }
}
