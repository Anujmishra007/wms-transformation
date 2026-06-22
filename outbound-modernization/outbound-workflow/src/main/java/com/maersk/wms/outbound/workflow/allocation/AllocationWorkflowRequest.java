package com.maersk.wms.outbound.workflow.allocation;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * Request to start an allocation workflow.
 */
@Value
@Builder
public class AllocationWorkflowRequest {

    String waveKey;
    List<String> orderKeys;
    String storerKey;
    String warehouseCode;
    String userId;

    // Allocation options
    String preferredStrategy;
    boolean allowPartialAllocation;
    boolean requireApprovalForPartial;
    boolean autoRelease;

    // Zone preferences
    List<String> preferredZones;
    List<String> excludedZones;

    // Timeout
    int allocationTimeoutMinutes;
}
