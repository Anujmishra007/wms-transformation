package com.maersk.wms.outbound.domain.shipping.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Collections;
import java.util.List;

/**
 * Validation result DTO for label generation operations.
 */
@Data
@Builder
public class LabelValidationResult {
    private boolean valid;
    @Singular("error")
    private List<String> errors;
    @Singular("warning")
    private List<String> warnings;
    private String errorCode;

    public static LabelValidationResult success() {
        return LabelValidationResult.builder()
                .valid(true)
                .errors(Collections.emptyList())
                .warnings(Collections.emptyList())
                .build();
    }
}
