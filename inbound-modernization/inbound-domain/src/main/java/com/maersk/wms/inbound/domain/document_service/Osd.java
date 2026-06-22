package com.maersk.wms.inbound.domain.document_service;

import com.maersk.wms.inbound.shared.kernel.identifiers.SkuKey;
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
 * OSD (Over/Short/Damage) Report for Document subdomain (inbound-service).
 *
 * Records discrepancies between expected and actual receipt quantities,
 * as well as any damage discovered during receiving.
 *
 * Legacy Table: OSD, OSDDETAIL
 * Legacy SPs: nsp_CreateOSD, nsp_UpdateOSD, nsp_GetOSD
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Osd {

    private String osdKey;
    private String externalOsdKey;

    private OsdType type;
    private OsdStatus status;

    // Owner
    private StorerKey storerKey;

    // Source document
    private String receiptKey;
    private String receiptDetailKey;
    private String asnKey;
    private String poKey;
    private String carrierKey;
    private String carrierCode;

    // Vendor
    private String vendorKey;
    private String vendorName;

    // Report dates
    private LocalDateTime reportDate;
    private LocalDateTime discoveryDate;
    private LocalDateTime resolutionDate;

    // Reporter
    private String reportedBy;
    private String department;

    // Totals
    private int totalLines;
    private BigDecimal totalOverQty;
    private BigDecimal totalShortQty;
    private BigDecimal totalDamagedQty;
    private BigDecimal totalClaimAmount;
    private String currency;

    // Resolution
    private String resolution;          // CLAIM, ACCEPT, REJECT, CREDIT
    private String resolutionNotes;
    private String resolvedBy;

    // Claim info
    private String claimNumber;
    private BigDecimal claimAmount;
    private BigDecimal creditAmount;
    private BigDecimal debitAmount;
    private String claimStatus;

    // Notes
    private String notes;
    private String internalNotes;

    // Attachments (photos of damage, etc.)
    private List<String> attachmentUrls;

    // Audit
    private String addWho;
    private LocalDateTime addDate;
    private String editWho;
    private LocalDateTime editDate;

    // Details
    @Builder.Default
    private List<OsdDetail> details = new ArrayList<>();

    /**
     * Check if OSD has overage.
     */
    public boolean hasOverage() {
        return totalOverQty != null && totalOverQty.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Check if OSD has shortage.
     */
    public boolean hasShortage() {
        return totalShortQty != null && totalShortQty.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Check if OSD has damage.
     */
    public boolean hasDamage() {
        return totalDamagedQty != null && totalDamagedQty.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Add detail line.
     */
    public void addDetail(OsdDetail detail) {
        if (details == null) {
            details = new ArrayList<>();
        }
        details.add(detail);
        detail.setOsd(this);
        totalLines = details.size();
        recalculateTotals();
    }

    private void recalculateTotals() {
        totalOverQty = BigDecimal.ZERO;
        totalShortQty = BigDecimal.ZERO;
        totalDamagedQty = BigDecimal.ZERO;

        for (OsdDetail detail : details) {
            if (detail.getOverQty() != null) {
                totalOverQty = totalOverQty.add(detail.getOverQty());
            }
            if (detail.getShortQty() != null) {
                totalShortQty = totalShortQty.add(detail.getShortQty());
            }
            if (detail.getDamagedQty() != null) {
                totalDamagedQty = totalDamagedQty.add(detail.getDamagedQty());
            }
        }
    }

    /**
     * Calculate totals (public wrapper).
     */
    public void calculateTotals() {
        recalculateTotals();
    }

    /**
     * Check if OSD is resolved.
     */
    public boolean isResolved() {
        return status == OsdStatus.RESOLVED || status == OsdStatus.CLOSED;
    }

    /**
     * Submit OSD for review.
     */
    public void submitForReview() {
        this.status = OsdStatus.PENDING_REVIEW;
        this.editDate = LocalDateTime.now();
    }

    /**
     * Approve the OSD.
     */
    public void approve(String approver) {
        this.status = OsdStatus.APPROVED;
        this.resolvedBy = approver;
        this.editDate = LocalDateTime.now();
    }

    /**
     * Initiate a claim for the OSD.
     */
    public void initiateClaim(String claimNumber) {
        this.claimNumber = claimNumber;
        this.claimStatus = "INITIATED";
        this.status = OsdStatus.CLAIM_PENDING;
        this.editDate = LocalDateTime.now();
    }

    /**
     * Resolve the OSD.
     */
    public void resolve(String resolution, String notes) {
        this.resolution = resolution;
        this.resolutionNotes = notes;
        this.status = OsdStatus.RESOLVED;
        this.resolutionDate = LocalDateTime.now();
        this.editDate = LocalDateTime.now();
    }

    /**
     * Reject the OSD.
     */
    public void reject(String reason, String rejectedBy) {
        this.status = OsdStatus.REJECTED;
        this.resolutionNotes = reason;
        this.resolvedBy = rejectedBy;
        this.editDate = LocalDateTime.now();
    }
}
