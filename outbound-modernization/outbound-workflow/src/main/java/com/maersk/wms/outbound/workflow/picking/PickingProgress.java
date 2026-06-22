package com.maersk.wms.outbound.workflow.picking;

import lombok.Builder;
import lombok.Value;

/**
 * Progress information for picking workflow.
 */
@Value
@Builder
public class PickingProgress {

    String pickListId;
    String userId;
    int totalPicks;
    int completedPicks;
    int shortedPicks;
    int remainingPicks;
    double percentComplete;
    PickingWorkflowStatus status;
    String currentLocation;
    long elapsedTimeMs;
}
