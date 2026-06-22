package com.maersk.wms.inbound.workflow.receiving;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Receipt progress information for queries.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptProgress {
    private String receiptKey;
    private int totalLines;
    private int receivedLines;
    private double percentComplete;
    private ReceivingWorkflowStatus status;
}
