package com.maersk.wms.outbound.kafka;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Event published when a wave is created.
 */
@Data
@Builder
public class WaveCreatedEvent {

    private String eventId;
    private LocalDateTime eventTime;
    private String clientCode;
    private String facilityCode;

    private String waveNumber;
    private String waveType;
    private int orderCount;
    private int lineCount;
    private int totalUnits;
    private List<String> orderNumbers;
    private String createdBy;
}
