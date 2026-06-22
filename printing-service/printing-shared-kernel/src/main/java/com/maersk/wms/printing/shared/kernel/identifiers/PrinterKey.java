package com.maersk.wms.printing.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for Printer.
 */
public record PrinterKey(String value) {
    public PrinterKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("PrinterKey cannot be null or blank");
        }
    }
}
