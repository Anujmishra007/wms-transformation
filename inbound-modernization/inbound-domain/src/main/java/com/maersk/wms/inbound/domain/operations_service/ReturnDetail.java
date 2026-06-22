package com.maersk.wms.inbound.domain.operations_service;

import com.maersk.wms.inbound.shared.kernel.identifiers.LpnKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.SkuKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Return detail line for Operations subdomain.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnDetail {

    private String returnDetailKey;
    private TradeReturn tradeReturn;
    private String lineNumber;

    // SKU
    private SkuKey sku;
    private String skuDescription;
    private String packKey;
    private String uom;

    // Quantities
    private BigDecimal expectedQty;
    private BigDecimal receivedQty;
    private BigDecimal inspectedQty;
    private BigDecimal dispositionedQty;
    private BigDecimal putawayQty;

    // Status
    private ReturnDetailStatus status;

    // LPN
    private LpnKey lpn;

    // Return reason
    private ReturnReason reason;
    private String reasonDescription;

    // Inspection
    private boolean inspectionRequired;
    private boolean inspected;
    private String inspectedBy;
    private LocalDateTime inspectionDate;
    private String inspectionResult;

    // Quality/Condition
    private String conditionCode;
    private String qualityGrade;
    private BigDecimal damagedQty;

    // Disposition
    private ReturnDisposition disposition;
    private String dispositionNotes;
    private String dispositionZone;

    // Refund
    private BigDecimal refundAmount;
    private String refundType;

    // Dates
    private LocalDateTime receivedDate;
    private LocalDateTime dispositionDate;
    private LocalDateTime putawayDate;

    // Audit
    private String addWho;
    private LocalDateTime addDate;

    public boolean isFullyReceived() {
        return receivedQty != null && receivedQty.compareTo(expectedQty) >= 0;
    }

    public boolean isInspected() {
        return inspected || !inspectionRequired;
    }

    public boolean hasDisposition() {
        return disposition != null;
    }
}
