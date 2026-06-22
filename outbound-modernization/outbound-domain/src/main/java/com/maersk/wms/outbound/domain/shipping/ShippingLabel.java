package com.maersk.wms.outbound.domain.shipping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Shipping Label entity.
 * Maps to label generation stored procedures.
 *
 * Legacy SP References:
 * - isp_PrintCarrierLabel
 * - rdtfnc_ReprintCarrierLabel
 * - isp_BT_Bartender_Shipper_Label_* (100+ client-specific variants)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingLabel {

    private String labelKey;
    private String cbolKey;
    private String mbolKey;
    private String orderKey;

    private String trackingNumber;
    private String carrierCode;
    private String serviceCode;

    private LabelFormat format;
    private LabelStatus status;

    private String labelContent;  // Base64 encoded
    private String labelUrl;
    private String labelZpl;      // ZPL format for thermal printers

    private String routingCode;
    private String sortCode;
    private String barcodeData;

    private LocalDateTime generatedAt;
    private LocalDateTime expiresAt;
    private LocalDateTime printedAt;
    private String printedBy;

    // Retry tracking
    private int printAttempts;
    private String lastError;

    private String addWho;
    private LocalDateTime addDate;

    /**
     * Check if label is valid for printing.
     */
    public boolean isValid() {
        return status == LabelStatus.GENERATED &&
               (expiresAt == null || expiresAt.isAfter(LocalDateTime.now()));
    }

    /**
     * Check if label has expired.
     */
    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }
}
