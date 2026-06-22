package com.maersk.wms.picking.shared.kernel.valueobjects;

import com.maersk.wms.picking.shared.kernel.identifiers.*;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

/**
 * Value object representing a pick instruction for RDT device.
 */
@Value
@Builder
public class PickInstruction {
    PickTaskKey pickTaskKey;
    int sequenceNumber;

    // SKU info
    SkuKey sku;
    String skuDescription;
    String skuBarcode;
    String packKey;

    // Source location
    LocationKey fromLocation;
    LpnKey fromLpn;
    String zone;
    String aisle;
    int level;

    // Destination
    LocationKey toLocation;
    LpnKey toLpn;

    // Quantity
    BigDecimal qtyToPick;
    String uom;

    // Order context
    OrderKey orderKey;
    String orderNumber;
    String consigneeName;
    int priority;

    // Special handling
    String specialInstructions;
    boolean requiresLotScan;
    boolean requiresSerialScan;
    boolean requiresWeightCapture;
}
