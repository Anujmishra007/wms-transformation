package com.maersk.wms.inbound.workflow.putaway;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Item to be put away.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PutawayItemRequest {
    private String lpn;
    private String sku;
    private BigDecimal quantity;
    private String fromLocation;
    private String toLocation;  // Optional override
}
