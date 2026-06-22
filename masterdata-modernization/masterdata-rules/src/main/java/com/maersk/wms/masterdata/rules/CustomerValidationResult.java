package com.maersk.wms.masterdata.rules;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Result from customer validation rule evaluation.
 */
@Data
public class CustomerValidationResult {

    private boolean valid = true;
    private List<String> errors = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();

    // Address validation
    private boolean addressValid = true;
    private String standardizedAddress1;
    private String standardizedCity;
    private String standardizedState;
    private String standardizedPostalCode;

    // Computed values
    private String computedServiceLevel;
    private int computedPriority;

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
