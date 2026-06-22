package com.maersk.wms.outbound.rules;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Result from wave planning rule evaluation.
 */
@Data
public class WaveRuleResult {

    private boolean waveAllowed = true;
    private List<WaveGroup> waveGroups = new ArrayList<>();
    private List<String> excludedOrders = new ArrayList<>();
    private List<String> validationErrors = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();

    public void addWaveGroup(WaveGroup group) {
        this.waveGroups.add(group);
    }

    public void excludeOrder(String orderNumber, String reason) {
        this.excludedOrders.add(orderNumber + ": " + reason);
    }

    public void addError(String error) {
        this.validationErrors.add(error);
        this.waveAllowed = false;
    }

    public void addWarning(String warning) {
        this.warnings.add(warning);
    }

    public boolean hasErrors() {
        return !validationErrors.isEmpty();
    }

    @Data
    @lombok.Builder
    public static class WaveGroup {
        private String groupKey;
        private String waveType;
        private String carrier;
        private String shipMethod;
        private int priority;
        private List<String> orderNumbers;
        private int totalLines;
        private int totalUnits;
        private String reason;
    }
}
