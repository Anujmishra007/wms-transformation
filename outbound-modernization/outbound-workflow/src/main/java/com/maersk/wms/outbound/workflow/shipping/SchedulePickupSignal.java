package com.maersk.wms.outbound.workflow.shipping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Signal to schedule carrier pickup.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchedulePickupSignal {

    // Pickup date/time
    private LocalDate pickupDate;
    private LocalTime readyTime;
    private LocalTime closeTime;

    // Pickup location
    private String pickupLocation;
    private String pickupAddress;
    private String pickupCity;
    private String pickupState;
    private String pickupZip;
    private String pickupCountry;

    // Contact info
    private String contactName;
    private String contactPhone;
    private String contactEmail;

    // Package summary
    private int totalPackages;
    private int totalWeight;
    private String weightUom;
    private int totalPallets;

    // Special instructions
    private boolean residentialPickup;
    private boolean liftGateRequired;
    private boolean insidePickup;
    private String specialInstructions;

    // User scheduling the pickup
    private String userId;
}
