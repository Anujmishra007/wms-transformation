package com.maersk.wms.inbound.workflow;

/**
 * Status enumeration for putaway workflow.
 */
public enum PutawayWorkflowStatus {

    INITIATED("Workflow initiated"),
    ASSIGNING("Assigning tasks"),
    IN_PROGRESS("Putaway in progress"),
    COMPLETING("Completing tasks"),
    COMPLETED("Workflow completed"),
    FAILED("Workflow failed");

    private final String description;

    PutawayWorkflowStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
