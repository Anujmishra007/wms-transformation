package com.maersk.wms.inbound.rules;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Facts for receiving validation rules.
 */
@Data
@Builder
public class ReceivingValidationFacts {

    private String clientCode;
    private String warehouseCode;
    private String sku;
    private String lot;

    private BigDecimal expectedQty;
    private BigDecimal receivedQty;
    private BigDecimal damagedQty;
    private BigDecimal overReceiveQty;

    private LocalDateTime expirationDate;
    private LocalDateTime manufactureDate;
    private int shelfLifeDays;
    private int minShelfLifePercentage;

    private String poKey;
    private String asnKey;
    private boolean blindReceive;
    private boolean overReceiveAllowed;
    private BigDecimal overReceiveTolerance;

    private String conditionCode;
    private boolean isHazmat;
    private boolean requiresSerialCapture;
}
