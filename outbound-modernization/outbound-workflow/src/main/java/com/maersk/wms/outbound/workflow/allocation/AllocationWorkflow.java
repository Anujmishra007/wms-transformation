package com.maersk.wms.outbound.workflow.allocation;

import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Temporal workflow interface for Order Allocation.
 * Orchestrates the allocation process.
 */
@WorkflowInterface
public interface AllocationWorkflow {

    /**
     * Executes the allocation workflow.
     */
    @WorkflowMethod
    AllocationWorkflowResult execute(AllocationWorkflowRequest request);

    /**
     * Signal to pause allocation.
     */
    @SignalMethod
    void pauseAllocation(String reason);

    /**
     * Signal to resume allocation.
     */
    @SignalMethod
    void resumeAllocation();

    /**
     * Signal to cancel allocation.
     */
    @SignalMethod
    void cancelAllocation(String reason);

    /**
     * Signal to approve partial allocation.
     */
    @SignalMethod
    void approvePartialAllocation(boolean approved);

    /**
     * Query for current status.
     */
    @QueryMethod
    AllocationWorkflowStatus getStatus();

    /**
     * Query for current state.
     */
    @QueryMethod
    AllocationWorkflowState getState();

    /**
     * Query for allocation progress.
     */
    @QueryMethod
    AllocationProgress getProgress();
}
