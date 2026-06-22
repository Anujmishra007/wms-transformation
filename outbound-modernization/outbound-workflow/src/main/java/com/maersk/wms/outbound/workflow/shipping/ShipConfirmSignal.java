package com.maersk.wms.outbound.workflow.shipping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Signal to confirm shipment.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipConfirmSignal {

    // Tracking info (may be populated from carrier API)
    private String masterTrackingNumber;
    private String proNumber;

    // Trailer/seal info (for LTL/FTL)
    private String trailerNumber;
    private String sealNumber;
    private String driverName;
    private String driverLicense;

    // Actual ship date/time
    private LocalDateTime actualShipDateTime;

    // Weight verification
    private java.math.BigDecimal actualWeight;
    private boolean weightVerified;

    // Door info
    private String door;
    private String dock;

    // User performing the confirmation
    private String userId;

    // Options
    private boolean sendAsn;
    private boolean notifyCustomer;
}
