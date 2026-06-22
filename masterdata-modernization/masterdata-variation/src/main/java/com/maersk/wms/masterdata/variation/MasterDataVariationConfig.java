package com.maersk.wms.masterdata.variation;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Configuration for client-specific master data variations.
 */
@Data
@Builder
public class MasterDataVariationConfig {

    private String clientCode;
    private String facilityCode;

    // Item settings
    private boolean autoSkuGenerationEnabled;
    private String skuFormat;
    private String defaultItemGroup;
    private String defaultItemClass;
    private boolean requirePhysicalDimensions;
    private boolean requireWeight;

    // Location settings
    private String locationCodeFormat;  // {ZONE}-{AISLE}-{BAY}-{LEVEL}-{POSITION}
    private boolean autoLocationCodeGeneration;
    private String defaultLocationType;
    private String defaultStorageType;
    private boolean requireLocationDimensions;

    // Customer settings
    private boolean addressValidationRequired;
    private String defaultCustomerType;
    private String defaultServiceLevel;

    // Carrier settings
    private boolean carrierApiRequired;
    private String defaultLabelFormat;

    // Custom parameters
    private Map<String, String> customParameters;

    public static MasterDataVariationConfig defaultConfig() {
        return MasterDataVariationConfig.builder()
                .autoSkuGenerationEnabled(false)
                .skuFormat("{PREFIX}-{SEQUENCE}")
                .requirePhysicalDimensions(false)
                .requireWeight(true)
                .locationCodeFormat("{ZONE}-{AISLE}-{BAY}-{LEVEL}-{POSITION}")
                .autoLocationCodeGeneration(true)
                .defaultLocationType("RESERVE")
                .defaultStorageType("STANDARD")
                .requireLocationDimensions(false)
                .addressValidationRequired(false)
                .defaultCustomerType("SHIPTO")
                .defaultServiceLevel("STANDARD")
                .carrierApiRequired(false)
                .defaultLabelFormat("ZPL")
                .build();
    }
}
