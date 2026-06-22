package com.maersk.wms.inbound.workflow.receiving;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Item that has been received and is ready for putaway.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadyForPutawayItem {
    private String lineNumber;
    private String lpn;
    private String sku;
    private BigDecimal quantity;
    private String location;
    private String lotNumber;
}
