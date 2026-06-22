package com.maersk.wms.outbound.kafka;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Event published when a wave is released.
 */
@Data
@Builder
public class WaveReleasedEvent {

    private String eventId;
    private LocalDateTime eventTime;
    private String clientCode;
    private String facilityCode;

    private String waveNumber;
    private int orderCount;
    private int lineCount;
    private int totalUnits;
    private List<String> orderNumbers;
    private String releasedBy;
    private LocalDateTime releasedAt;
}
