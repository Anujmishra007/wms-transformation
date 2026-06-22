package com.maersk.wms.outbound.kafka;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Event published when a shipment is created.
 */
@Data
@Builder
public class ShipmentCreatedEvent {

    private String eventId;
    private LocalDateTime eventTime;
    private String clientCode;
    private String facilityCode;

    private String shipmentId;
    private String orderNumber;
    private String carrier;
    private String shipMethod;
    private String shipmentType;
    private int cartonCount;

    private String shipToName;
    private String shipToCity;
    private String shipToState;
    private String shipToCountry;
    private String shipToZip;

    private List<String> cartonIds;
    private String createdBy;
}
