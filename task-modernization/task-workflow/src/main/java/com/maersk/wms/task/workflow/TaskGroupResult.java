package com.maersk.wms.task.workflow;

import com.maersk.wms.task.domain.enums.TaskGroupStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Result object from task group workflow.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskGroupResult {

    private boolean success;
    private Long taskGroupKey;
    private String taskGroupId;
    private TaskGroupStatus finalStatus;

    private Integer totalTasks;
    private Integer completedTasks;
    private Integer failedTasks;
    private Integer cancelledTasks;
    private Double completionPercent;

    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private Integer totalMinutes;

    private List<Long> completedTaskKeys;
    private List<Long> failedTaskKeys;

    private String errorCode;
    private String errorMessage;
    private List<String> warnings;
    private List<String> auditTrail;
}
