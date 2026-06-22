package com.maersk.wms.inventory.rules;

import com.maersk.wms.inventory.domain.InventoryAdjustment;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdjustmentApprovalFacts {
    private InventoryAdjustment adjustment;
    private AdjustmentApprovalResult result;
}
