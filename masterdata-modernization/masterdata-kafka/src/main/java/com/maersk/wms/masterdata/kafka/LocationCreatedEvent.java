package com.maersk.wms.masterdata.kafka;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Event published when a location is created.
 */
@Data
@Builder
public class LocationCreatedEvent {

    private String eventId;
    private LocalDateTime eventTime;
    private String clientCode;
    private String facilityCode;

    private String locationCode;
    private String locationType;
    private String zone;
    private String aisle;
    private String bay;
    private String level;
    private String position;
    private BigDecimal maxWeight;
    private BigDecimal maxCube;
    private boolean pickLocation;
    private boolean putawayLocation;
    private int pickPathSequence;

    private String createdBy;
}
