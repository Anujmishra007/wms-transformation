package com.maersk.wms.inbound.workflow;

/**
 * Status enumeration for receiving workflow.
 */
public enum ReceivingWorkflowStatus {

    INITIATED("Workflow initiated"),
    VALIDATING("Validating ASN/PO"),
    CREATING_RECEIPT("Creating receipt"),
    RECEIVING("Receiving in progress"),
    GENERATING_PUTAWAY("Generating putaway tasks"),
    CLOSING("Closing receipt"),
    COMPLETED("Workflow completed"),
    FAILED("Workflow failed"),
    CANCELLED("Workflow cancelled");

    private final String description;

    ReceivingWorkflowStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
