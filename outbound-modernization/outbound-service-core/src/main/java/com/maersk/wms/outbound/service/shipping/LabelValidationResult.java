package com.maersk.wms.outbound.service.shipping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Result of label validation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabelValidationResult {

    private boolean valid;

    @Builder.Default
    private List<String> errors = new ArrayList<>();

    @Builder.Default
    private List<String> warnings = new ArrayList<>();

    // Address validation details
    private boolean addressVerified;
    private String suggestedAddress;
    private boolean residentialIndicator;

    public LabelValidationResult error(String errorMessage) {
        this.valid = false;
        this.errors.add(errorMessage);
        return this;
    }

    public LabelValidationResult warning(String warningMessage) {
        this.warnings.add(warningMessage);
        return this;
    }

    public static LabelValidationResult success() {
        return LabelValidationResult.builder()
                .valid(true)
                .addressVerified(true)
                .build();
    }

    public static LabelValidationResult failure(String error) {
        LabelValidationResult result = new LabelValidationResult();
        result.setValid(false);
        result.getErrors().add(error);
        return result;
    }
}
