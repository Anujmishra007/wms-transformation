package com.maersk.wms.masterdata.domain;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Carrier service level entity.
 */
@Data
public class CarrierService {

    private Long id;
    private String carrierCode;
    private String serviceCode;
    private String serviceName;
    private String serviceDescription;

    // Transit time
    private int transitDays;
    private int transitDaysMin;
    private int transitDaysMax;
    private boolean guaranteedDelivery;

    // Service characteristics
    private boolean residentialDelivery;
    private boolean signatureRequired;
    private boolean saturdayDelivery;
    private boolean sundayDelivery;
    private boolean hazmatAllowed;

    // Dimensions / Weight limits
    private BigDecimal maxWeight;
    private BigDecimal maxLength;
    private BigDecimal maxWidth;
    private BigDecimal maxHeight;
    private BigDecimal maxGirth;

    // Priority
    private int priority;
    private boolean defaultService;
    private boolean active;
}
