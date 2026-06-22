package com.maersk.wms.inbound.domain.document_service;

import com.maersk.wms.inbound.shared.kernel.identifiers.SkuKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Purchase Order detail line for Document subdomain.
 *
 * Legacy Table: PODETAIL
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderDetail {

    private String poDetailKey;
    private PurchaseOrder purchaseOrder;
    private String lineNumber;

    // SKU
    private SkuKey sku;
    private String skuDescription;
    private String packKey;
    private String uom;

    // Quantities
    private BigDecimal orderedQty;
    private BigDecimal receivedQty;
    private BigDecimal openQty;
    private BigDecimal cancelledQty;

    // Status
    private PoDetailStatus status;

    // Pricing
    private BigDecimal unitPrice;
    private BigDecimal extendedPrice;
    private BigDecimal discount;
    private String currency;

    // Dates
    private LocalDateTime expectedDate;  // Line-level expected date

    // Notes
    private String notes;

    // Audit
    private String addWho;
    private LocalDateTime addDate;
    private String editWho;
    private LocalDateTime editDate;

    /**
     * Calculate open quantity.
     */
    public BigDecimal calculateOpenQty() {
        BigDecimal received = receivedQty != null ? receivedQty : BigDecimal.ZERO;
        BigDecimal cancelled = cancelledQty != null ? cancelledQty : BigDecimal.ZERO;
        return orderedQty.subtract(received).subtract(cancelled);
    }

    /**
     * Check if fully received.
     */
    public boolean isFullyReceived() {
        return calculateOpenQty().compareTo(BigDecimal.ZERO) <= 0;
    }
}
