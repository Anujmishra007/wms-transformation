package com.maersk.wms.outbound.domain.shipping.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Result DTO for manifest transmission operations.
 */
@Data
@Builder
public class ManifestTransmissionResult {
    private boolean success;
    private String manifestKey;
    private String carrierConfirmationNumber;
    private String transmissionId;
    private LocalDateTime transmittedAt;
    private String errorCode;
    private String errorMessage;
}
