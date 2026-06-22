package com.maersk.wms.picking.shared.kernel.valueobjects;

import com.maersk.wms.picking.shared.kernel.identifiers.*;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Value object representing a pick confirmation from RDT device.
 */
@Value
@Builder
public class PickConfirmation {
    PickTaskKey pickTaskKey;
    UserKey userId;
    DeviceKey deviceId;

    // Scanned values
    String scannedLocation;
    String scannedLpn;
    String scannedSku;
    String scannedLot;
    String scannedSerial;

    // Quantities
    BigDecimal qtyPicked;
    BigDecimal qtyShorted;
    BigDecimal capturedWeight;

    // Destination
    String toLpn;
    String dropLocation;

    // Timing
    LocalDateTime confirmTime;
    long pickDurationMs;

    // Validation
    boolean locationVerified;
    boolean skuVerified;
    boolean lotVerified;

    public boolean isComplete() {
        return qtyShorted.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isShort() {
        return qtyShorted.compareTo(BigDecimal.ZERO) > 0;
    }
}
