package com.maersk.wms.inbound.service.putaway_service.dto;

import com.maersk.wms.inbound.shared.kernel.identifiers.LocationKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.LpnKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.ReceiptKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.SkuKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request to create a putaway task.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePutawayTaskRequest {
    private ReceiptKey receiptKey;
    private String receiptDetailKey;
    private LpnKey lpn;
    private SkuKey sku;
    private String packKey;
    private BigDecimal quantity;
    private String uom;

    private LocationKey fromLocation;
    private LocationKey toLocation;  // If null, will be determined by strategy

    private String strategyKey;
    private String fromZone;

    @Builder.Default
    private int priority = 5;
    private boolean urgent;
    private boolean crossDock;

    // For returns
    private boolean isReturn;
    private String disposition;

    private String userId;
}
