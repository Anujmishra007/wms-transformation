package com.maersk.wms.outbound.rules;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Result from allocation rule evaluation.
 */
@Data
public class AllocationRuleResult {

    private boolean allocationAllowed = true;
    private String allocationStrategy = "FIFO";
    private List<AllocationDecision> allocations = new ArrayList<>();
    private List<String> validationErrors = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();
    private BigDecimal totalAllocated = BigDecimal.ZERO;
    private BigDecimal shortQty = BigDecimal.ZERO;

    public void addAllocation(AllocationDecision allocation) {
        this.allocations.add(allocation);
        this.totalAllocated = this.totalAllocated.add(allocation.getQuantity());
    }

    public void addError(String error) {
        this.validationErrors.add(error);
        this.allocationAllowed = false;
    }

    public void addWarning(String warning) {
        this.warnings.add(warning);
    }

    public boolean hasErrors() {
        return !validationErrors.isEmpty();
    }

    @Data
    @lombok.Builder
    public static class AllocationDecision {
        private String location;
        private String lpn;
        private String lot;
        private BigDecimal quantity;
        private int priority;
        private String reason;
    }
}
