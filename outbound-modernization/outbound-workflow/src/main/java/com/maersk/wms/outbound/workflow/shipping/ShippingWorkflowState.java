package com.maersk.wms.outbound.workflow.shipping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Internal state object for shipping workflow.
 * Maintains the current state of the workflow for query methods.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingWorkflowState {

    private String mbolKey;
    private String manifestKey;
    private ShippingWorkflowStatus status;
    private String statusMessage;

    // Phase tracking
    private boolean mbolCreated;
    private boolean carrierSelected;
    private boolean labelsGenerated;
    private boolean manifested;
    private boolean manifestClosed;
    private boolean manifestTransmitted;
    private boolean shipped;
    private boolean pickupScheduled;
    private boolean cancelled;

    // Timestamps
    private LocalDateTime workflowStartTime;
    private LocalDateTime mbolCreatedTime;
    private LocalDateTime carrierSelectedTime;
    private LocalDateTime labelsGeneratedTime;
    private LocalDateTime manifestedTime;
    private LocalDateTime manifestClosedTime;
    private LocalDateTime manifestTransmittedTime;
    private LocalDateTime shippedTime;
    private LocalDateTime pickupScheduledTime;

    // Carrier info
    private String carrierCode;
    private String carrierName;
    private String serviceCode;
    private String serviceName;
    private String masterTrackingNumber;

    // Totals
    private int totalOrders;
    private int totalPackages;
    private BigDecimal totalWeight;
    private BigDecimal freightCharge;
    private BigDecimal totalShippingCost;

    // Packages/Labels
    @Builder.Default
    private List<PackageState> packages = new ArrayList<>();

    // Manifest info
    private String manifestNumber;
    private String manifestTransmissionId;

    // Pickup info
    private String pickupConfirmationNumber;
    private LocalDateTime scheduledPickupTime;

    // Errors
    @Builder.Default
    private List<String> errors = new ArrayList<>();

    @Builder.Default
    private List<String> warnings = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PackageState {
        private String cbolKey;
        private String trackingNumber;
        private String packageType;
        private BigDecimal weight;
        private BigDecimal freightCharge;
        private boolean labelGenerated;
        private boolean labelPrinted;
        private String labelKey;
        private String labelUrl;
    }

    /**
     * Get count of packages with labels.
     */
    public int getLabeledPackageCount() {
        return (int) packages.stream().filter(PackageState::isLabelGenerated).count();
    }

    /**
     * Check if all packages have labels.
     */
    public boolean allPackagesLabeled() {
        return !packages.isEmpty() && packages.stream().allMatch(PackageState::isLabelGenerated);
    }

    /**
     * Check if ready to ship.
     */
    public boolean isReadyToShip() {
        return mbolCreated && carrierSelected && allPackagesLabeled();
    }

    /**
     * Add or update a package state.
     */
    public void updatePackage(PackageState packageState) {
        packages.removeIf(p -> p.getCbolKey().equals(packageState.getCbolKey()));
        packages.add(packageState);
    }

    /**
     * Find package state by CBOL key.
     */
    public PackageState findPackage(String cbolKey) {
        return packages.stream()
                .filter(p -> p.getCbolKey().equals(cbolKey))
                .findFirst()
                .orElse(null);
    }

    /**
     * Get progress percentage.
     */
    public int getProgressPercent() {
        return status != null ? status.getProgressPercent() : 0;
    }
}
