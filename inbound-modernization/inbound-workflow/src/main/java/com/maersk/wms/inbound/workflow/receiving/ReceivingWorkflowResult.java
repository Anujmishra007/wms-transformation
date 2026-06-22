package com.maersk.wms.inbound.workflow.receiving;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Result of receiving workflow execution.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceivingWorkflowResult {
    private String receiptKey;
    private boolean success;
    private ReceivingWorkflowStatus status;
    private int linesReceived;
    private List<ReadyForPutawayItem> readyForPutaway;
    private String message;
}
