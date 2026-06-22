package com.maersk.wms.outbound.service.shipping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Result of manifest transmission to carrier.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManifestTransmissionResult {

    private boolean success;
    private String manifestKey;
    private String carrierConfirmationNumber;
    private String carrierReference;
    private LocalDateTime transmittedAt;
    private String manifestUrl;
    private String errorCode;
    private String errorMessage;

    public static ManifestTransmissionResult success(String manifestKey, String confirmationNumber) {
        return ManifestTransmissionResult.builder()
                .success(true)
                .manifestKey(manifestKey)
                .carrierConfirmationNumber(confirmationNumber)
                .transmittedAt(LocalDateTime.now())
                .build();
    }

    public static ManifestTransmissionResult failure(String manifestKey, String errorCode, String errorMessage) {
        return ManifestTransmissionResult.builder()
                .success(false)
                .manifestKey(manifestKey)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build();
    }
}
