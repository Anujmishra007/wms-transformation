package com.maersk.wms.inbound.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.QueryMethod;

/**
 * Temporal workflow for putaway operations.
 * Orchestrates the putaway process from task assignment to completion.
 */
@WorkflowInterface
public interface PutawayWorkflow {

    /**
     * Execute the putaway workflow.
     *
     * @param request The putaway request
     * @return The workflow result
     */
    @WorkflowMethod
    PutawayWorkflowResult execute(PutawayWorkflowRequest request);

    /**
     * Signal task started.
     */
    @SignalMethod
    void taskStarted(String taskKey);

    /**
     * Signal task completed.
     */
    @SignalMethod
    void taskCompleted(String taskKey, String actualLocation, String actualLpn);

    /**
     * Signal task shorted.
     */
    @SignalMethod
    void taskShorted(String taskKey, int actualQty, String reason);

    /**
     * Query current status.
     */
    @QueryMethod
    PutawayWorkflowStatus getStatus();
}
