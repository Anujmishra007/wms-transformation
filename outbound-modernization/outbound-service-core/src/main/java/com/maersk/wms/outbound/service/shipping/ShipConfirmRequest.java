package com.maersk.wms.outbound.service.shipping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Request for ship confirmation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipConfirmRequest {

    private String trackingNumber;
    private String proNumber;
    private String trailerNumber;
    private String sealNumber;
    private String driverName;
    private String driverLicense;

    private LocalDateTime actualShipDate;
    private LocalDateTime estimatedDeliveryDate;

    private BigDecimal actualWeight;
    private BigDecimal freightCharge;
    private BigDecimal fuelSurcharge;
    private BigDecimal accessorialCharges;
    private BigDecimal totalCharge;
    private String currency;

    private String carrierReference;
    private String notes;
}
