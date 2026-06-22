package com.maersk.wms.task.workflow;

import com.maersk.wms.task.domain.entity.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request object for task assignment workflow.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskAssignmentRequest {

    private Task task;
    private String clientCode;
    private String facilityCode;

    // Assignment preferences
    private String preferredUserId;
    private String preferredWorkGroup;
    private String preferredWorkZone;
    private List<String> excludedUserIds;

    // Assignment strategy
    private String assignmentStrategy; // ROUND_ROBIN, LEAST_LOADED, ZONE_PROXIMITY, SKILL_MATCH

    // Timeout and retry settings
    private Integer timeoutMinutes;
    private Integer maxRetries;
    private Integer retryDelaySeconds;

    // Fallback settings
    private Boolean allowFallbackToAnyUser;
    private Boolean queueIfNoUserAvailable;
    private String fallbackQueueCode;
}
