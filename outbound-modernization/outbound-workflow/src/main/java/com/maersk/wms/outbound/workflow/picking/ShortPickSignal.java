package com.maersk.wms.outbound.workflow.picking;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

/**
 * Signal to record a short pick.
 */
@Value
@Builder
public class ShortPickSignal {

    String pickDetailKey;
    BigDecimal expectedQty;
    BigDecimal actualQty;
    String reasonCode;
    String reasonDescription;
    String scannedLocation;
}
