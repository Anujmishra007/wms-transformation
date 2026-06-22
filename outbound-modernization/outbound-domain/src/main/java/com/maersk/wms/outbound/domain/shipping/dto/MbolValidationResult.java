package com.maersk.wms.outbound.domain.shipping.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Collections;
import java.util.List;

/**
 * Validation result DTO for MBOL (Master Bill of Lading) operations.
 */
@Data
@Builder
public class MbolValidationResult {
    private boolean valid;
    private String mbolKey;
    @Singular("error")
    private List<String> errors;
    @Singular("warning")
    private List<String> warnings;
    private String errorCode;
    private boolean requiresApproval;
    private String approvalReason;

    public static MbolValidationResult success(String mbolKey) {
        return MbolValidationResult.builder()
                .valid(true)
                .mbolKey(mbolKey)
                .errors(Collections.emptyList())
                .warnings(Collections.emptyList())
                .build();
    }
}
