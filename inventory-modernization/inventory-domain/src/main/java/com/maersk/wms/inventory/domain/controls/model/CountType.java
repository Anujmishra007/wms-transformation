package com.maersk.wms.inventory.domain.controls.model;

import com.maersk.wms.inventory.shared.kernel.identifiers.*;

import lombok.*;
import java.time.Instant;
import java.util.List;

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
    private CountMode mode;

    // Configuration
    private boolean blindCount;          // Hide expected quantities
    private boolean requireRecount;      // Require recount on variance
    private boolean allowZeroCount;      // Allow counting to zero
    private boolean requireApproval;     // Variance requires approval

    // Variance Thresholds
    private double varianceThresholdPercent;
    private double varianceThresholdQuantity;
    private double varianceThresholdValue;

    // Recount Configuration
    private int maxRecountAttempts;
    private boolean differentUserRecount;  // Different user must recount

    // Scope Configuration
    @Builder.Default
    private List<String> allowedLocations = List.of();     // Empty = all locations
    @Builder.Default
    private List<String> excludedLocations = List.of();
    @Builder.Default
    private List<String> allowedStorers = List.of();       // Empty = all storers

    // Status
    private CountTypeStatus status;

    // Audit
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;

    public enum CountStrategy {
        PHYSICAL_COUNT,     // Full physical inventory
        CYCLE_COUNT,        // Regular cycle counting
        ABC_COUNT,          // ABC classification-based
        RANDOM_COUNT,       // Random sampling
        DIRECTED_COUNT,     // System-directed counting
        SPOT_COUNT,         // Ad-hoc spot checks
        VARIANCE_COUNT      // Count triggered by variance
    }

    public enum CountMode {
        NORMAL,             // Show expected quantities
        BLIND,              // Hide expected quantities
        GUIDED,             // Step-by-step guidance
        RFID,               // RFID-assisted counting
        BARCODE_SCAN        // Barcode scan counting
    }

    public enum CountTypeStatus {
        ACTIVE,
        INACTIVE,
        DRAFT
    }

    /**
     * Check if variance requires recount.
     */
    public boolean requiresRecount(double variancePercent, double varianceQty) {
        if (!requireRecount) return false;

        return variancePercent > varianceThresholdPercent
                || Math.abs(varianceQty) > varianceThresholdQuantity;
    }

    /**
     * Check if variance requires approval.
     */
    public boolean requiresApproval(double variancePercent, double varianceQty, double varianceValue) {
        if (!requireApproval) return false;

        return variancePercent > varianceThresholdPercent
                || Math.abs(varianceQty) > varianceThresholdQuantity
                || Math.abs(varianceValue) > varianceThresholdValue;
    }

    /**
     * Check if location is allowed for this count type.
     */
    public boolean isLocationAllowed(String location) {
        if (excludedLocations.contains(location)) return false;
        return allowedLocations.isEmpty() || allowedLocations.contains(location);
    }

    /**
     * Check if storer is allowed for this count type.
     */
    public boolean isStorerAllowed(String storer) {
        return allowedStorers.isEmpty() || allowedStorers.contains(storer);
    }
}
