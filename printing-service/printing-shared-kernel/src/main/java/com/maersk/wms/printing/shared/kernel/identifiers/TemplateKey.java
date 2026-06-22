package com.maersk.wms.printing.shared.kernel.identifiers;

/**
 * Strongly-typed identifier for Label Template.
 */
public record TemplateKey(String value) {
    public TemplateKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("TemplateKey cannot be null or blank");
        }
    }
}
