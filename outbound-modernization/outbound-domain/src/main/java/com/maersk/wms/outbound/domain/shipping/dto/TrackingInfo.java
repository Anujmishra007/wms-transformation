package com.maersk.wms.outbound.domain.shipping.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

/**
 * DTO for tracking information from carrier.
 */
@Data
@Builder
public class TrackingInfo {
    private String trackingNumber;
    private String carrierCode;
    private String status;
    private String statusDescription;
    private String signedBy;
    private Instant deliveryDate;
    private Instant estimatedDeliveryDate;
    private List<TrackingEvent> events;

    @Data
    @Builder
    public static class TrackingEvent {
        private Instant timestamp;
        private String status;
        private String description;
        private String city;
        private String state;
        private String country;
        private String postalCode;
    }
}
