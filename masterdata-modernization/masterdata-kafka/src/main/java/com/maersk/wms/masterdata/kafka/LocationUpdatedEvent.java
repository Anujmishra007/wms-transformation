package com.maersk.wms.masterdata.kafka;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Event published when a location is updated.
 */
@Data
@Builder
public class LocationUpdatedEvent {

    private String eventId;
    private LocalDateTime eventTime;
    private String clientCode;
    private String facilityCode;

    private String locationCode;
    private Map<String, Object> changedFields;
    private String updatedBy;
}
