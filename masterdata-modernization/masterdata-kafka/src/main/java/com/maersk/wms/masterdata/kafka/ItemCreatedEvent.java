package com.maersk.wms.masterdata.kafka;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Event published when an item is created.
 */
@Data
@Builder
public class ItemCreatedEvent {

    private String eventId;
    private LocalDateTime eventTime;
    private String clientCode;
    private String facilityCode;

    private String sku;
    private String description;
    private String itemType;
    private String itemGroup;
    private String itemClass;
    private BigDecimal weight;
    private BigDecimal cube;
    private boolean lotControlled;
    private boolean expirationControlled;
    private boolean hazmat;
    private String storageZone;

    private String createdBy;
}
