package com.maersk.wms.inbound.workflow.putaway;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request to start a putaway workflow.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PutawayWorkflowRequest {
    private String receiptKey;
    private String storerKey;
    private String facility;
    private String userId;

    // Items to put away (if specific items, otherwise all from receipt)
    private List<PutawayItemRequest> items;

    // Strategy
    private String strategyKey;
    private String preferredZone;

    // Behavior options
    @Builder.Default
    private boolean autoCreateTasks = true;
    @Builder.Default
    private boolean autoAssign = false;
    @Builder.Default
    private int timeoutMinutes = 120;
}
