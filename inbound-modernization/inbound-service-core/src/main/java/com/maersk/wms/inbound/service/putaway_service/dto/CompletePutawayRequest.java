package com.maersk.wms.inbound.service.putaway_service.dto;

import com.maersk.wms.inbound.shared.kernel.identifiers.LocationKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request to complete a putaway task.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompletePutawayRequest {
    private LocationKey actualLocation;  // If different from suggested
    private String userId;
}
