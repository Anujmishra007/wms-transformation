package com.maersk.wms.inbound.workflow;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Result object for putaway workflow.
 */
@Data
@Builder
public class PutawayWorkflowResult {

    private boolean success;
    private String message;
    private int tasksCompleted;
    private int tasksShorted;
    private int tasksFailed;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<String> completedTaskKeys;
    private List<String> errors;
}
