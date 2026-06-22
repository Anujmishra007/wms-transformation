package com.maersk.wms.inbound.workflow.receiving;

/**
 * Status values for receiving workflow.
 */
public enum ReceivingWorkflowStatus {
    INITIALIZED(0),
    INITIALIZING(5),
    AWAITING_START(10),
    RECEIVING(20),
    VALIDATING(30),
    AWAITING_APPROVAL(35),
    COMPLETING(40),
    PUTAWAY_TRIGGERED(50),
    COMPLETED(100),
    CANCELLED(-1),
    FAILED(-2);

    private final int progressPercent;

    ReceivingWorkflowStatus(int progressPercent) {
        this.progressPercent = progressPercent;
    }

    public int getProgressPercent() {
        return progressPercent;
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED || this == FAILED;
    }

    public boolean isActive() {
        return !isTerminal() && this != INITIALIZED;
    }
}
