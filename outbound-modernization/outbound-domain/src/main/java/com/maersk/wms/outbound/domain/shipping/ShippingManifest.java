package com.maersk.wms.outbound.domain.shipping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Shipping Manifest entity - end of day carrier manifest.
 * Maps to manifest/load plan tables in the legacy system.
 *
 * Legacy SP References:
 * - nsp_ShippingManifestDetails
 * - nsp_GetLoadManifest
 * - nsp_LoadManifestSum*
 * - isp_shipping_manifest_by_load_*
 * - isp_CartonManifestLabel*
 * - rdtfnc_PrtPltManifest
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingManifest {

    private String manifestKey;
    private String manifestNumber;
    private String storerKey;
    private String carrierKey;
    private String carrierCode;
    private String carrierName;

    private ManifestStatus status;
    private ManifestType type;

    private String door;
    private String trailerNumber;
    private String sealNumber;
    private String driverName;
    private String driverLicense;

    private LocalDateTime manifestDate;
    private LocalDateTime closeDate;
    private LocalDateTime pickupScheduledTime;
    private LocalDateTime pickupActualTime;
    private LocalDateTime scheduledPickupDate;
    private String pickupConfirmation;

    private int totalShipments;
    private int totalPackages;
    private int totalPallets;
    private BigDecimal totalWeight;
    private String weightUom;
    private BigDecimal totalVolume;
    private String volumeUom;

    private String facility;
    private String accountNumber;
    private String manifestUrl;

    private String addWho;
    private LocalDateTime addDate;
    private String editWho;
    private LocalDateTime editDate;

    @Builder.Default
    private List<String> mbolKeys = new ArrayList<>();

    @Builder.Default
    private List<String> cbolKeys = new ArrayList<>();

    /**
     * Check if manifest can be closed.
     */
    public boolean canClose() {
        return status == ManifestStatus.OPEN && !mbolKeys.isEmpty();
    }

    /**
     * Check if manifest is open for adding shipments.
     */
    public boolean isOpen() {
        return status == ManifestStatus.OPEN;
    }

    /**
     * Get manifest type (alias for type field).
     */
    public ManifestType getManifestType() {
        return type;
    }

    /**
     * Set manifest type (alias for type field).
     */
    public void setManifestType(ManifestType manifestType) {
        this.type = manifestType;
    }
}
