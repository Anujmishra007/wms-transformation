package com.maersk.wms.outbound.shared.kernel.valueobjects;

import com.maersk.wms.outbound.shared.kernel.identifiers.LocationKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.LpnKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.SkuKey;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

/**
 * Value object representing an allocation line item.
 */
@Value
@Builder
public class AllocationLine {
    SkuKey sku;
    LpnKey lpn;
    LocationKey fromLocation;
    LocationKey toLocation;
    BigDecimal allocatedQty;
    String lot;
    String packKey;
    String uom;
}
