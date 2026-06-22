package com.maersk.wms.masterdata.rules;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Facts for customer validation rule evaluation.
 */
@Data
@Builder
public class CustomerValidationFacts {

    private String clientCode;
    private String facilityCode;
    private String operationType;

    // Customer data
    private String customerCode;
    private String customerName;
    private String customerType;

    // Address
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String postalCode;
    private String country;

    // Contact
    private String contactEmail;
    private String contactPhone;

    // Compliance
    private boolean requiresRoutingGuide;
    private boolean requiresUccLabels;
    private boolean requiresAsn;

    // Configuration
    private Map<String, String> clientConfig;
}
