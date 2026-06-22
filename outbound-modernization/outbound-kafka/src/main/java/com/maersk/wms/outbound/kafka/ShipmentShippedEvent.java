package com.maersk.wms.outbound.kafka;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Event published when a shipment is shipped (ship confirmed).
 */
@Data
@Builder
public class ShipmentShippedEvent {

    private String eventId;
    private LocalDateTime eventTime;
    private String clientCode;
    private String facilityCode;

    private String shipmentId;
    private String orderNumber;
    private String trackingNumber;
    private String carrier;
    private String shipMethod;
    private int cartonCount;
    private BigDecimal totalWeight;
    private BigDecimal freightCharge;

    private String shipToName;
    private String shipToCity;
    private String shipToState;
    private String shipToCountry;
    private String shipToZip;

    private List<CartonShippedEvent> cartons;
    private String shippedBy;
    private LocalDateTime shippedAt;

    @Data
    @Builder
    public static class CartonShippedEvent {
        private String cartonId;
        private String trackingNumber;
        private BigDecimal weight;
    }
}
