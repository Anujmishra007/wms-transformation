package com.maersk.wms.inbound.rules;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Result of receiving validation rules.
 */
@Data
public class ReceivingValidationResult {

    private boolean valid;
    private boolean allowReceive;
    private boolean requiresApproval;
    private String approvalReason;
    private List<String> errors = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();

    public void addError(String error) {
        errors.add(error);
        this.valid = false;
        this.allowReceive = false;
    }

    public void addWarning(String warning) {
        warnings.add(warning);
    }

    public void requireApproval(String reason) {
        this.requiresApproval = true;
        this.approvalReason = reason;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }
}
