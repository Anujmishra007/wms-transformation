package com.maersk.wms.events.contracts.inventory;

import com.maersk.wms.events.contracts.BaseDomainEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * Event published when inventory is transferred between locations.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InventoryTransferredEvent extends BaseDomainEvent {

    public static final String EVENT_TYPE = "inventory.transferred";

    private String transferKey;
    private String sku;
    private String lot;
    private String fromLocation;
    private String fromLpn;
    private String toLocation;
    private String toLpn;
    private BigDecimal transferQty;
    private String transferType;
    private String userId;
}
