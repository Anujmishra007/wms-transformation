package com.maersk.wms.task.workflow;

import com.maersk.wms.task.domain.entity.TaskGroup;
import com.maersk.wms.task.domain.entity.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request object for task group workflow.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskGroupRequest {

    private TaskGroup taskGroup;
    private List<Task> tasks;
    private String clientCode;
    private String facilityCode;
    private String initiatedBy;

    // Processing settings
    private Boolean autoRelease;
    private Boolean autoAssignTasks;
    private String assignmentStrategy;
    private Integer maxConcurrentTasks;

    // Completion settings
    private Boolean requireAllTasksComplete;
    private Double minimumCompletionPercent;
    private Boolean allowPartialCompletion;

    // Timeout settings
    private Integer groupTimeoutMinutes;
    private Integer taskTimeoutMinutes;
}
