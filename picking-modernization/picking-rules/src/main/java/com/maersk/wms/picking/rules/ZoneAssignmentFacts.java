package com.maersk.wms.picking.rules;

import com.maersk.wms.picking.domain.PickTask;
import lombok.Builder;
import lombok.Data;

/**
 * Facts object for zone assignment rules.
 */
@Data
@Builder
public class ZoneAssignmentFacts {
    private PickTask task;
    private String userId;
    private String assignedZone;
    private String preferredZone;
}
