package com.maersk.wms.picking.domain.task_execution_service.model;

import com.maersk.wms.picking.shared.kernel.identifiers.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * PickTask aggregate root - represents a single pick task for RDT device execution.
 * Part of Task Execution Service bounded context.
 */
@Data
@Builder
public class PickTask {

    private PickTaskKey pickTaskKey;
    private PickDetailKey pickDetailKey;
    private PickListKey pickListKey;
    private OrderKey orderKey;
    private WaveKey waveKey;

    // SKU info
    private SkuKey sku;
    private String skuDescription;
    private String skuBarcode;
    private String packKey;
    private String uom;

    // Source location
    private LocationKey fromLocation;
    private LpnKey fromLpn;
    private String lot;
    private String zone;
    private String aisle;
    private int level;
    private int pickSequence;

    // Destination
    private LocationKey toLocation;
    private LpnKey toLpn;
    private String dropZone;

    // Quantities
    private BigDecimal qtyToPick;
    private BigDecimal qtyPicked;
    private BigDecimal qtyShorted;

    // Status
    private PickTaskStatus status;
    private TaskType taskType;
    private int priority;

    // Assignment
    private UserKey assignedUser;
    private DeviceKey assignedDevice;
    private LocalDateTime assignedTime;

    // Execution times
    private LocalDateTime startTime;
    private LocalDateTime completeTime;
    private long pickDurationMs;

    // Audit
    private String addWho;
    private LocalDateTime addDate;
    private String editWho;
    private LocalDateTime editDate;

    // Business methods
    public BigDecimal getOpenQty() {
        return qtyToPick.subtract(qtyPicked).subtract(qtyShorted);
    }

    public boolean isComplete() {
        return getOpenQty().compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean canStart() {
        return status == PickTaskStatus.RELEASED || status == PickTaskStatus.ASSIGNED;
    }

    public boolean canConfirm() {
        return status == PickTaskStatus.IN_PROGRESS;
    }

    public boolean canCancel() {
        return status != PickTaskStatus.COMPLETED && status != PickTaskStatus.CANCELLED;
    }

    public void start() {
        this.status = PickTaskStatus.IN_PROGRESS;
        this.startTime = LocalDateTime.now();
    }

    public void start(UserKey userId, DeviceKey deviceId) {
        this.status = PickTaskStatus.IN_PROGRESS;
        this.assignedUser = userId;
        this.assignedDevice = deviceId;
        this.startTime = LocalDateTime.now();
    }

    public String getZone() {
        return zone;
    }

    public String getAisle() {
        return aisle;
    }

    public void recordPick(BigDecimal qty) {
        this.qtyPicked = this.qtyPicked.add(qty);
        this.editDate = LocalDateTime.now();
        if (isComplete()) {
            this.status = PickTaskStatus.COMPLETED;
            this.completeTime = LocalDateTime.now();
            this.pickDurationMs = java.time.Duration.between(startTime, completeTime).toMillis();
        }
    }

    public void recordShort(BigDecimal qty) {
        this.qtyShorted = this.qtyShorted.add(qty);
        this.editDate = LocalDateTime.now();
        if (isComplete()) {
            this.status = PickTaskStatus.COMPLETED;
            this.completeTime = LocalDateTime.now();
        }
    }
}
