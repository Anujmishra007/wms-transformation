package com.maersk.wms.inbound.rules;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Facts for putaway location determination rules.
 */
@Data
@Builder
public class PutawayLocationFacts {

    private String clientCode;
    private String warehouseCode;
    private String sku;
    private String skuClass;  // ABC velocity class
    private String productType;
    private String storageType;  // AMBIENT, COLD, HAZMAT, etc.

    private BigDecimal qty;
    private String uom;
    private BigDecimal weight;
    private BigDecimal volume;
    private BigDecimal cubeSize;

    private String lot;
    private LocalDateTime expirationDate;
    private String conditionCode;
    private boolean requiresInspection;

    private String preferredZone;
    private String fixedLocation;
    private boolean allowConsolidation;
    private boolean allowMixedLot;

    private String receiptType;
    private boolean isCrossDock;
    private boolean isReturn;
}
