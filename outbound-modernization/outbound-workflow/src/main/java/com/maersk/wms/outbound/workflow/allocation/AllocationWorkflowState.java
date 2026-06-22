package com.maersk.wms.outbound.workflow.allocation;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Current state of allocation workflow.
 */
@Data
@Builder
public class AllocationWorkflowState {

    private String waveKey;
    private String currentOrderKey;
    private int totalOrders;
    private int processedOrders;
    private BigDecimal totalAllocated;
    private BigDecimal totalShortage;
    private LocalDateTime startTime;
    private String lastError;
}
