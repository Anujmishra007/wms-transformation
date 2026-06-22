package com.maersk.wms.outbound.shared.kernel.valueobjects;

import com.maersk.wms.outbound.shared.kernel.identifiers.LocationKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.LpnKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.PickDetailKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.SkuKey;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

/**
 * Value object representing a pick instruction for RDT device.
 */
@Value
@Builder
public class PickInstruction {
    PickDetailKey pickDetailKey;
    SkuKey sku;
    String skuDescription;
    LocationKey fromLocation;
    LpnKey fromLpn;
    LocationKey toLocation;
    LpnKey toLpn;
    BigDecimal qtyToPick;
    String uom;
    String packKey;
    String lot;
    int priority;
    String zone;
    String aisle;
}
