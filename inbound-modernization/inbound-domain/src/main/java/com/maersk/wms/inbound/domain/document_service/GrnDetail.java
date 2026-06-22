package com.maersk.wms.inbound.domain.document_service;

import com.maersk.wms.inbound.shared.kernel.identifiers.SkuKey;
import com.maersk.wms.inbound.shared.kernel.valueobjects.LotAttributes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * GRN detail line.
 *
 * Legacy Table: GRNDETAIL
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrnDetail {

    private String grnDetailKey;
    private Grn grn;
    private String lineNumber;

    // SKU
    private SkuKey sku;
    private String skuDescription;
    private String packKey;
    private String uom;

    // Quantities
    private BigDecimal orderedQty;       // From PO
    private BigDecimal receivedQty;      // Actually received
    private BigDecimal acceptedQty;      // Accepted after inspection
    private BigDecimal rejectedQty;      // Rejected
    private BigDecimal damagedQty;       // Damaged

    // Lot info
    private LotAttributes lotAttributes;

    // Pricing (from PO)
    private BigDecimal unitPrice;
    private BigDecimal lineValue;
    private String currency;

    // Inspection
    private boolean inspected;
    private String inspectionResult;
    private String inspectionNotes;

    // Storage
    private String storageLocation;
    private String lpn;

    // References
    private String receiptDetailKey;
    private String poDetailKey;

    // Audit
    private String addWho;
    private LocalDateTime addDate;

    /**
     * Calculate variance from ordered quantity.
     */
    public BigDecimal getVarianceQty() {
        BigDecimal ordered = orderedQty != null ? orderedQty : BigDecimal.ZERO;
        BigDecimal received = receivedQty != null ? receivedQty : BigDecimal.ZERO;
        return received.subtract(ordered);
    }

    /**
     * Get variance percentage.
     */
    public double getVariancePercentage() {
        if (orderedQty == null || orderedQty.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }
        return getVarianceQty()
                .divide(orderedQty, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }
}
