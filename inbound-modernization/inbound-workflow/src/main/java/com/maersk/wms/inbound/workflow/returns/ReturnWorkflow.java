package com.maersk.wms.inbound.workflow.returns;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.QueryMethod;

import java.util.List;

/**
 * Temporal workflow for return processing operations.
 * Orchestrates the complete return process from receiving through inspection,
 * disposition, and closure.
 *
 * Legacy SP References:
 * - rdtfnc_Return (5,547 lines) - Standard return flow
 * - rdtfnc_EcomReturn (6,099 lines) - E-commerce return flow
 * - rdt_Return_V7_* - Return V7 functions
 * - rdt_EcomReturn_* - E-commerce return functions
 */
@WorkflowInterface
public interface ReturnWorkflow {

    /**
     * Execute the return workflow.
     * Orchestrates: Create → Receive → Inspect → Disposition → Close
     *
     * @param request The return request containing RMA/order details
     * @return The workflow result containing return information
     */
    @WorkflowMethod
    ReturnWorkflowResult execute(ReturnWorkflowRequest request);

    // ========== Receiving Signals ==========

    /**
     * Signal to start receiving the return.
     */
    @SignalMethod
    void startReceiving();

    /**
     * Signal to receive a line item.
     */
    @SignalMethod
    void receiveLineItem(ReceiveReturnLineSignal signal);

    /**
     * Signal to complete receiving phase.
     */
    @SignalMethod
    void completeReceiving();

    // ========== Inspection Signals ==========

    /**
     * Signal to start inspection.
     */
    @SignalMethod
    void startInspection();

    /**
     * Signal to inspect a line item.
     */
    @SignalMethod
    void inspectLineItem(InspectReturnLineSignal signal);

    /**
     * Signal to complete inspection phase.
     */
    @SignalMethod
    void completeInspection();

    // ========== Disposition Signals ==========

    /**
     * Signal to assign disposition to a line.
     */
    @SignalMethod
    void assignDisposition(AssignDispositionSignal signal);

    /**
     * Signal to auto-assign dispositions for all lines.
     */
    @SignalMethod
    void autoAssignAllDispositions();

    // ========== Processing Signals ==========

    /**
     * Signal to close the return.
     */
    @SignalMethod
    void closeReturn();

    /**
     * Signal to cancel the return.
     */
    @SignalMethod
    void cancelReturn(String reason);

    /**
     * Signal to process refund/credit.
     */
    @SignalMethod
    void processRefund();

    // ========== Query Methods ==========

    /**
     * Query current workflow status.
     */
    @QueryMethod
    ReturnWorkflowStatus getStatus();

    /**
     * Query return details.
     */
    @QueryMethod
    ReturnWorkflowState getState();

    /**
     * Query received line count.
     */
    @QueryMethod
    int getReceivedLineCount();

    /**
     * Query inspected line count.
     */
    @QueryMethod
    int getInspectedLineCount();

    /**
     * Query lines pending disposition.
     */
    @QueryMethod
    List<String> getPendingDispositionSkus();

    /**
     * Query calculated refund amount.
     */
    @QueryMethod
    java.math.BigDecimal getCalculatedRefund();
}
