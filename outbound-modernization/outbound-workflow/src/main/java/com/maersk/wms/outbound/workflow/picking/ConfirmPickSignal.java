package com.maersk.wms.outbound.workflow.picking;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

/**
 * Signal to confirm a pick.
 */
@Value
@Builder
public class ConfirmPickSignal {

    String pickDetailKey;
    BigDecimal qtyPicked;
    String scannedSku;
    String scannedLocation;
    String scannedLpn;
    String toLpn;
    String lot;
    String serialNumber;
}
