package com.maersk.wms.task.workflow;

import com.maersk.wms.task.domain.entity.Task;
import com.maersk.wms.task.domain.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Result object from task lifecycle workflow.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskLifecycleResult {

    private boolean success;
    private Task task;
    private TaskStatus finalStatus;

    private String assignedUserId;
    private LocalDateTime assignedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    private Double originalQuantity;
    private Double completedQuantity;
    private Double shortQuantity;

    private Integer estimatedMinutes;
    private Integer actualMinutes;

    private String errorCode;
    private String errorMessage;
    private List<String> warnings;
    private List<String> auditTrail;
}
