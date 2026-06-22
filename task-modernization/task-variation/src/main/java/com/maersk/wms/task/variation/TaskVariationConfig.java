package com.maersk.wms.task.variation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Task configuration resolved for a specific client/facility context.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskVariationConfig {

    private String clientCode;
    private String facilityCode;

    // Auto-assignment settings
    private Boolean autoAssignEnabled;
    private String autoAssignStrategy;

    // Priority escalation settings
    private Boolean priorityEscalationEnabled;
    private Integer escalationThresholdMinutes;

    // Assignment limits
    private Integer maxTasksPerUser;

    // Task timeout
    private Integer defaultTaskTimeoutMinutes;

    // Shortage handling
    private Boolean allowShortage;
    private Boolean requireReasonForShort;

    // Routing
    private Boolean enableZoneRouting;

    // Batching
    private Boolean enableBatching;
    private Integer defaultBatchSize;
}
