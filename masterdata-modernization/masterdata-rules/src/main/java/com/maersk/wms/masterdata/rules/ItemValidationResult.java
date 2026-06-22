package com.maersk.wms.masterdata.rules;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Result from item validation rule evaluation.
 */
@Data
public class ItemValidationResult {

    private boolean valid = true;
    private List<String> errors = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();

    // Computed values from rules
    private String computedAbcClass;
    private String computedStorageZone;
    private String computedVelocityCode;

    public void addError(String error) {
        this.errors.add(error);
        this.valid = false;
    }

    public void addWarning(String warning) {
        this.warnings.add(warning);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }
}
