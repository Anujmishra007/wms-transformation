package com.maersk.wms.inbound.service.putaway_service.dto;

import com.maersk.wms.inbound.shared.kernel.identifiers.LocationKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Suggested putaway location.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationSuggestion {
    private LocationKey location;
    private String zone;
    private String aisle;
    private String bay;
    private String level;
    private String locationType;
    private String strategyUsed;
    private String reason;
    private double score;
}
