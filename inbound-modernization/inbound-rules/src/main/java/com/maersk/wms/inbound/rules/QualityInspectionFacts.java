package com.maersk.wms.inbound.rules;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Facts for quality inspection determination rules.
 */
@Data
@Builder
public class QualityInspectionFacts {

    private String clientCode;
    private String warehouseCode;
    private String sku;
    private String productCategory;
    private String supplierKey;

    private BigDecimal receivedQty;
    private String lot;
    private String conditionCode;

    private boolean isNewSupplier;
    private boolean isNewSku;
    private boolean hasQualityHistory;
    private int supplierQualityScore;
    private int recentDefectRate;

    private boolean isReturn;
    private String returnReason;
    private boolean isDamaged;

    private String receiptType;
    private boolean isHighValueItem;
    private boolean isRegulated;
}
