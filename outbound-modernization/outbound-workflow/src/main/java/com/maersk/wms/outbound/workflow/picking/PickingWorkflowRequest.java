package com.maersk.wms.outbound.workflow.picking;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * Request to start a picking workflow.
 */
@Value
@Builder
public class PickingWorkflowRequest {

    String userId;
    String deviceId;
    String pickListId;
    List<String> pickHeaderKeys;
    String zone;
    String equipmentType;

    // Session options
    boolean autoAdvance;
    boolean allowSkip;
    boolean allowPartialPick;
    int sessionTimeoutMinutes;
}
