package com.maersk.wms.task.workflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Progress tracking for task group workflow.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskGroupProgress {

    private Integer totalTasks;
    private Integer completedTasks;
    private Integer inProgressTasks;
    private Integer pendingTasks;
    private Integer failedTasks;

    private Double completionPercent;
    private Integer elapsedMinutes;
    private Integer estimatedRemainingMinutes;

    private String currentPhase;
    private String status;
}
