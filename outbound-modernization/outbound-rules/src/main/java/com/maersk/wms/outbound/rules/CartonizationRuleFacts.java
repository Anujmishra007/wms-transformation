package com.maersk.wms.outbound.rules;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Facts for cartonization rule evaluation.
 */
@Data
@Builder
public class CartonizationRuleFacts {

    private String clientCode;
    private String facilityCode;
    private String orderNumber;

    // Items to pack
    private List<PackItem> items;

    // Available carton types
    private List<CartonType> availableCartons;

    // Constraints
    private BigDecimal maxCartonWeight;
    private boolean giftWrap;
    private boolean fragile;
    private String packingInstructions;

    // Configuration
    private Map<String, String> clientConfig;

    @Data
    @Builder
    public static class PackItem {
        private String sku;
        private String description;
        private BigDecimal quantity;
        private BigDecimal unitWeight;
        private BigDecimal unitLength;
        private BigDecimal unitWidth;
        private BigDecimal unitHeight;
        private boolean fragile;
        private boolean hazmat;
        private String packGroup;
    }

    @Data
    @Builder
    public static class CartonType {
        private String cartonCode;
        private String description;
        private BigDecimal maxWeight;
        private BigDecimal innerLength;
        private BigDecimal innerWidth;
        private BigDecimal innerHeight;
        private BigDecimal tareWeight;
        private int priority;
    }
}
