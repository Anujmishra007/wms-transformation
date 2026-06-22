package com.maersk.wms.outbound.plugin.shipping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Tracking information from carrier.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingInfo {

    private String trackingNumber;
    private String carrierCode;
    private String status;
    private String statusDescription;

    private LocalDateTime shipDate;
    private LocalDateTime estimatedDeliveryDate;
    private LocalDateTime actualDeliveryDate;

    private String currentLocation;
    private String signedBy;

    @Builder.Default
    private List<TrackingEvent> events = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrackingEvent {
        private LocalDateTime timestamp;
        private String status;
        private String description;
        private String location;
        private String city;
        private String state;
        private String country;
    }
}
