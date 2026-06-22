package com.maersk.wms.outbound.plugin.shipping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Request for scheduling carrier pickup.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickupRequest {

    private LocalDate pickupDate;
    private LocalTime readyTime;
    private LocalTime closeTime;

    private String pickupLocation;
    private String contactName;
    private String contactPhone;

    private int totalPackages;
    private int totalWeight;
    private String weightUom;

    private boolean residentialPickup;
    private String specialInstructions;
}
