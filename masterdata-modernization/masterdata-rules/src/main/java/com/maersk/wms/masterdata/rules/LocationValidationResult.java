package com.maersk.wms.masterdata.rules;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Result from location validation rule evaluation.
 */
@Data
public class LocationValidationResult {

    private boolean valid = true;
    private List<String> errors = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();

    // Computed values from rules
    private int computedPickPathSequence;
    private int computedPutawaySequence;
    private String computedStorageType;

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
}
