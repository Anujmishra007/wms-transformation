package com.maersk.wms.task.workflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Result object from task assignment workflow.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskAssignmentResult {

    private boolean success;
    private Long taskKey;
    private String taskId;

    private String assignedUserId;
    private String assignedUserName;
    private String assignedWorkGroup;
    private LocalDateTime assignedAt;

    private String assignmentStrategy;
    private Integer attemptCount;
    private List<String> triedUserIds;

    private boolean queued;
    private String queueCode;

    private String errorCode;
    private String errorMessage;
    private List<String> auditTrail;
}
