package com.maersk.wms.outbound.rules;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Result from cartonization rule evaluation.
 */
@Data
public class CartonizationRuleResult {

    private boolean cartonizationAllowed = true;
    private List<CartonPlan> cartonPlans = new ArrayList<>();
    private List<String> validationErrors = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();
    private int totalCartons = 0;
    private BigDecimal totalWeight = BigDecimal.ZERO;

    public void addCartonPlan(CartonPlan plan) {
        this.cartonPlans.add(plan);
        this.totalCartons++;
        this.totalWeight = this.totalWeight.add(plan.getTotalWeight());
    }

    public void addError(String error) {
        this.validationErrors.add(error);
        this.cartonizationAllowed = false;
    }

    public void addWarning(String warning) {
        this.warnings.add(warning);
    }

    public boolean hasErrors() {
        return !validationErrors.isEmpty();
    }

    @Data
    @lombok.Builder
    public static class CartonPlan {
        private String cartonType;
        private List<PackedItem> items;
        private BigDecimal totalWeight;
        private BigDecimal fillPercentage;
        private boolean requiresSpecialHandling;
        private String packingInstructions;
    }

    @Data
    @lombok.Builder
    public static class PackedItem {
        private String sku;
        private BigDecimal quantity;
        private BigDecimal weight;
    }
}
