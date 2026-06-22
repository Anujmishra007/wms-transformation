package com.maersk.wms.inbound.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.QueryMethod;

/**
 * Temporal workflow for receiving operations.
 * Orchestrates the complete receiving process from ASN check-in to putaway.
 */
@WorkflowInterface
public interface ReceivingWorkflow {

    /**
     * Execute the receiving workflow.
     *
     * @param request The receiving request containing ASN/PO details
     * @return The workflow result containing receipt information
     */
    @WorkflowMethod
    ReceivingWorkflowResult execute(ReceivingWorkflowRequest request);

    /**
     * Signal to receive a line item.
     */
    @SignalMethod
    void receiveLineItem(ReceiveLineItemSignal signal);

    /**
     * Signal to complete receiving.
     */
    @SignalMethod
    void completeReceiving();

    /**
     * Signal to cancel receiving.
     */
    @SignalMethod
    void cancelReceiving(String reason);

    /**
     * Query current workflow status.
     */
    @QueryMethod
    ReceivingWorkflowStatus getStatus();

    /**
     * Query received lines.
     */
    @QueryMethod
    int getReceivedLineCount();
}
