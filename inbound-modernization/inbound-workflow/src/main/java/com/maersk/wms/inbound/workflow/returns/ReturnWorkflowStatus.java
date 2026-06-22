package com.maersk.wms.inbound.workflow.returns;

/**
 * Status enumeration for return workflow.
 * Represents the progression through the return lifecycle.
 */
public enum ReturnWorkflowStatus {

    // Initialization
    INITIATED("Workflow initiated"),
    VALIDATING("Validating RMA and order"),
    CREATING_RETURN("Creating return record"),

    // Receiving phase
    AWAITING_RECEIPT("Awaiting package receipt"),
    RECEIVING("Receiving in progress"),
    RECEIVING_COMPLETED("Receiving completed"),

    // Inspection phase
    AWAITING_INSPECTION("Awaiting inspection"),
    INSPECTING("Inspection in progress"),
    INSPECTION_COMPLETED("Inspection completed"),

    // Disposition phase
    ASSIGNING_DISPOSITION("Assigning dispositions"),
    DISPOSITION_COMPLETED("All dispositions assigned"),

    // Processing phase
    PROCESSING_INVENTORY("Processing inventory updates"),
    GENERATING_PUTAWAY("Generating putaway tasks"),
    CALCULATING_REFUND("Calculating refund"),
    GENERATING_CREDIT_MEMO("Generating credit memo"),

    // Closing phase
    CLOSING("Closing return"),
    CLOSED("Return closed"),

    // Terminal states
    COMPLETED("Workflow completed successfully"),
    FAILED("Workflow failed"),
    CANCELLED("Workflow cancelled"),

    // Special states
    ON_HOLD("Return on hold"),
    PENDING_APPROVAL("Pending approval");

    private final String description;

    ReturnWorkflowStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if workflow is in a terminal state.
     */
    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED;
    }

    /**
     * Check if workflow is in receiving phase.
     */
    public boolean isReceivingPhase() {
        return this == AWAITING_RECEIPT || this == RECEIVING || this == RECEIVING_COMPLETED;
    }

    /**
     * Check if workflow is in inspection phase.
     */
    public boolean isInspectionPhase() {
        return this == AWAITING_INSPECTION || this == INSPECTING || this == INSPECTION_COMPLETED;
    }

    /**
     * Check if workflow is in processing phase.
     */
    public boolean isProcessingPhase() {
        return this == PROCESSING_INVENTORY || this == GENERATING_PUTAWAY ||
               this == CALCULATING_REFUND || this == GENERATING_CREDIT_MEMO;
    }

    /**
     * Check if signals are allowed in current state.
     */
    public boolean allowsSignals() {
        return !isTerminal() && this != ON_HOLD;
    }
}
