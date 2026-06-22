package com.maersk.wms.task.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import io.temporal.workflow.QueryMethod;

/**
 * Temporal workflow for automated task assignment.
 */
@WorkflowInterface
public interface TaskAssignmentWorkflow {

    /**
     * Main workflow method that handles task assignment.
     */
    @WorkflowMethod
    TaskAssignmentResult execute(TaskAssignmentRequest request);

    /**
     * Query assignment status.
     */
    @QueryMethod
    String getAssignmentStatus();
}
