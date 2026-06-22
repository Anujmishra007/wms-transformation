package com.maersk.wms.inbound.domain.operations_service;

import com.maersk.wms.inbound.shared.kernel.identifiers.LpnKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.SkuKey;
import com.maersk.wms.inbound.shared.kernel.valueobjects.LotAttributes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Receipt detail line for Operations subdomain.
 *
 * Legacy Table: RECEIPTDETAIL
 * Legacy SPs: nsp_ReceiveInventory
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptDetail {

    private String receiptDetailKey;
    private Receipt receipt;
    private String lineNumber;

    // SKU
    private SkuKey sku;
    private String skuDescription;
    private String packKey;
    private String uom;

    // Quantities
    private BigDecimal expectedQty;
    private BigDecimal receivedQty;
    private BigDecimal damagedQty;
    private BigDecimal rejectedQty;
    private BigDecimal putawayQty;

    // Status
    private ReceiptDetailStatus status;

    // LPN tracking
    private LpnKey lpn;
    private LpnKey toLpn;

    // Lot tracking
    private LotAttributes lotAttributes;

    // Locations
    private String receiveLocation;
    private String putawayLocation;

    // Document references
    private String poDetailKey;
    private String asnDetailKey;

    // Quality
    private String conditionCode;
    private boolean inspectionRequired;
    private boolean inspected;
    private String inspectionResult;

    // Dates
    private LocalDateTime receiveDate;
    private LocalDateTime putawayDate;

    // Audit
    private String addWho;
    private LocalDateTime addDate;
    private String editWho;
    private LocalDateTime editDate;

    /**
     * Get remaining quantity to receive.
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

    /**
     * Check if fully put away.
     */
    public boolean isFullyPutAway() {
        return putawayQty != null && putawayQty.compareTo(receivedQty) >= 0;
    }
}
