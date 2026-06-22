package com.maersk.wms.outbound.plugin.shipping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Result of pickup scheduling.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickupScheduleResult {

    private boolean success;
    private String confirmationNumber;
    private LocalDateTime scheduledPickupTime;
    private String driverName;
    private String driverPhone;
    private String errorCode;
    private String errorMessage;

    public static PickupScheduleResult success(String confirmationNumber) {
        return PickupScheduleResult.builder()
                .success(true)
                .confirmationNumber(confirmationNumber)
                .build();
    }

    public static PickupScheduleResult failure(String errorCode, String errorMessage) {
        return PickupScheduleResult.builder()
                .success(false)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build();
    }
}
