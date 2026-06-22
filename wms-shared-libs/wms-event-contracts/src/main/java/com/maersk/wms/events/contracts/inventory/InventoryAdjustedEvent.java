package com.maersk.wms.events.contracts.inventory;

import com.maersk.wms.events.contracts.BaseDomainEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * Event published when inventory is adjusted (cycle count, damage, etc.).
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InventoryAdjustedEvent extends BaseDomainEvent {

    public static final String EVENT_TYPE = "inventory.adjusted";

    private String adjustmentKey;
    private String sku;
    private String location;
    private String lpn;
    private String lot;
    private String adjustmentType;
    private BigDecimal systemQty;
    private BigDecimal adjustedQty;
    private BigDecimal variance;
    private String reasonCode;
    private String comments;
    private String userId;
}
