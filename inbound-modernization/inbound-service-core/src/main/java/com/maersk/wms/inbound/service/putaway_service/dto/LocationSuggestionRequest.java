package com.maersk.wms.inbound.service.putaway_service.dto;

import com.maersk.wms.inbound.shared.kernel.identifiers.LpnKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.SkuKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request for location suggestion.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationSuggestionRequest {
    private LpnKey lpn;
    private SkuKey sku;
    private BigDecimal quantity;
    private String packKey;
    private String uom;

    private String strategyKey;
    private String preferredZone;

    // For returns
    private boolean isReturn;
    private String disposition;
    private String conditionCode;
}
