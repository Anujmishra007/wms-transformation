package com.maersk.wms.outbound.domain.allocation_service.model;

import com.maersk.wms.outbound.shared.kernel.identifiers.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * PickDetail entity - represents a single pick line.
 * Part of Inventory Allocation Service bounded context.
 */
@Data
@Builder
public class PickDetail {

    private PickDetailKey pickDetailKey;
    private PickHeaderKey pickHeaderKey;
    private OrderKey orderKey;
    private int orderLineNumber;

    // SKU info
    private SkuKey sku;
    private String skuDescription;
    private String packKey;
    private String uom;

    // Location info
    private LocationKey fromLocation;
    private LpnKey fromLpn;
    private LocationKey toLocation;
    private LpnKey toLpn;
    private String lot;

    // Quantities
    private BigDecimal qtyAllocated;
    private BigDecimal qtyPicked;
    private BigDecimal qtyShorted;

    // Status
    private PickDetailStatus status;

    // Picking attributes
    private String zone;
    private String aisle;
    private int pickSequence;
    private int priority;

    // Assignment
    private String assignedUser;
    private LocalDateTime assignedTime;
    private LocalDateTime pickedTime;

    // Audit
    private String addWho;
    private LocalDateTime addDate;
    private String editWho;
    private LocalDateTime editDate;

    // Business methods
    public BigDecimal getOpenQty() {
        return qtyAllocated.subtract(qtyPicked).subtract(qtyShorted);
    }

    public boolean isComplete() {
        return getOpenQty().compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean canPick() {
        return status == PickDetailStatus.RELEASED || status == PickDetailStatus.IN_PROGRESS;
    }

    public boolean canShort() {
        return status == PickDetailStatus.IN_PROGRESS && getOpenQty().compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean canCancel() {
        return status != PickDetailStatus.COMPLETED && status != PickDetailStatus.CANCELLED;
    }

    public void recordPick(BigDecimal qtyPicked, String userId) {
        this.qtyPicked = this.qtyPicked.add(qtyPicked);
        this.editWho = userId;
        this.editDate = LocalDateTime.now();
        if (isComplete()) {
            this.status = PickDetailStatus.COMPLETED;
            this.pickedTime = LocalDateTime.now();
        }
    }

    public void recordShort(BigDecimal qtyShorted, String userId, String reason) {
        this.qtyShorted = this.qtyShorted.add(qtyShorted);
        this.editWho = userId;
        this.editDate = LocalDateTime.now();
        if (isComplete()) {
            this.status = PickDetailStatus.COMPLETED;
            this.pickedTime = LocalDateTime.now();
        }
    }
}
