package com.maersk.wms.masterdata.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Carrier master data entity.
 * Maps to the CARRIER table in the WMS database.
 */
@Data
public class Carrier {

    private Long id;
    private String carrierCode;
    private String carrierName;
    private CarrierType carrierType;
    private CarrierStatus status;

    // Contact
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private String contactFax;

    // Address
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String postalCode;
    private String country;

    // SCAC / DOT
    private String scacCode;
    private String dotNumber;
    private String mcNumber;

    // Integration
    private String accountNumber;
    private String apiEndpoint;
    private String apiKey;
    private boolean trackingEnabled;
    private String trackingUrlTemplate;
    private String labelFormat;
    private String manifestFormat;

    // Rating
    private boolean rateShopEnabled;
    private String ratingMethod;
    private BigDecimal markupPercent;
    private BigDecimal minimumCharge;

    // Service levels
    private List<CarrierService> services;

    // Cutoff times
    private String defaultCutoffTime;
    private String saturdayCutoffTime;

    // Restrictions
    private boolean hazmatCertified;
    private boolean oversizedCapable;
    private boolean refrigeratedCapable;
    private BigDecimal maxWeight;
    private String maxWeightUom;

    // Custom
    private String customField01;
    private String customField02;

    // Audit
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
