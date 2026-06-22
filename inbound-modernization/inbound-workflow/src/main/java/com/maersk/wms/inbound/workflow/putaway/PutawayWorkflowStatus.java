package com.maersk.wms.inbound.workflow.putaway;

/**
 * Status values for putaway workflow.
 */
public enum PutawayWorkflowStatus {
    INITIALIZED(0),
    CREATING_TASKS(10),
    AWAITING_START(20),
    IN_PROGRESS(30),
    COMPLETING(40),
    COMPLETED(100),
    PARTIALLY_COMPLETED(90),
    CANCELLED(-1),
    FAILED(-2);

    private final int progressPercent;

    PutawayWorkflowStatus(int progressPercent) {
        this.progressPercent = progressPercent;
    }

    public int getProgressPercent() {
        return progressPercent;
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED || this == FAILED || this == PARTIALLY_COMPLETED;
    }
}
