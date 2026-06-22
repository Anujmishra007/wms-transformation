package com.maersk.wms.inbound.workflow.receiving;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Signal to receive a line item.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiveLineSignal {
    private String lineNumber;
    private String sku;
    private String packKey;
    private String uom;
    private BigDecimal quantity;
    private String lpn;
    private String location;
    private String lotNumber;
    private String conditionCode;
    private String userId;
}
