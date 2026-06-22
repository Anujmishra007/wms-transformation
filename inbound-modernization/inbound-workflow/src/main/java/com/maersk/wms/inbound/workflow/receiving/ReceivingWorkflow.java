package com.maersk.wms.inbound.workflow.receiving;

import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Temporal Workflow interface for Receiving operations.
 *
 * Orchestrates the receiving process from ASN/PO through receipt completion.
 *
 * Legacy SP Reference: rdtfnc_Receiving
 */
@WorkflowInterface
public interface ReceivingWorkflow {

    /**
     * Execute the receiving workflow.
     */
    @WorkflowMethod
    ReceivingWorkflowResult execute(ReceivingWorkflowRequest request);

    // ==================== SIGNALS ====================

    /**
     * Signal to start receiving on a receipt.
     */
    @SignalMethod
    void startReceiving();

    /**
     * Signal to receive a line item.
     */
    @SignalMethod
    void receiveLineItem(ReceiveLineSignal signal);

    /**
     * Signal to complete receiving.
     */
    @SignalMethod
    void completeReceiving();

    /**
     * Signal to trigger putaway for received items.
     */
    @SignalMethod
    void triggerPutaway();

    /**
     * Signal to cancel the receiving workflow.
     */
    @SignalMethod
    void cancelReceiving(String reason);

    /**
     * Signal to handle over-receipt approval.
     */
    @SignalMethod
    void approveOverReceipt(String lineNumber, boolean approved);

    /**
     * Signal to handle damage reporting.
     */
    @SignalMethod
    void reportDamage(DamageReportSignal signal);

    // ==================== QUERIES ====================

    /**
     * Query the current workflow status.
     */
    @QueryMethod
    ReceivingWorkflowStatus getStatus();

    /**
     * Query the current workflow state.
     */
    @QueryMethod
    ReceivingWorkflowState getState();

    /**
     * Query receipt progress.
     */
    @QueryMethod
    ReceiptProgress getProgress();

    /**
     * Query items ready for putaway.
     */
    @QueryMethod
    java.util.List<ReadyForPutawayItem> getItemsReadyForPutaway();
}
