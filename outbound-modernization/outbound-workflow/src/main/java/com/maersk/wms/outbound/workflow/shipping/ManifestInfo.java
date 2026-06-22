package com.maersk.wms.outbound.workflow.shipping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Manifest information for query response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManifestInfo {

    private String manifestKey;
    private String manifestNumber;
    private String carrierCode;
    private String status;
    private String type;

    private String facility;
    private String door;

    private int totalShipments;
    private int totalPackages;
    private BigDecimal totalWeight;
    private String weightUom;

    private LocalDateTime manifestDate;
    private LocalDateTime closeDate;
    private LocalDateTime transmittedDate;

    private String transmissionId;
    private String transmissionStatus;

    // Pickup info
    private String pickupConfirmation;
    private LocalDateTime scheduledPickupTime;
    private LocalDateTime actualPickupTime;
}
