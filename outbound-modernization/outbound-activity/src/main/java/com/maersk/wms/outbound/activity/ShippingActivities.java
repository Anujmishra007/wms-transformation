package com.maersk.wms.outbound.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Temporal activity interface for shipping operations.
 *
 * Legacy SP References:
 * - WM.lsp_WaveGenMBOL - Generate MBOL from wave
 * - nsp_BackEndShipped - Ship confirmation
 * - isp_PrintCarrierLabel - Label generation
 * - nsp_ShippingManifestDetails - Manifest operations
 */
@ActivityInterface
public interface ShippingActivities {

    // ========== MBOL Operations ==========

    @ActivityMethod
    MbolResult generateMbolFromWave(String waveKey, String clientCode, String facilityCode);

    @ActivityMethod
    MbolResult populateMbolFromLoadPlan(String loadKey, String clientCode, String facilityCode);

    @ActivityMethod
    MbolValidation validateMbol(String mbolKey, String clientCode, String facilityCode);

    @ActivityMethod
    MbolResult shipMbol(String mbolKey, ShipConfirmInput input, String clientCode, String facilityCode);

    @ActivityMethod
    MbolResult moveOrderToNewMbol(String orderKey, String sourceMbolKey, String clientCode, String facilityCode);

    // ========== Label Operations ==========

    @ActivityMethod
    LabelResult generateLabel(LabelInput labelInput, String clientCode, String facilityCode);

    @ActivityMethod
    LabelResult generateLabelsForMbol(String mbolKey, String format, String clientCode, String facilityCode);

    @ActivityMethod
    LabelResult reprintLabel(String labelKey, String clientCode, String facilityCode);

    @ActivityMethod
    void voidLabel(String labelKey, String reason, String clientCode, String facilityCode);

    // ========== Manifest Operations ==========

    @ActivityMethod
    ManifestResult createManifest(String carrierCode, String manifestType, String clientCode, String facilityCode);

    @ActivityMethod
    ManifestResult addMbolToManifest(String manifestKey, String mbolKey, String clientCode, String facilityCode);

    @ActivityMethod
    ManifestResult closeManifest(String manifestKey, String clientCode, String facilityCode);

    @ActivityMethod
    ManifestResult transmitManifest(String manifestKey, String clientCode, String facilityCode);

    @ActivityMethod
    PickupResult schedulePickup(String manifestKey, PickupInput input, String clientCode, String facilityCode);

    @ActivityMethod
    void cancelPickup(String confirmationNumber, String clientCode, String facilityCode);

    // ========== Carrier Operations ==========

    @ActivityMethod
    CarrierResult selectCarrier(String mbolKey, String clientCode, String facilityCode);

    @ActivityMethod
    CarrierResult changeCarrier(String mbolKey, String newCarrierCode, String newServiceCode,
                                String clientCode, String facilityCode);

    @ActivityMethod
    FreightResult calculateFreight(String mbolKey, String clientCode, String facilityCode);

    // ========== Legacy Shipment Operations (backward compatibility) ==========

    @ActivityMethod
    ShipResult shipOrder(String orderNumber, List<String> cartonIds,
                         String clientCode, String facilityCode);

    @ActivityMethod
    ShipResult createShipment(String orderNumber, String carrier, String shipMethod,
                              String clientCode, String facilityCode);

    @ActivityMethod
    ShipResult addCartonToShipment(String shipmentId, String cartonId,
                                   String clientCode, String facilityCode);

    @ActivityMethod
    ShipResult confirmShipment(String shipmentId, String clientCode, String facilityCode);

    @ActivityMethod
    void cancelShipment(String shipmentId, String clientCode, String facilityCode);

    // ========== Input DTOs ==========

    @Data
    @Builder
    class ShipConfirmInput {
        private String trackingNumber;
        private String proNumber;
        private String trailerNumber;
        private String sealNumber;
        private LocalDateTime actualShipDate;
    }

    @Data
    @Builder
    class LabelInput {
        private String cbolKey;
        private String mbolKey;
        private String carrierCode;
        private String serviceCode;
        private String format;
        private String shipToName;
        private String shipToAddress1;
        private String shipToAddress2;
        private String shipToCity;
        private String shipToState;
        private String shipToZip;
        private String shipToCountry;
        private BigDecimal weight;
        private BigDecimal length;
        private BigDecimal width;
        private BigDecimal height;
    }

    @Data
    @Builder
    class PickupInput {
        private String pickupDate;
        private String readyTime;
        private String closeTime;
        private String pickupLocation;
        private String contactName;
        private String contactPhone;
        private int totalPackages;
        private int totalWeight;
        private String weightUom;
        private boolean residentialPickup;
        private String specialInstructions;
    }

    // ========== Result DTOs ==========

    @Data
    @Builder
    class MbolResult {
        private boolean success;
        private String mbolKey;
        private String waveKey;
        private String loadKey;
        private String carrierCode;
        private String trackingNumber;
        private String status;
        private int totalOrders;
        private int totalCartons;
        private BigDecimal totalWeight;
        private List<String> errors;
    }

    @Data
    @Builder
    class MbolValidation {
        private boolean valid;
        private String mbolKey;
        private List<String> errors;
        private List<String> warnings;
    }

    @Data
    @Builder
    class ShipResult {
        private boolean success;
        private String shipmentId;
        private String trackingNumber;
        private String carrier;
        private String shipMethod;
        private int cartonCount;
        private BigDecimal totalWeight;
        private BigDecimal freightCharge;
        private List<String> errors;
    }

    @Data
    @Builder
    class ManifestResult {
        private boolean success;
        private String manifestKey;
        private String manifestType;
        private String carrierCode;
        private String status;
        private String transmissionId;
        private LocalDateTime transmittedAt;
        private int totalMbols;
        private int totalPackages;
        private List<String> errors;
    }

    @Data
    @Builder
    class LabelResult {
        private boolean success;
        private String labelKey;
        private String trackingNumber;
        private String format;
        private byte[] labelContent;
        private List<String> labelUrls;
        private List<String> errors;
    }

    @Data
    @Builder
    class PickupResult {
        private boolean success;
        private String confirmationNumber;
        private LocalDateTime scheduledPickupTime;
        private String driverName;
        private String driverPhone;
        private List<String> errors;
    }

    @Data
    @Builder
    class CarrierResult {
        private boolean success;
        private String carrierCode;
        private String carrierName;
        private String serviceCode;
        private String serviceName;
        private BigDecimal estimatedRate;
        private String estimatedDeliveryDays;
        private List<String> errors;
    }

    @Data
    @Builder
    class FreightResult {
        private boolean success;
        private BigDecimal freightCharge;
        private String currency;
        private String rateCode;
        private BigDecimal fuelSurcharge;
        private BigDecimal accessorialCharges;
        private List<String> errors;
    }
}
