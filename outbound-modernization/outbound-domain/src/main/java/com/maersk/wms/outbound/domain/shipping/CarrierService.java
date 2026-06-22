package com.maersk.wms.outbound.domain.shipping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Carrier Service entity representing available shipping services.
 *
 * Legacy SP References:
 * - isp_ConnectCarrierService
 * - isp_UpdateCarrierService
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarrierService {

    private String serviceKey;
    private String carrierKey;
    private String serviceCode;
    private String serviceName;
    private String serviceDescription;

    private ServiceType serviceType;
    private boolean active;

    // Transit time in days
    private Integer transitDays;
    private Integer transitDaysMin;
    private Integer transitDaysMax;

    // Cutoff time for same-day pickup
    private String cutoffTime;

    // Weight and dimension limits
    private BigDecimal maxWeight;
    private BigDecimal maxLength;
    private BigDecimal maxWidth;
    private BigDecimal maxHeight;

    // Service features
    private boolean saturdayDelivery;
    private boolean signatureRequired;
    private boolean hazmatAllowed;
    private boolean internationalAllowed;
    private boolean residentialDelivery;

    // Default pricing
    private BigDecimal baseRate;
    private BigDecimal fuelSurchargePercent;

    private String addWho;
    private LocalDateTime addDate;
    private String editWho;
    private LocalDateTime editDate;

    public boolean isActive() {
        return active;
    }
}
