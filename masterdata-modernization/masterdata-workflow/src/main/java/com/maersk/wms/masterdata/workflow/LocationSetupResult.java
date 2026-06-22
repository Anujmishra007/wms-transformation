package com.maersk.wms.masterdata.workflow;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Result of location setup workflow.
 */
@Data
@Builder
public class LocationSetupResult {

    private boolean success;
    private int totalLocations;
    private int createdCount;
    private int failureCount;
    private int skippedCount;  // Already existed
    private List<String> createdLocationCodes;
    private List<String> errors;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private long durationMs;
}
