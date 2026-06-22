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
 * TradeReturn for Operations subdomain (inbound-operations-service).
 *
 * Handles the operational processing of customer returns including
 * receiving, inspection, and disposition.
 *
 * Legacy: rdtfnc_Return, rdtfnc_EcomReturn
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeReturn {

    private String returnKey;
    private String rmaNumber;
    private String externalReturnKey;
    private String externalReference;

    private ReceiptKey linkedReceiptKey;

    private ReturnType type;
    private ReturnType returnType;  // Alias for type
    private ReturnStatus status;

    // Owner
    private StorerKey storerKey;

    // Original order
    private String originalOrderKey;
    private String originalOrderLine;

    // Customer
    private String customerKey;
    private String customerName;
    private String customerReference;

    // Return reason
    private ReturnReason reason;
    private String returnReason;  // String representation of reason
    private String reasonDescription;

    // Carrier
    private String carrierKey;
    private String carrierCode;  // Alias for carrierKey
    private String trackingNumber;

    // Dates
    private LocalDateTime requestDate;
    private LocalDateTime receivedDate;
    private LocalDateTime inspectionDate;
    private LocalDateTime closeDate;

    // Totals
    private int totalLines;
    private int receivedLines;
    private int inspectedLines;
    private int dispositionedLines;
    private int putawayLines;

    // Financial
    private BigDecimal returnValue;
    private BigDecimal refundAmount;
    private String currency;
    private String creditMemoKey;

    // Notes
    private String notes;

    // Audit
    private String createdBy;
    private String addWho;
    private LocalDateTime addDate;
    private String editWho;
    private LocalDateTime editDate;

    // Details
    @Builder.Default
    private List<ReturnDetail> details = new ArrayList<>();

    public boolean canReceive() {
        return status == ReturnStatus.OPEN || status == ReturnStatus.RECEIVING;
    }

    public boolean canInspect() {
        return status == ReturnStatus.RECEIVED || status == ReturnStatus.INSPECTING;
    }

    public boolean isComplete() {
        return status == ReturnStatus.CLOSED || status == ReturnStatus.PUTAWAY_COMPLETE;
    }

    public void addDetail(ReturnDetail detail) {
        if (details == null) details = new ArrayList<>();
        details.add(detail);
        detail.setTradeReturn(this);
        totalLines = details.size();
    }

    /**
     * Start receiving process.
     */
    public void startReceiving(String userId) {
        this.status = ReturnStatus.RECEIVING;
        this.receivedDate = LocalDateTime.now();
        this.editWho = userId;
        this.editDate = LocalDateTime.now();
    }

    /**
     * Complete receiving process.
     */
    public void completeReceiving(String userId) {
        this.status = ReturnStatus.RECEIVED;
        this.editWho = userId;
        this.editDate = LocalDateTime.now();
    }

    /**
     * Start inspection.
     */
    public void startInspection(String lineKey, String userId) {
        this.status = ReturnStatus.INSPECTING;
        this.inspectionDate = LocalDateTime.now();
        this.editWho = userId;
        this.editDate = LocalDateTime.now();
    }

    /**
     * Complete inspection for a line.
     */
    public void completeInspection(String lineKey, String inspectionResult, String conditionCode, String userId) {
        for (ReturnDetail detail : details) {
            if (detail.getReturnDetailKey() != null && detail.getReturnDetailKey().equals(lineKey)) {
                detail.setInspected(true);
                detail.setInspectionResult(inspectionResult);
                detail.setConditionCode(conditionCode);
                inspectedLines++;
                break;
            }
        }
        if (inspectedLines >= totalLines) {
            this.status = ReturnStatus.INSPECTED;
        }
        this.editWho = userId;
        this.editDate = LocalDateTime.now();
    }

    /**
     * Assign disposition for a line.
     */
    public void assignDisposition(String lineKey, ReturnDisposition disposition, String userId) {
        for (ReturnDetail detail : details) {
            if (detail.getReturnDetailKey() != null && detail.getReturnDetailKey().equals(lineKey)) {
                detail.setDisposition(disposition);
                dispositionedLines++;
                break;
            }
        }
        this.status = ReturnStatus.DISPOSITIONING;
        this.editWho = userId;
        this.editDate = LocalDateTime.now();
    }

    /**
     * Complete disposition.
     */
    public void completeDisposition(String userId) {
        this.status = ReturnStatus.DISPOSITIONED;
        this.editWho = userId;
        this.editDate = LocalDateTime.now();
    }

    /**
     * Start putaway.
     */
    public void startPutaway() {
        this.status = ReturnStatus.PUTAWAY;
        this.editDate = LocalDateTime.now();
    }

    /**
     * Complete putaway.
     */
    public void completePutaway() {
        this.status = ReturnStatus.PUTAWAY_COMPLETE;
        this.editDate = LocalDateTime.now();
    }

    /**
     * Close the return.
     */
    public void close() {
        this.status = ReturnStatus.CLOSED;
        this.closeDate = LocalDateTime.now();
        this.editDate = LocalDateTime.now();
    }

    /**
     * Cancel the return.
     */
    public void cancel(String reason) {
        this.status = ReturnStatus.CANCELLED;
        this.reasonDescription = reason;
        this.editDate = LocalDateTime.now();
    }
}
