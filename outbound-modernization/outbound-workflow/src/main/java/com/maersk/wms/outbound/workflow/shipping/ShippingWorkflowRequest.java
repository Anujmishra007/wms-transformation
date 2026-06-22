package com.maersk.wms.outbound.workflow.shipping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Request object for shipping workflow.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingWorkflowRequest {

    // Context
    private String clientCode;
    private String facilityCode;
    private String userId;

    // Source - one of these should be provided
    private String waveKey;
    private String loadKey;
    private String mbolKey;  // If MBOL already exists
    private List<String> orderKeys;

    // Storer/Customer
    private String storerKey;
    private String consigneeKey;

    // Ship-to address (override if needed)
    private String shipToName;
    private String shipToAddress1;
    private String shipToAddress2;
    private String shipToCity;
    private String shipToState;
    private String shipToZip;
    private String shipToCountry;
    private String shipToPhone;
    private String shipToEmail;

    // Carrier preferences
    private String preferredCarrierCode;
    private String preferredServiceCode;
    private boolean rateShopEnabled;
    private BigDecimal maxShippingCost;

    // Shipping options
    private String shipmentType;  // PARCEL, LTL, FTL, AIR, OCEAN
    private boolean signatureRequired;
    private boolean saturdayDelivery;
    private boolean residentialDelivery;
    private boolean holdAtLocation;
    private String holdAtLocationAddress;

    // Special services
    private boolean codRequired;
    private BigDecimal codAmount;
    private boolean insuranceRequired;
    private BigDecimal declaredValue;

    // Timing
    private LocalDateTime requestedShipDate;
    private LocalDateTime requestedDeliveryDate;
    private String deliveryWindow;  // AM, PM, EVENING

    // Label options
    private String labelFormat;  // PDF, ZPL, EPL, PNG
    private boolean autoGenerateLabels;
    private int labelCopies;

    // Manifest options
    private boolean autoAddToManifest;
    private boolean autoCloseManifest;
    private String manifestKey;  // If adding to existing manifest

    // Workflow options
    private boolean autoSelectCarrier;
    private boolean autoShip;
    private boolean autoSchedulePickup;
    private boolean waitForManifestClose;

    // Package info (if known)
    private List<PackageInfo> packages;

    // Reference numbers
    private String poNumber;
    private String referenceNumber1;
    private String referenceNumber2;

    // Notes
    private String shippingInstructions;
    private String specialInstructions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PackageInfo {
        private String packageId;
        private String packageType;  // BOX, ENVELOPE, PALLET
        private BigDecimal weight;
        private String weightUom;
        private BigDecimal length;
        private BigDecimal width;
        private BigDecimal height;
        private String dimensionUom;
        private BigDecimal declaredValue;
        private String contents;
    }
}
