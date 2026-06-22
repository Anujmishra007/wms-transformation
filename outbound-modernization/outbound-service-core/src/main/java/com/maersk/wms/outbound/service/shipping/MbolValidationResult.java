package com.maersk.wms.outbound.service.shipping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Result of MBOL validation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MbolValidationResult {

    private String mbolKey;
    private boolean valid;

    @Builder.Default
    private List<String> errors = new ArrayList<>();

    @Builder.Default
    private List<String> warnings = new ArrayList<>();

    public static MbolValidationResult success(String mbolKey) {
        return MbolValidationResult.builder()
                .mbolKey(mbolKey)
                .valid(true)
                .build();
    }

    public static MbolValidationResult failure(String mbolKey, String error) {
        MbolValidationResult result = new MbolValidationResult();
        result.setMbolKey(mbolKey);
        result.setValid(false);
        result.getErrors().add(error);
        return result;
    }

    public MbolValidationResult error(String errorMessage) {
        this.valid = false;
        this.errors.add(errorMessage);
        return this;
    }

    public MbolValidationResult warning(String warningMessage) {
        this.warnings.add(warningMessage);
        return this;
    }
}
