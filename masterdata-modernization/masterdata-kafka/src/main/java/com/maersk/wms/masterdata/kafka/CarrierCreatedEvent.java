package com.maersk.wms.masterdata.kafka;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Event published when a carrier is created.
 */
@Data
@Builder
public class CarrierCreatedEvent {

    private String eventId;
    private LocalDateTime eventTime;
    private String clientCode;
    private String facilityCode;

    private String carrierCode;
    private String carrierName;
    private String carrierType;
    private String scacCode;
    private boolean hazmatCertified;
    private boolean trackingEnabled;

    private String createdBy;
}
