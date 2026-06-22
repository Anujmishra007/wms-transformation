package com.maersk.wms.inventory.domain.controls.model;

import com.maersk.wms.inventory.shared.kernel.identifiers.*;

import lombok.*;
import java.time.Instant;

/**
 * Entity representing inventory count type configuration.
 * Supports multiple counting strategies: Physical, Cycle, Blind, Directed, Spot, Recount.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountType {

    private CountKey countTypeKey;
    private WarehouseKey warehouseKey;

    // Identity
    private String countTypeCode;
    private String countTypeName;
    private String description;

    // Count Strategy
    private CountStrategy strategy;

    // Configuration flags
    private boolean active;
    private boolean blindCount;
    private boolean guidedCount;
    private boolean allowAddItems;
    private boolean requireRecount;
    private boolean requireApproval;
    @Builder.Default
    private boolean isDefault = false;

    // Recount Thresholds
    private double recountThresholdPercent;
    private double recountThresholdQty;
    private int maxRecounts;

    // Approval Thresholds
    private double approvalThresholdPercent;
    private double approvalThresholdQty;
    private double approvalThresholdValue;

    // Scope Filters (comma-separated or wildcard patterns)
    private String locationFilter;
    private String storerFilter;
    private String skuFilter;

    // Priority (lower = higher priority)
    private int priority;

    // Audit
    private Instant createdAt;
    private UserKey createdBy;
    private Instant updatedAt;
    private UserKey updatedBy;

    // Record-style accessor methods
    public CountKey countTypeKey() { return countTypeKey; }
    public WarehouseKey warehouseKey() { return warehouseKey; }
    public String countTypeCode() { return countTypeCode; }
    public String countTypeName() { return countTypeName; }
    public String description() { return description; }
    public CountStrategy strategy() { return strategy; }
    public boolean active() { return active; }
    public boolean blindCount() { return blindCount; }
    public boolean guidedCount() { return guidedCount; }
    public boolean allowAddItems() { return allowAddItems; }
    public boolean requireRecount() { return requireRecount; }
    public boolean requireApproval() { return requireApproval; }
    public boolean isDefault() { return isDefault; }
    public double recountThresholdPercent() { return recountThresholdPercent; }
    public double recountThresholdQty() { return recountThresholdQty; }
    public int maxRecounts() { return maxRecounts; }
    public double approvalThresholdPercent() { return approvalThresholdPercent; }
    public double approvalThresholdQty() { return approvalThresholdQty; }
    public double approvalThresholdValue() { return approvalThresholdValue; }
    public String locationFilter() { return locationFilter; }
    public String storerFilter() { return storerFilter; }
    public String skuFilter() { return skuFilter; }
    public int priority() { return priority; }
    public Instant createdAt() { return createdAt; }
    public UserKey createdBy() { return createdBy; }
    public Instant updatedAt() { return updatedAt; }
    public UserKey updatedBy() { return updatedBy; }

    // Getter-style methods for compatibility
    public CountKey getCountTypeKey() { return countTypeKey; }
    public WarehouseKey getWarehouseKey() { return warehouseKey; }
    public String getCountTypeCode() { return countTypeCode; }
    public String getDescription() { return description; }
    public CountStrategy getStrategy() { return strategy; }
    public boolean isActive() { return active; }
    public boolean isBlindCount() { return blindCount; }
    public boolean isRequireRecount() { return requireRecount; }
    public boolean isRequireApproval() { return requireApproval; }

    public enum CountStrategy {
        PHYSICAL,           // Full physical inventory
        CYCLE,              // Regular cycle counting
        ABC,                // ABC classification-based
        RANDOM,             // Random sampling
        DIRECTED,           // System-directed counting
        SPOT,               // Ad-hoc spot checks
        VARIANCE,           // Count triggered by variance
        PHYSICAL_COUNT,     // Full physical inventory (alias)
        CYCLE_COUNT,        // Regular cycle counting (alias)
        ABC_COUNT,          // ABC classification-based (alias)
        RANDOM_COUNT,       // Random sampling (alias)
        DIRECTED_COUNT,     // System-directed counting (alias)
        SPOT_COUNT,         // Ad-hoc spot checks (alias)
        VARIANCE_COUNT      // Count triggered by variance (alias)
    }

    /**
     * Check if variance requires recount.
     */
    public boolean requiresRecount(double variancePercent, double varianceQty) {
        if (!requireRecount) return false;
        return variancePercent > recountThresholdPercent
                || Math.abs(varianceQty) > recountThresholdQty;
    }

    /**
     * Check if variance requires approval.
     */
    public boolean requiresApproval(double variancePercent, double varianceQty, double varianceValue) {
        if (!requireApproval) return false;
        return variancePercent > approvalThresholdPercent
                || Math.abs(varianceQty) > approvalThresholdQty
                || Math.abs(varianceValue) > approvalThresholdValue;
    }

    /**
     * Check if location is allowed for this count type.
     */
    public boolean isLocationAllowed(String location) {
        if (locationFilter == null || locationFilter.isBlank()) return true;
        return matchesFilter(location, locationFilter);
    }

    /**
     * Check if storer is allowed for this count type.
     */
    public boolean isStorerAllowed(String storer) {
        if (storerFilter == null || storerFilter.isBlank()) return true;
        return matchesFilter(storer, storerFilter);
    }

    private boolean matchesFilter(String value, String filter) {
        if (filter.contains(",")) {
            String[] allowedValues = filter.split(",");
            for (String allowed : allowedValues) {
                if (allowed.trim().equalsIgnoreCase(value)) {
                    return true;
                }
            }
            return false;
        } else if (filter.contains("*")) {
            String regex = filter.replace("*", ".*");
            return value.matches("(?i)" + regex);
        } else {
            return filter.equalsIgnoreCase(value);
        }
    }
}
