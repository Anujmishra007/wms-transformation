package com.maersk.wms.task.workflow;

import com.maersk.wms.task.domain.entity.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request object for task lifecycle workflow.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskLifecycleRequest {

    private Task task;
    private String clientCode;
    private String facilityCode;
    private String initiatedBy;

    // Auto-assignment settings
    private Boolean autoAssign;
    private String preferredWorkGroup;
    private String preferredUserId;

    // Timeout settings
    private Integer assignmentTimeoutMinutes;
    private Integer executionTimeoutMinutes;

    // Escalation settings
    private Boolean enableEscalation;
    private Integer escalationThresholdMinutes;
}
