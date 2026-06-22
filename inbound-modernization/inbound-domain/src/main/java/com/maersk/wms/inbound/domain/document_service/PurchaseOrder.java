package com.maersk.wms.inbound.domain.document_service;

import com.maersk.wms.inbound.shared.kernel.identifiers.PoKey;
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
 * Purchase Order entity for the Document subdomain (inbound-service).
 *
 * PO represents the expected inbound shipment from a vendor.
 * This is a planning/document entity - actual receiving happens in operations subdomain.
 *
 * Legacy Table: PO
 * Legacy SPs: nsp_CreatePO, nsp_UpdatePO, nsp_GetPO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrder {

    private String poKey;
    private String externalPoKey;

    private PoType type;
    private PoStatus status;

    // Owner
    private StorerKey storerKey;

    // Vendor
    private String vendorKey;
    private String vendorName;
    private String vendorContact;

    // Schedule
    private LocalDateTime orderDate;
    private LocalDateTime expectedDate;
    private LocalDateTime cancelDate;  // Cancel if not received by

    // Buyer info
    private String buyerKey;
    private String buyerName;

    // Totals
    private int totalLines;
    private BigDecimal totalExpectedQty;
    private BigDecimal totalReceivedQty;
    private BigDecimal totalValue;
    private String currency;

    // Linked documents
    private String contractKey;
    private String blanketPoKey;  // For releases against blanket PO

    // Notes
    private String notes;
    private String internalNotes;
    private String vendorNotes;

    // Audit
    private String addWho;
    private LocalDateTime addDate;
    private String editWho;
    private LocalDateTime editDate;

    // Details
    @Builder.Default
    private List<PurchaseOrderDetail> details = new ArrayList<>();

    /**
     * Check if PO can be received.
     */
    public boolean canReceive() {
        return status == PoStatus.OPEN || status == PoStatus.PARTIAL;
    }

    /**
     * Check if PO is fully received.
     */
    public boolean isFullyReceived() {
        return status == PoStatus.RECEIVED || status == PoStatus.CLOSED;
    }

    /**
     * Calculate receipt percentage.
     */
    public double getReceiptPercentage() {
        if (totalExpectedQty == null || totalExpectedQty.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }
        BigDecimal received = totalReceivedQty != null ? totalReceivedQty : BigDecimal.ZERO;
        return received.divide(totalExpectedQty, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    /**
     * Check if PO is past cancel date.
     */
    public boolean isPastCancelDate() {
        return cancelDate != null && LocalDateTime.now().isAfter(cancelDate);
    }

    public void addDetail(PurchaseOrderDetail detail) {
        if (details == null) {
            details = new ArrayList<>();
        }
        details.add(detail);
        detail.setPurchaseOrder(this);
        totalLines = details.size();
    }
}
