package com.maersk.wms.outbound.rules;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Facts for shipping rule evaluation.
 */
@Data
@Builder
public class ShippingRuleFacts {

    private String clientCode;
    private String facilityCode;
    private String orderNumber;
    private String shipmentId;

    // Shipment details
    private String carrier;
    private String shipMethod;
    private String shipToCountry;
    private String shipToState;
    private String shipToZip;

    // Package details
    private List<PackageFact> packages;
    private BigDecimal totalWeight;
    private BigDecimal totalVolume;

    // Special handling
    private boolean hazmat;
    private boolean oversized;
    private boolean refrigerated;
    private boolean signature;
    private boolean insurance;

    // Configuration
    private Map<String, String> clientConfig;

    @Data
    @Builder
    public static class PackageFact {
        private String cartonId;
        private String cartonType;
        private BigDecimal weight;
        private BigDecimal length;
        private BigDecimal width;
        private BigDecimal height;
        private int itemCount;
    }
}
