package com.maersk.wms.outbound.domain.shipping.dto;

import com.maersk.wms.outbound.domain.shipping.Carrier;
import lombok.Builder;
import lombok.Data;

/**
 * Validation result DTO for carrier change operations.
 */
@Data
@Builder
public class CarrierChangeValidation {
    private boolean valid;
    private String errorCode;
    private String errorMessage;
    private boolean requiresApproval;
    private String approvalReason;
    private Carrier newCarrier;
}
