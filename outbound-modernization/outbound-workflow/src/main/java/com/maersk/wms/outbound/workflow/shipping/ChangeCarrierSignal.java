package com.maersk.wms.outbound.workflow.shipping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Signal to change carrier for a shipment.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeCarrierSignal {

    private String newCarrierCode;
    private String newServiceCode;
    private String reason;

    // Re-rate options
    private boolean regenerateLabels;
    private boolean recalculateFreight;

    // User performing the change
    private String userId;
}
