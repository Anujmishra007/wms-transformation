package com.maersk.wms.picking.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Pick Task entity - core domain object for FN839 Piece Pick.
 *
 * Maps to: PickDetail, TaskDetail, PIXTransaction
 * Legacy tables: PICKDETAIL, TASKDETAIL, PIXTRANSACTION
 */
@Data
@Builder
public class PickTask {

    @NotBlank(message = "Task ID is required")
    private String taskId;

    @NotBlank(message = "Order ID is required")
    private String orderId;

    private String orderLineId;
    private String waveId;

    @NotBlank(message = "SKU is required")
    private String sku;

    private String skuDescription;
    private String lot;
    private String lpn;

    @NotBlank(message = "From location is required")
    private String fromLocation;

    private String toLocation;
    private String zone;
    private String aisle;

    @NotNull(message = "Requested quantity is required")
    @Positive(message = "Requested quantity must be positive")
    private BigDecimal requestedQty;

    private BigDecimal pickedQty;
    private BigDecimal shortQty;

    @NotNull(message = "Task status is required")
    private TaskStatus status;

    private TaskType taskType;
    private PickType pickType;
    private String assignedUser;
    private String equipment;
    private int priority;
    private int sequence;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime assignedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    // Concurrency control
    private int trafficCop;
    private byte[] rowVersion;

    // Multi-tenant
    private String countryCode;
    private String clientCode;
    private String warehouseCode;

    /**
     * Check if task can be started.
     */
    public boolean canStart() {
        return status == TaskStatus.ASSIGNED || status == TaskStatus.RELEASED;
    }

    /**
     * Check if task can be completed.
     */
    public boolean canComplete() {
        return status == TaskStatus.IN_PROGRESS;
    }

    /**
     * Check if this is a short pick.
     */
    public boolean isShortPick() {
        return pickedQty != null && requestedQty != null
               && pickedQty.compareTo(requestedQty) < 0;
    }

    /**
     * Calculate remaining quantity.
     */
    public BigDecimal getRemainingQty() {
        if (pickedQty == null) {
            return requestedQty;
        }
        return requestedQty.subtract(pickedQty);
    }
}
