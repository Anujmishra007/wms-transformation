package com.maersk.wms.masterdata.kafka;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Event published when a customer is created.
 */
@Data
@Builder
public class CustomerCreatedEvent {

    private String eventId;
    private LocalDateTime eventTime;
    private String clientCode;
    private String facilityCode;

    private String customerCode;
    private String customerName;
    private String customerType;
    private String city;
    private String state;
    private String country;

    private String createdBy;
}
