package com.maersk.wms.outbound.workflow.shipping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Result object for shipping workflow.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingWorkflowResult {

    private boolean success;
    private String mbolKey;
    private String manifestKey;
    private String status;
    private String message;

    // Carrier info
    private String carrierCode;
    private String carrierName;
    private String serviceCode;
    private String serviceName;
    private String masterTrackingNumber;

    // Shipping totals
    private int totalOrders;
    private int totalPackages;
    private int totalCartons;
    private int totalPallets;
    private BigDecimal totalWeight;
    private String weightUom;

    // Financial
    private BigDecimal freightCharge;
    private BigDecimal fuelSurcharge;
    private BigDecimal accessorialCharges;
    private BigDecimal totalShippingCost;
    private String currency;

    // Timing
    private LocalDateTime workflowStartTime;
    private LocalDateTime mbolCreatedTime;
    private LocalDateTime labelsGeneratedTime;
    private LocalDateTime manifestedTime;
    private LocalDateTime shippedTime;
    private LocalDateTime workflowEndTime;
    private LocalDateTime estimatedDeliveryDate;
    private int transitDays;

    // Labels generated
    private List<LabelResult> labels;

    // Manifest info
    private String manifestNumber;
    private String manifestStatus;
    private String manifestTransmissionId;
    private LocalDateTime manifestTransmittedTime;

    // Pickup info
    private String pickupConfirmationNumber;
    private LocalDateTime scheduledPickupTime;
    private String driverName;
    private String driverPhone;

    // Errors and warnings
    private List<String> errors;
    private List<String> warnings;

    // Package-level results
    private List<PackageResult> packageResults;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LabelResult {
        private String labelKey;
        private String cbolKey;
        private String trackingNumber;
        private String format;
        private String labelUrl;
        private boolean printed;
        private LocalDateTime generatedTime;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PackageResult {
        private String cbolKey;
        private String trackingNumber;
        private String packageType;
        private BigDecimal weight;
        private BigDecimal freightCharge;
        private String labelKey;
        private String status;
    }
}
