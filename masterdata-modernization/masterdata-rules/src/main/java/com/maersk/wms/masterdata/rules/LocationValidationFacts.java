package com.maersk.wms.masterdata.rules;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Facts for location validation rule evaluation.
 */
@Data
@Builder
public class LocationValidationFacts {

    private String clientCode;
    private String facilityCode;
    private String operationType;

    // Location data
    private String locationCode;
    private String locationType;
    private String zone;
    private String aisle;
    private String bay;
    private String level;
    private String position;

    // Dimensions
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;
    private BigDecimal maxWeight;
    private BigDecimal maxCube;

    // Capacity
    private int maxPallets;
    private int maxCases;
    private int maxEaches;

    // Flags
    private boolean pickLocation;
    private boolean putawayLocation;
    private boolean mixedSku;
    private boolean mixedLot;

    // Configuration
    private Map<String, String> clientConfig;
}
