package com.maersk.wms.inventory.rules;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdjustmentApprovalResult {
    private boolean requiresApproval;
    private String approvalLevel;
    private String reason;
}
