package com.maersk.wms.picking.workflow;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

/**
 * Result of picking workflow execution.
 */
@Data
@Builder
public class PickingWorkflowResult {

    private String taskId;
    private boolean success;
    private boolean skipped;
    private BigDecimal pickedQty;
    private String errorMessage;
    private PickingWorkflowState finalState;

    public static PickingWorkflowResult success(String taskId, BigDecimal pickedQty) {
        return PickingWorkflowResult.builder()
                .taskId(taskId)
                .success(true)
                .pickedQty(pickedQty)
                .finalState(PickingWorkflowState.COMPLETED)
                .build();
    }

    public static PickingWorkflowResult failure(String taskId, String errorMessage) {
        return PickingWorkflowResult.builder()
                .taskId(taskId)
                .success(false)
                .errorMessage(errorMessage)
                .finalState(PickingWorkflowState.FAILED)
                .build();
    }

    public static PickingWorkflowResult skipped(String taskId) {
        return PickingWorkflowResult.builder()
                .taskId(taskId)
                .success(true)
                .skipped(true)
                .finalState(PickingWorkflowState.SKIPPED)
                .build();
    }
}
