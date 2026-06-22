package com.maersk.wms.masterdata.kafka;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Event published when an item is updated.
 */
@Data
@Builder
public class ItemUpdatedEvent {

    private String eventId;
    private LocalDateTime eventTime;
    private String clientCode;
    private String facilityCode;

    private String sku;
    private Map<String, Object> changedFields;
    private String updatedBy;
}
