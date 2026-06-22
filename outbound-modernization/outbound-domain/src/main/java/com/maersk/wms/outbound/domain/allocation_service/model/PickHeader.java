package com.maersk.wms.outbound.domain.allocation_service.model;

import com.maersk.wms.outbound.shared.kernel.identifiers.OrderKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.PickHeaderKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.StorerKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.WaveKey;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * PickHeader aggregate root - represents allocation header.
 * Groups pick details for a single order/wave allocation.
 * Part of Inventory Allocation Service bounded context.
 */
@Data
@Builder
public class PickHeader {

    private PickHeaderKey pickHeaderKey;
    private OrderKey orderKey;
    private WaveKey waveKey;
    private StorerKey storerKey;

    // Header attributes
    private PickHeaderStatus status;
    private PickHeaderType type;

    // Assignment
    private String assignedUser;
    private String assignedEquipment;
    private String route;
    private int routeSequence;

    // Quantities
    private BigDecimal totalQtyOrdered;
    private BigDecimal totalQtyAllocated;
    private BigDecimal totalQtyPicked;
    private BigDecimal totalQtyShorted;

    // Pick details
    @Builder.Default
    private List<PickDetail> pickDetails = new ArrayList<>();

    // Timing
    private LocalDateTime releaseTime;
    private LocalDateTime startTime;
    private LocalDateTime completeTime;

    // Audit
    private String addWho;
    private LocalDateTime addDate;
    private String editWho;
    private LocalDateTime editDate;

    // Business methods
    public boolean isFullyPicked() {
        return totalQtyAllocated.compareTo(totalQtyPicked.add(totalQtyShorted)) == 0;
    }

    public boolean hasShorts() {
        return totalQtyShorted.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean canStart() {
        return status == PickHeaderStatus.RELEASED;
    }

    public boolean canComplete() {
        return status == PickHeaderStatus.IN_PROGRESS && isFullyPicked();
    }

    public void addPickDetail(PickDetail detail) {
        pickDetails.add(detail);
        recalculateTotals();
    }

    public void recalculateTotals() {
        this.totalQtyAllocated = pickDetails.stream()
                .map(PickDetail::getQtyAllocated)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.totalQtyPicked = pickDetails.stream()
                .map(PickDetail::getQtyPicked)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.totalQtyShorted = pickDetails.stream()
                .map(PickDetail::getQtyShorted)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getOpenPickCount() {
        return (int) pickDetails.stream()
                .filter(pd -> pd.getStatus() == PickDetailStatus.RELEASED ||
                              pd.getStatus() == PickDetailStatus.IN_PROGRESS)
                .count();
    }
}
