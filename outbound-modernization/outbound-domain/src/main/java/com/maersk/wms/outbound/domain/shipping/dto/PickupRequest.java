package com.maersk.wms.outbound.domain.shipping.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Request DTO for pickup scheduling operations.
 */
@Data
@Builder
public class PickupRequest {
    private String manifestKey;
    private String carrierCode;
    private String carrierAccountNumber;
    private LocalDate requestedPickupDate;
    private LocalDate pickupDate;
    private LocalTime preferredTimeWindowStart;
    private LocalTime preferredTimeWindowEnd;
    private LocalTime readyTime;
    private LocalTime closeTime;
    private String pickupLocation;
    private String contactName;
    private String contactPhone;
    private String specialInstructions;
    private int packageCount;
    private int totalPackages;
    private double totalWeight;
    private String weightUom;
    private boolean residentialPickup;
}
