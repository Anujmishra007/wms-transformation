package com.maersk.wms.outbound.workflow;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Request to start a wave planning workflow.
 */
@Data
@Builder
public class WavePlanningRequest {

    private String clientCode;
    private String facilityCode;
    private String userId;

    // Wave parameters
    private String waveType;
    private List<String> orderNumbers;
    private LocalDateTime targetShipDate;
    private String carrier;
    private String shipMethod;

    // Wave constraints
    private int maxOrdersPerWave;
    private int maxLinesPerWave;
    private int maxUnitsPerWave;

    // Processing options
    private boolean autoRelease;
    private boolean autoAllocate;

    // Additional context
    private Map<String, String> metadata;
}
