package com.maersk.wms.picking.api.dto;

import com.maersk.wms.picking.domain.PickTask;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class ConfirmResponse {
    private boolean success;
    private String taskId;
    private String status;
    private BigDecimal pickedQty;
    private BigDecimal remainingQty;
    private String message;

    public static ConfirmResponse from(PickTask task) {
        return ConfirmResponse.builder()
                .success(true)
                .taskId(task.getTaskId())
                .status(task.getStatus().name())
                .pickedQty(task.getPickedQty())
                .remainingQty(task.getRemainingQty())
                .build();
    }
}
