package com.maersk.wms.inbound.workflow.putaway;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Putaway task information for queries.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PutawayTaskInfo {
    private String taskKey;
    private String lpn;
    private String sku;
    private BigDecimal quantity;
    private String fromLocation;
    private String toLocation;
    private String actualLocation;
    private String status;
    private String assignedTo;
}
