package com.maersk.wms.inbound.domain.operations_service;

import com.maersk.wms.inbound.shared.kernel.identifiers.ReceiptKey;
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
 * Receipt aggregate root for Operations subdomain (inbound-operations-service).
 *
 * Receipt is the operational entity for actually receiving goods into the warehouse.
 * It's linked to documents (PO, ASN) from the document subdomain.
 *
 * Legacy Table: RECEIPT
 * Legacy SPs: nsp_CreateReceipt, nsp_UpdateReceipt, nsp_ReceiveInventory, rdtfnc_Receiving
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Receipt {

    private ReceiptKey receiptKey;
    private String externalReceiptKey;

    private ReceiptType receiptType;
    private ReceiptStatus status;

    // Owner
    private StorerKey storerKey;

    // Source documents (from document subdomain)
    private String poKey;
    private String asnKey;

    // Carrier
    private String carrierKey;
    private String carrierName;
    private String trailerNumber;
    private String sealNumber;

    // Warehouse info
    private String facility;
    private String door;
    private String stagingLocation;

    // Schedule
    private LocalDateTime expectedDate;
    private LocalDateTime arrivalDate;
    private LocalDateTime receiptDate;
    private LocalDateTime closeDate;

    // Totals
    private int expectedLines;
    private int receivedLines;
    private int putawayLines;
    private BigDecimal expectedQty;
    private BigDecimal receivedQty;
    private BigDecimal putawayQty;

    // Notes
    private String notes;
    private String driverName;

    // Audit
    private String addWho;
    private LocalDateTime addDate;
    private String editWho;
    private LocalDateTime editDate;

    // Details
    @Builder.Default
    private List<ReceiptDetail> details = new ArrayList<>();

    /**
     * Check if receipt can accept more items.
     */
    public boolean canReceive() {
        return status == ReceiptStatus.OPEN || status == ReceiptStatus.IN_PROGRESS;
    }

    /**
     * Check if receipt is ready for putaway.
     */
    public boolean isReadyForPutaway() {
        return status == ReceiptStatus.RECEIVED || status == ReceiptStatus.IN_PROGRESS;
    }

    /**
     * Check if this is a return receipt.
     */
    public boolean isReturn() {
        return receiptType == ReceiptType.RETURN;
    }

    /**
     * Calculate completion percentage.
     */
    public double getCompletionPercentage() {
        if (expectedLines == 0) return 0;
        return (double) putawayLines / expectedLines * 100;
    }

    /**
     * Add a detail line.
     */
    public void addDetail(ReceiptDetail detail) {
        if (details == null) {
            details = new ArrayList<>();
        }
        details.add(detail);
        detail.setReceipt(this);
    }
}
