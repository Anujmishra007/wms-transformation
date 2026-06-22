package com.maersk.wms.outbound.kafka;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Event published when an order is created.
 */
@Data
@Builder
public class OrderCreatedEvent {

    private String eventId;
    private LocalDateTime eventTime;
    private String clientCode;
    private String facilityCode;

    private String orderNumber;
    private String externalOrderNumber;
    private String customerCode;
    private String orderType;
    private int priority;
    private int lineCount;
    private BigDecimal totalQty;
    private LocalDateTime requiredDate;

    private String shipToName;
    private String shipToCity;
    private String shipToState;
    private String shipToCountry;
    private String carrierCode;
    private String shipMethod;

    private List<OrderLineEvent> lines;

    @Data
    @Builder
    public static class OrderLineEvent {
        private String lineNumber;
        private String sku;
        private BigDecimal quantity;
    }
}
