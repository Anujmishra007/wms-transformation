package com.maersk.wms.picking.api.dto;

import com.maersk.wms.picking.domain.PickTask;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class TaskResponse {
    private String taskId;
    private String orderId;
    private String sku;
    private String skuDescription;
    private String fromLocation;
    private String toLocation;
    private String lpn;
    private String lot;
    private BigDecimal requestedQty;
    private String zone;
    private String aisle;
    private int priority;
    private String status;

    public static TaskResponse from(PickTask task) {
        return TaskResponse.builder()
                .taskId(task.getTaskId())
                .orderId(task.getOrderId())
                .sku(task.getSku())
                .skuDescription(task.getSkuDescription())
                .fromLocation(task.getFromLocation())
                .toLocation(task.getToLocation())
                .lpn(task.getLpn())
                .lot(task.getLot())
                .requestedQty(task.getRequestedQty())
                .zone(task.getZone())
                .aisle(task.getAisle())
                .priority(task.getPriority())
                .status(task.getStatus().name())
                .build();
    }
}
