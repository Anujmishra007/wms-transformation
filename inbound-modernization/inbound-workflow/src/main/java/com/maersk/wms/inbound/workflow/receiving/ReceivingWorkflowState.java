package com.maersk.wms.inbound.workflow.receiving;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Internal state tracking for receiving workflow.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceivingWorkflowState {
    private String receiptKey;
    private String asnKey;
    private String poKey;
    private String storerKey;
    private int totalLines;
    private int linesReceived;
    private int linesPutAway;
    private String currentPhase;
    private String errorMessage;
}
