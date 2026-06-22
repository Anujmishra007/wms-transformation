package com.maersk.wms.outbound.workflow;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Result of a wave planning workflow execution.
 */
@Data
@Builder
public class WavePlanningResult {

    private String waveNumber;
    private WavePlanningStatus status;
    private String message;

    // Wave contents
    private int orderCount;
    private int lineCount;
    private int totalUnits;
    private List<String> orderNumbers;
    private List<String> excludedOrders;

    // Release results (if auto-released)
    private boolean released;
    private List<String> allocationIds;

    // Timing
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private long durationMs;

    // Errors if any
    private List<String> errors;
    private Map<String, String> metadata;
}
