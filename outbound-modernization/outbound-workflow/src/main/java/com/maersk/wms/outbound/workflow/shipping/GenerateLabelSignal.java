package com.maersk.wms.outbound.workflow.shipping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Signal to generate a shipping label for a specific package.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateLabelSignal {

    private String cbolKey;
    private String packageId;

    // Package dimensions (if not already set)
    private BigDecimal weight;
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;
    private String dimensionUom;

    // Label options
    private String format;  // PDF, ZPL, EPL, PNG
    private int copies;
    private String printerName;
    private boolean printImmediately;

    // Address override (if different from MBOL)
    private String shipToName;
    private String shipToAddress1;
    private String shipToAddress2;
    private String shipToCity;
    private String shipToState;
    private String shipToZip;
    private String shipToCountry;

    // User performing the action
    private String userId;
}
