package com.maersk.wms.outbound.workflow.picking;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

/**
 * Pick instruction for RDT device display.
 */
@Value
@Builder
public class PickInstruction {

    String pickDetailKey;
    int sequenceNumber;

    // SKU info
    String sku;
    String skuDescription;
    String skuBarcode;

    // Location
    String fromLocation;
    String fromLpn;
    String zone;
    String aisle;

    // Quantity
    BigDecimal qtyToPick;
    String uom;

    // Destination
    String toLocation;
    String toLpn;

    // Order context
    String orderNumber;
    String consigneeName;

    // Instructions
    String specialInstructions;
    boolean requiresLotScan;
    boolean requiresSerialScan;
}
