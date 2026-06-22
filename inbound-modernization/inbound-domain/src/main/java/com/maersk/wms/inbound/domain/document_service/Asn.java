package com.maersk.wms.inbound.domain.document_service;

import com.maersk.wms.inbound.shared.kernel.identifiers.AsnKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.StorerKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Advance Ship Notice (ASN) entity for Document subdomain (inbound-service).
 *
 * ASN provides advance notification of incoming shipments,
 * enabling planned receiving and dock scheduling.
 *
 * Legacy Table: ASN
 * Legacy SPs: nsp_CreateASN, nsp_UpdateASN, nsp_GetASN, nsp_ProcessASN
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Asn {

    private String asnKey;
    private String externalAsnKey;

    private AsnType type;
    private AsnStatus status;

    // Owner
    private StorerKey storerKey;

    // Vendor/Supplier
    private String vendorKey;
    private String vendorName;

    // Carrier/Shipping
    private String carrierKey;
    private String carrierName;
    private String trailerNumber;
    private String sealNumber;
    private String billOfLading;
    private String proNumber;
    private String scac;  // Standard Carrier Alpha Code

    // Schedule
    private LocalDateTime expectedArrivalDate;
    private LocalDateTime actualArrivalDate;
    private String appointmentNumber;
    private String door;

    // Ship from
    private String shipFromName;
    private String shipFromAddress;
    private String shipFromCity;
    private String shipFromState;
    private String shipFromZip;
    private String shipFromCountry;

    // Linked documents
    private String poKey;
    private String receiptKey;  // Populated when converted to receipt

    // Totals
    private int totalLines;
    private int totalCartons;
    private int totalPallets;
    private BigDecimal totalWeight;
    private String weightUom;
    private BigDecimal totalVolume;
    private String volumeUom;

    // Notes
    private String notes;
    private String specialInstructions;

    // EDI info
    private String ediTransactionId;
    private LocalDateTime ediReceivedDate;

    // Audit
    private String addWho;
    private LocalDateTime addDate;
    private String editWho;
    private LocalDateTime editDate;

    // Details
    @Builder.Default
    private List<AsnDetail> details = new ArrayList<>();

    /**
     * Check if ASN can be received.
     */
    public boolean canReceive() {
        return status == AsnStatus.OPEN || status == AsnStatus.ARRIVED;
    }

    /**
     * Check if ASN is linked to a receipt.
     */
    public boolean hasReceipt() {
        return receiptKey != null && !receiptKey.isBlank();
    }

    /**
     * Check if ASN has arrived.
     */
    public boolean hasArrived() {
        return actualArrivalDate != null;
    }

    /**
     * Add a detail line.
     */
    public void addDetail(AsnDetail detail) {
        if (details == null) {
            details = new ArrayList<>();
        }
        details.add(detail);
        detail.setAsn(this);
        totalLines = details.size();
    }
}
