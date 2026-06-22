package com.maersk.wms.inbound.workflow.putaway;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Putaway progress information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PutawayProgress {
    private String receiptKey;
    private int totalTasks;
    private int completedTasks;
    private int inProgressTasks;
    private int pendingTasks;
    private double percentComplete;
    private PutawayWorkflowStatus status;
}
