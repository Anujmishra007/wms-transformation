package com.maersk.wms.masterdata.rules;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Facts for carrier validation rule evaluation.
 */
@Data
@Builder
public class CarrierValidationFacts {

    private String clientCode;
    private String facilityCode;
    private String operationType;

    // Carrier data
    private String carrierCode;
    private String carrierName;
    private String carrierType;
    private String scacCode;

    // Capabilities
    private boolean hazmatCertified;
    private boolean oversizedCapable;
    private boolean refrigeratedCapable;
    private BigDecimal maxWeight;

    // Integration
    private String apiEndpoint;
    private boolean trackingEnabled;

    // Configuration
    private Map<String, String> clientConfig;
}
