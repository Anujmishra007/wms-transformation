package com.maersk.wms.outbound.service.shipping;

import com.maersk.wms.outbound.domain.shipping.Carrier;
import com.maersk.wms.outbound.domain.shipping.CarrierService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Result of carrier selection process.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarrierSelectionResult {

    // Selection status
    private boolean selected;
    private String reason;

    // Selected carrier details
    private Carrier selectedCarrier;
    private CarrierService selectedService;
    private String carrierCode;
    private String carrierName;
    private String serviceCode;
    private String serviceName;
    private String selectionReason;

    // Rate information
    private BigDecimal estimatedRate;
    private BigDecimal estimatedCost;
    private BigDecimal fuelSurcharge;
    private BigDecimal totalCost;
    private String currency;

    // Transit information
    private Integer estimatedTransitDays;
    private String estimatedDeliveryDays;
    private LocalDate estimatedDeliveryDate;

    // Selection metadata
    private boolean rateShopPerformed;
    private int carriersEvaluated;
    private String rateSource;  // API, CACHED, DEFAULT

    /**
     * Helper method to check if selection was successful.
     */
    public boolean isSelected() {
        return selected || selectedCarrier != null;
    }
}
