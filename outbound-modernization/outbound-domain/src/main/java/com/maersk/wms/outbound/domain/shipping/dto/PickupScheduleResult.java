package com.maersk.wms.outbound.domain.shipping.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Result DTO for pickup scheduling operations.
 */
@Data
@Builder
public class PickupScheduleResult {
    private boolean success;
    private String confirmationNumber;
    private LocalDate pickupDate;
    private LocalTime pickupTimeWindowStart;
    private LocalTime pickupTimeWindowEnd;
    private LocalDateTime scheduledPickupTime;
    private LocalDateTime scheduledPickupDateTime;
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
}
