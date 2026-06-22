package com.maersk.wms.outbound.workflow.picking;

import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Temporal workflow interface for Picking Operations.
 * Orchestrates the picking process for RDT devices.
 */
@WorkflowInterface
public interface PickingWorkflow {

    /**
     * Executes the picking workflow.
     */
    @WorkflowMethod
    PickingWorkflowResult execute(PickingWorkflowRequest request);

    /**
     * Signal: Record a pick confirmation.
     */
    @SignalMethod
    void confirmPick(ConfirmPickSignal signal);

    /**
     * Signal: Record a short pick.
     */
    @SignalMethod
    void shortPick(ShortPickSignal signal);

    /**
     * Signal: Skip current pick.
     */
    @SignalMethod
    void skipPick(String pickDetailKey, String reason);

    /**
     * Signal: Complete the picking session.
     */
    @SignalMethod
    void completeSession();

    /**
     * Signal: Pause the picking session.
     */
    @SignalMethod
    void pauseSession(String reason);

    /**
     * Signal: Cancel the picking session.
     */
    @SignalMethod
    void cancelSession(String reason);

    /**
     * Query for current status.
     */
    @QueryMethod
    PickingWorkflowStatus getStatus();

    /**
     * Query for current pick instruction.
     */
    @QueryMethod
    PickInstruction getCurrentPick();

    /**
     * Query for session progress.
     */
    @QueryMethod
    PickingProgress getProgress();
}
