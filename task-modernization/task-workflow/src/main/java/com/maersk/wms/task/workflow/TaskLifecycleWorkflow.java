package com.maersk.wms.task.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.QueryMethod;

/**
 * Temporal workflow for managing task lifecycle from creation to completion.
 */
@WorkflowInterface
public interface TaskLifecycleWorkflow {

    /**
     * Main workflow method that orchestrates task lifecycle.
     */
    @WorkflowMethod
    TaskLifecycleResult execute(TaskLifecycleRequest request);

    /**
     * Signal to assign the task to a user.
     */
    @SignalMethod
    void assignToUser(String userId, String assignedBy);

    /**
     * Signal to start task execution.
     */
    @SignalMethod
    void startTask(String startedBy);

    /**
     * Signal to complete the task.
     */
    @SignalMethod
    void completeTask(Double completedQuantity, String completedBy);

    /**
     * Signal to short the task.
     */
    @SignalMethod
    void shortTask(Double shortQuantity, String reason, String shortedBy);

    /**
     * Signal to cancel the task.
     */
    @SignalMethod
    void cancelTask(String reason, String cancelledBy);

    /**
     * Signal to put task on hold.
     */
    @SignalMethod
    void holdTask(String reason, String heldBy);

    /**
     * Signal to release task from hold.
     */
    @SignalMethod
    void releaseFromHold(String releasedBy);

    /**
     * Signal to reassign task to different user.
     */
    @SignalMethod
    void reassignTask(String newUserId, String reason, String reassignedBy);

    /**
     * Query current task state.
     */
    @QueryMethod
    String getCurrentState();

    /**
     * Query assigned user.
     */
    @QueryMethod
    String getAssignedUser();

    /**
     * Query if task is complete.
     */
    @QueryMethod
    boolean isComplete();
}
