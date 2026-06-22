package com.maersk.wms.inbound.workflow.putaway;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Signal to add an item to the putaway workflow.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddItemSignal {
    private String lpn;
    private String sku;
    private BigDecimal quantity;
    private String fromLocation;
    private String userId;
}
