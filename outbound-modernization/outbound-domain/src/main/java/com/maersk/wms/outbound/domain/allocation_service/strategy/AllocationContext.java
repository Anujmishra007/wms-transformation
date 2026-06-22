package com.maersk.wms.outbound.domain.allocation_service.strategy;

import com.maersk.wms.outbound.shared.kernel.identifiers.StorerKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.WaveKey;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Map;

/**
 * Context for allocation strategy execution.
 * Contains parameters that influence allocation behavior.
 */
@Value
@Builder
public class AllocationContext {

    StorerKey storerKey;
    WaveKey waveKey;
    String warehouseCode;
    String userId;

    // Strategy preferences
    String preferredStrategy;
    boolean allowPartialAllocation;
    boolean allowSubstitution;
    boolean respectFifo;
    boolean respectFefo;

    // Zone preferences
    List<String> preferredZones;
    List<String> excludedZones;

    // Lot constraints
    Map<String, String> lottableConstraints;

    // Performance tuning
    int maxLocationsToSearch;
    int maxAllocationsPerLine;
}
