package com.maersk.wms.task.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.QueryMethod;

/**
 * Temporal workflow for managing task groups (waves, batches).
 */
@WorkflowInterface
public interface TaskGroupWorkflow {

    /**
     * Main workflow method that orchestrates task group processing.
     */
    @WorkflowMethod
    TaskGroupResult execute(TaskGroupRequest request);

    /**
     * Signal to release the task group for processing.
     */
    @SignalMethod
    void releaseGroup(String releasedBy);

    /**
     * Signal when a task in the group is completed.
     */
    @SignalMethod
    void taskCompleted(Long taskKey);

    /**
     * Signal when a task in the group fails.
     */
    @SignalMethod
    void taskFailed(Long taskKey, String reason);

    /**
     * Signal to cancel the task group.
     */
    @SignalMethod
    void cancelGroup(String reason, String cancelledBy);

    /**
     * Query current progress.
     */
    @QueryMethod
    TaskGroupProgress getProgress();

    /**
     * Query if group is complete.
     */
    @QueryMethod
    boolean isComplete();
}
