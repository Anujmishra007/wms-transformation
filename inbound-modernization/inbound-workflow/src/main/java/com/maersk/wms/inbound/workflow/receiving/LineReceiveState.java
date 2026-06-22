package com.maersk.wms.inbound.workflow.receiving;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Tracks state of individual line during receiving.
 */
@Data
public class LineReceiveState {
    private final String lineNumber;
    private BigDecimal expectedQty = BigDecimal.ZERO;
    private BigDecimal receivedQty = BigDecimal.ZERO;
    private BigDecimal damagedQty = BigDecimal.ZERO;
    private String status;
    private boolean overReceived;

    public LineReceiveState(String lineNumber) {
        this.lineNumber = lineNumber;
    }

    public void updateOverReceiveStatus() {
        this.overReceived = receivedQty.compareTo(expectedQty) > 0;
    }
}
