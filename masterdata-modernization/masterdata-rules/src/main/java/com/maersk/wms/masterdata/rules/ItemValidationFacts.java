package com.maersk.wms.masterdata.rules;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Facts for item validation rule evaluation.
 */
@Data
@Builder
public class ItemValidationFacts {

    private String clientCode;
    private String facilityCode;
    private String operationType;  // CREATE, UPDATE

    // Item data
    private String sku;
    private String description;
    private String itemType;
    private String itemGroup;
    private String itemClass;

    // Physical attributes
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;
    private BigDecimal weight;
    private BigDecimal cube;

    // Control flags
    private boolean lotControlled;
    private boolean serialControlled;
    private boolean expirationControlled;
    private int shelfLife;

    // Storage
    private String storageType;
    private String storageZone;

    // Hazmat
    private boolean hazmat;
    private String hazmatClass;

    // Catch weight
    private boolean catchWeight;
    private BigDecimal minWeight;
    private BigDecimal maxWeight;

    // Configuration
    private Map<String, String> clientConfig;
}
