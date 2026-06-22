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
 * ASN detail line for Document subdomain.
 *
 * Legacy Table: ASNDETAIL
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsnDetail {

    private String asnDetailKey;
    private Asn asn;
    private String lineNumber;

    // SKU
    private SkuKey sku;
    private String skuDescription;
    private String packKey;
    private String uom;

    // Quantities
    private BigDecimal expectedQty;
    private BigDecimal receivedQty;

    // Container info
    private String containerType;  // CARTON, PALLET, etc.
    private int containerQty;
    private String lpn;           // Pre-assigned LPN if any
    private String sscc;          // Serial Shipping Container Code

    // Lot tracking
    private LotAttributes lotAttributes;

    // Weight/dimensions
    private BigDecimal weight;
    private String weightUom;

    // PO reference
    private String poKey;
    private String poDetailKey;

    // Audit
    private String addWho;
    private LocalDateTime addDate;

    /**
     * Get remaining quantity.
     */
    public BigDecimal getRemainingQty() {
        return expectedQty.subtract(receivedQty != null ? receivedQty : BigDecimal.ZERO);
    }

    /**
     * Check if fully received.
     */
    public boolean isFullyReceived() {
        return receivedQty != null && receivedQty.compareTo(expectedQty) >= 0;
    }
}
