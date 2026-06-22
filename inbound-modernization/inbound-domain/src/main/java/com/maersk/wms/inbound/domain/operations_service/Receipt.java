package com.maersk.wms.inbound.domain.operations_service;

import com.maersk.wms.inbound.shared.kernel.identifiers.ReceiptKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.StorerKey;
import com.maersk.wms.inbound.shared.kernel.valueobjects.Quantity;
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

    // Vendor
    private String vendorKey;

    // Carrier
    private String carrierKey;
    private String carrierCode;
    private String carrierName;
    private String trailerNumber;
    private String sealNumber;

    // Warehouse info
    private String facility;
    private String door;
    private String dockDoor;
    private String stagingLocation;

    // Schedule
    private java.time.LocalDate expectedDate;
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
    private String createdBy;
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

    /**
     * Start receiving process.
     */
    public void startReceiving(String userId) {
        this.status = ReceiptStatus.IN_PROGRESS;
        this.arrivalDate = LocalDateTime.now();
        this.editWho = userId;
        this.editDate = LocalDateTime.now();
    }

    /**
     * Update totals from detail lines.
     */
    public void updateTotals() {
        if (details == null) return;

        receivedLines = 0;
        receivedQty = BigDecimal.ZERO;
        putawayQty = BigDecimal.ZERO;

        for (ReceiptDetail detail : details) {
            if (detail.getReceivedQty() != null && detail.getReceivedQty().compareTo(BigDecimal.ZERO) > 0) {
                receivedLines++;
                receivedQty = receivedQty.add(detail.getReceivedQty());
            }
            if (detail.getPutawayQty() != null) {
                putawayQty = putawayQty.add(detail.getPutawayQty());
            }
        }
    }

    /**
     * Complete receiving process.
     */
    public void completeReceiving(String userId) {
        this.status = ReceiptStatus.RECEIVED;
        this.receiptDate = LocalDateTime.now();
        this.editWho = userId;
        this.editDate = LocalDateTime.now();
    }

    /**
     * Close the receipt.
     */
    public void close() {
        this.status = ReceiptStatus.CLOSED;
        this.closeDate = LocalDateTime.now();
        this.editDate = LocalDateTime.now();
    }

    /**
     * Cancel the receipt.
     */
    public void cancel(String reason) {
        this.status = ReceiptStatus.CANCELLED;
        this.notes = reason;
        this.editDate = LocalDateTime.now();
    }

    /**
     * Record short receipt.
     */
    public void recordShort(String lineKey, Quantity quantity, String reason) {
        // Find detail and record short
        for (ReceiptDetail detail : details) {
            if (detail.getReceiptDetailKey() != null && detail.getReceiptDetailKey().equals(lineKey)) {
                detail.setShortQty(quantity.getValue());
                detail.setShortReason(reason);
                break;
            }
        }
        this.editDate = LocalDateTime.now();
    }

    /**
     * Record overage.
     */
    public void recordOverage(String lineKey, Quantity quantity, String reason) {
        // Find detail and record overage
        for (ReceiptDetail detail : details) {
            if (detail.getReceiptDetailKey() != null && detail.getReceiptDetailKey().equals(lineKey)) {
                detail.setOverQty(quantity.getValue());
                detail.setOverReason(reason);
                break;
            }
        }
        this.editDate = LocalDateTime.now();
    }

    /**
     * Record damage.
     */
    public void recordDamage(String lineKey, Quantity quantity, String damageCode, String reason) {
        // Find detail and record damage
        for (ReceiptDetail detail : details) {
            if (detail.getReceiptDetailKey() != null && detail.getReceiptDetailKey().equals(lineKey)) {
                detail.setDamagedQty(quantity.getValue());
                detail.setDamageCode(damageCode);
                detail.setDamageReason(reason);
                break;
            }
        }
        this.editDate = LocalDateTime.now();
    }
}
