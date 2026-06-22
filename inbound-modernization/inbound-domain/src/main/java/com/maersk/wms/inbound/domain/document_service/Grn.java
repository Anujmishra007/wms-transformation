package com.maersk.wms.inbound.domain.document_service;

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
 * GRN (Goods Receipt Note) for Document subdomain (inbound-service).
 *
 * GRN is the formal acknowledgment document that goods have been received.
 * It's typically generated after receiving is complete and serves as
 * the basis for vendor payment and inventory booking.
 *
 * Legacy Table: GRN, GRNDETAIL (or generated from RECEIPT)
 * Legacy SPs: nsp_CreateGRN, nsp_GetGRN, nsp_FinalizeGRN
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Grn {

    private String grnKey;
    private String grnNumber;         // Display/document number
    private String externalGrnKey;

    private GrnStatus status;

    // Owner
    private StorerKey storerKey;

    // Source documents
    private String receiptKey;
    private String asnKey;
    private String poKey;

    // Vendor
    private String vendorKey;
    private String vendorName;
    private String vendorInvoice;
    private String deliveryNote;

    // Dates
    private LocalDateTime receiptDate;
    private LocalDateTime grnDate;          // Date GRN was created
    private LocalDateTime postingDate;      // Date posted to financial system
    private LocalDateTime approvalDate;

    // Totals
    private int totalLines;
    private BigDecimal totalReceivedQty;
    private BigDecimal totalAcceptedQty;
    private BigDecimal totalRejectedQty;
    private BigDecimal totalValue;
    private String currency;

    // Inspection summary
    private boolean inspectionComplete;
    private String inspectionResult;

    // Generated/Approval
    private String generatedBy;
    private String approvedBy;
    private String approvalNotes;

    // Integration
    private String erpReference;          // Reference in ERP/financial system
    private boolean postedToErp;
    private LocalDateTime erpPostingDate;
    private String erpPostingStatus;

    // Quality
    private boolean qualityHold;
    private String qualityHoldReason;

    // Notes
    private String notes;
    private String receiverNotes;

    // Audit
    private String addWho;
    private LocalDateTime addDate;
    private String editWho;
    private LocalDateTime editDate;

    // Details
    @Builder.Default
    private List<GrnDetail> details = new ArrayList<>();

    /**
     * Check if GRN can be finalized.
     */
    public boolean canFinalize() {
        return status == GrnStatus.DRAFT || status == GrnStatus.PENDING_APPROVAL;
    }

    /**
     * Check if GRN is approved.
     */
    public boolean isApproved() {
        return status == GrnStatus.APPROVED || status == GrnStatus.POSTED;
    }

    /**
     * Calculate acceptance rate.
     */
    public double getAcceptanceRate() {
        if (totalReceivedQty == null || totalReceivedQty.compareTo(BigDecimal.ZERO) == 0) {
            return 100.0;
        }
        BigDecimal accepted = totalAcceptedQty != null ? totalAcceptedQty : totalReceivedQty;
        return accepted.divide(totalReceivedQty, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    /**
     * Add detail line.
     */
    public void addDetail(GrnDetail detail) {
        if (details == null) {
            details = new ArrayList<>();
        }
        details.add(detail);
        detail.setGrn(this);
        totalLines = details.size();
    }

    /**
     * Mark the GRN as printed.
     */
    public void markPrinted() {
        // Mark as printed - could update status or set a flag
        this.editDate = LocalDateTime.now();
    }

    /**
     * Approve the GRN.
     */
    public void approve(String approvedBy) {
        if (status != GrnStatus.PENDING_APPROVAL && status != GrnStatus.DRAFT) {
            throw new IllegalStateException("GRN cannot be approved from status: " + status);
        }
        this.status = GrnStatus.APPROVED;
        this.approvedBy = approvedBy;
        this.approvalDate = LocalDateTime.now();
        this.editDate = LocalDateTime.now();
    }

    /**
     * Post GRN to ERP system.
     */
    public void postToErp(String erpReference) {
        if (status != GrnStatus.APPROVED) {
            throw new IllegalStateException("GRN must be approved before posting to ERP");
        }
        this.erpReference = erpReference;
        this.postedToErp = true;
        this.erpPostingDate = LocalDateTime.now();
        this.erpPostingStatus = "POSTED";
        this.status = GrnStatus.POSTED;
        this.editDate = LocalDateTime.now();
    }

    /**
     * Mark ERP posting as failed.
     */
    public void markErpPostingFailed(String errorMessage) {
        this.postedToErp = false;
        this.erpPostingStatus = "FAILED: " + errorMessage;
        this.editDate = LocalDateTime.now();
    }

    /**
     * Finalize/complete the GRN.
     * Named markFinalized to avoid conflict with Object.finalize()
     */
    public void markFinalized() {
        if (!canFinalize()) {
            throw new IllegalStateException("GRN cannot be finalized from status: " + status);
        }
        this.status = GrnStatus.CLOSED;
        this.editDate = LocalDateTime.now();
    }

    /**
     * Cancel the GRN.
     */
    public void cancel(String reason) {
        if (status == GrnStatus.POSTED) {
            throw new IllegalStateException("Cannot cancel a posted GRN");
        }
        this.status = GrnStatus.CANCELLED;
        this.notes = reason;
        this.editDate = LocalDateTime.now();
    }

    /**
     * Calculate totals from details.
     */
    public void calculateTotals() {
        if (details == null || details.isEmpty()) {
            totalLines = 0;
            totalReceivedQty = BigDecimal.ZERO;
            totalAcceptedQty = BigDecimal.ZERO;
            totalRejectedQty = BigDecimal.ZERO;
            return;
        }

        totalLines = details.size();
        totalReceivedQty = details.stream()
            .map(GrnDetail::getReceivedQty)
            .filter(q -> q != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        totalAcceptedQty = details.stream()
            .map(GrnDetail::getAcceptedQty)
            .filter(q -> q != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        totalRejectedQty = details.stream()
            .map(GrnDetail::getRejectedQty)
            .filter(q -> q != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
