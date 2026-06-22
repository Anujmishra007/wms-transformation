package com.maersk.wms.inbound.workflow.putaway;

import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

import java.util.List;

/**
 * Temporal Workflow interface for Putaway operations.
 *
 * Orchestrates the putaway process from task creation through completion.
 *
 * Legacy SP Reference: Part of rdtfnc_Receiving (putaway phase)
 */
@WorkflowInterface
public interface PutawayWorkflow {

    /**
     * Execute the putaway workflow.
     */
    @WorkflowMethod
    PutawayWorkflowResult execute(PutawayWorkflowRequest request);

    // ==================== SIGNALS ====================

    /**
     * Signal to start a putaway task.
     */
    @SignalMethod
    void startTask(String taskKey);

    /**
     * Signal to complete a putaway task.
     */
    @SignalMethod
    void completeTask(CompleteTaskSignal signal);

    /**
     * Signal to redirect to a different location.
     */
    @SignalMethod
    void redirectTask(RedirectTaskSignal signal);

    /**
     * Signal to cancel a task.
     */
    @SignalMethod
    void cancelTask(String taskKey, String reason);

    /**
     * Signal to add more items to putaway.
     */
    @SignalMethod
    void addItem(AddItemSignal signal);

    // ==================== QUERIES ====================

    /**
     * Query current workflow status.
     */
    @QueryMethod
    PutawayWorkflowStatus getStatus();

    /**
     * Query pending tasks.
     */
    @QueryMethod
    List<PutawayTaskInfo> getPendingTasks();

    /**
     * Query completed tasks.
     */
    @QueryMethod
    List<PutawayTaskInfo> getCompletedTasks();

    /**
     * Query progress.
     */
    @QueryMethod
    PutawayProgress getProgress();
}
