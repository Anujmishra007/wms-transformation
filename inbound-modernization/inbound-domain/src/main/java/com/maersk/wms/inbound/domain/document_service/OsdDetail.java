package com.maersk.wms.inbound.domain.document_service;

import com.maersk.wms.inbound.shared.kernel.identifiers.SkuKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * OSD detail line recording discrepancy for a specific SKU.
 *
 * Legacy Table: OSDDETAIL
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OsdDetail {

    private String osdDetailKey;
    private Osd osd;
    private String lineNumber;

    // SKU
    private SkuKey sku;
    private String skuKey;  // String representation for convenience
    private String skuDescription;
    private String packKey;
    private String uom;

    // Expected vs Actual
    private BigDecimal expectedQty;
    private BigDecimal actualQty;

    // Discrepancy quantities
    private BigDecimal overQty;
    private BigDecimal shortQty;
    private BigDecimal damagedQty;
    private BigDecimal varianceQty;  // calculated variance
    private BigDecimal damageQty;    // alias for damagedQty

    // Lot info
    private String lot;

    // Damage details
    private String damageCode;
    private String damageType;       // damage type code
    private String damageDescription;
    private String damageLocation;  // Where on the item

    // Disposition
    private String disposition;     // ACCEPT, REJECT, RETURN_TO_VENDOR, SCRAP
    private String dispositionNotes;

    // Financial
    private BigDecimal unitCost;
    private BigDecimal claimAmount;

    // Reference
    private String receiptDetailKey;
    private String poDetailKey;
    private String lpn;
    private String lpnKey;

    // Notes
    private String notes;

    // Photos/evidence
    private List<String> photoUrls;

    // Audit
    private String addWho;
    private LocalDateTime addDate;

    /**
     * Calculate variance quantity (actual - expected).
     */
    public BigDecimal getVarianceQty() {
        BigDecimal expected = expectedQty != null ? expectedQty : BigDecimal.ZERO;
        BigDecimal actual = actualQty != null ? actualQty : BigDecimal.ZERO;
        return actual.subtract(expected);
    }

    /**
     * Get OSD type based on quantities.
     */
    public OsdType getOsdType() {
        if (damagedQty != null && damagedQty.compareTo(BigDecimal.ZERO) > 0) {
            return OsdType.DAMAGE;
        }
        BigDecimal variance = getVarianceQty();
        if (variance.compareTo(BigDecimal.ZERO) > 0) {
            return OsdType.OVERAGE;
        } else if (variance.compareTo(BigDecimal.ZERO) < 0) {
            return OsdType.SHORTAGE;
        }
        return null;
    }
}
