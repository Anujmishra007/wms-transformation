package com.maersk.wms.outbound.domain.shipping.dto;

import com.maersk.wms.outbound.domain.shipping.Carrier;
import com.maersk.wms.outbound.domain.shipping.CarrierService;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Result DTO for carrier selection operations.
 */
@Data
@Builder
public class CarrierSelectionResult {
    private boolean selected;
    private String carrierCode;
    private String carrierName;
    private String serviceCode;
    private String serviceName;
    private BigDecimal rate;
    private BigDecimal estimatedRate;
    private String currency;
    private String currencyCode;
    private String estimatedDeliveryDate;
    private int transitDays;
    private int estimatedDeliveryDays;
    private String reason;
    private String selectionReason;
    private String errorCode;
    private Carrier selectedCarrier;
    private CarrierService selectedService;
}
