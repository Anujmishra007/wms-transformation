package com.maersk.wms.inbound.workflow.receiving;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request to start a receiving workflow.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceivingWorkflowRequest {
    private String receiptKey;        // Existing receipt key (optional)
    private String asnKey;            // ASN to convert to receipt (optional)
    private String poKey;             // PO reference (optional)
    private String storerKey;
    private String facility;
    private String door;
    private String userId;

    // Workflow behavior options
    @Builder.Default
    private boolean autoStart = false;
    @Builder.Default
    private boolean autoCompleteOnTimeout = false;
    @Builder.Default
    private boolean autoTriggerPutaway = true;
    @Builder.Default
    private boolean autoApproveOverReceipts = false;
    @Builder.Default
    private int receivingTimeoutMinutes = 60;
}
