package com.maersk.wms.inbound.workflow.putaway;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Result of putaway workflow execution.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PutawayWorkflowResult {
    private String receiptKey;
    private boolean success;
    private PutawayWorkflowStatus status;
    private int tasksCreated;
    private int tasksCompleted;
    private int tasksCancelled;
    private List<PutawayTaskInfo> completedTasks;
    private String message;
}
