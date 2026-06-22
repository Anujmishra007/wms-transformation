package com.maersk.wms.inbound.workflow.returns;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Signal to assign disposition to a return line item.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignDispositionSignal {

    private String sku;

    // Disposition
    private String disposition;         // RESTOCK, REFURBISH, SCRAP, VENDOR_RETURN, DONATE, LIQUIDATE
    private String dispositionLocation;
    private String dispositionNotes;

    // For restock - target info
    private String targetLocation;
    private String targetZone;

    // For vendor return
    private String vendorKey;
    private String vendorReturnAuth;

    // For refurbish
    private String refurbishWorkOrder;

    // Override auto-disposition
    private boolean manualOverride;
    private String overrideReason;

    // User assigning disposition
    private String userId;

    // Approval info (if required)
    private boolean approved;
    private String approvedBy;
    private String approvalCode;
}
