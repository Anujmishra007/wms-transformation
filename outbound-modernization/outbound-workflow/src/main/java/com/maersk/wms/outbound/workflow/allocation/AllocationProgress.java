package com.maersk.wms.outbound.workflow.allocation;

import lombok.Builder;
import lombok.Value;

/**
 * Progress information for allocation workflow.
 */
@Value
@Builder
public class AllocationProgress {

    String waveKey;
    int totalOrders;
    int processedOrders;
    int fullyAllocated;
    int partiallyAllocated;
    int notAllocated;
    double percentComplete;
    AllocationWorkflowStatus status;
}
