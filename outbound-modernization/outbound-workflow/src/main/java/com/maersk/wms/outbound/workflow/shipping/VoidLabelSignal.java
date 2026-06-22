package com.maersk.wms.outbound.workflow.shipping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Signal to void a shipping label.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoidLabelSignal {

    private String labelKey;
    private String trackingNumber;
    private String reason;

    // Options
    private boolean regenerateLabel;
    private boolean refundShippingCost;

    // User performing the void
    private String userId;
}
