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

    private ReceiptKey linkedReceiptKey;

    private ReturnType type;
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
    private String reasonDescription;

    // Carrier
    private String carrierKey;
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

    // Audit
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
}
