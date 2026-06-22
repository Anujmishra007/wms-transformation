package com.maersk.wms.outbound.domain.order_service.model;

import com.maersk.wms.outbound.shared.kernel.identifiers.OrderKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.StorerKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.WaveKey;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Wave aggregate - groups orders for batch processing.
 * Part of Order Service bounded context.
 */
@Data
@Builder
public class Wave {

    private WaveKey waveKey;
    private StorerKey storerKey;

    // Wave attributes
    private WaveStatus status;
    private WaveType type;
    private String description;

    // Planning
    private LocalDateTime plannedStartTime;
    private LocalDateTime actualStartTime;
    private LocalDateTime completedTime;

    // Counts
    private int totalOrders;
    private int totalLines;
    private BigDecimal totalQty;
    private BigDecimal allocatedQty;
    private BigDecimal pickedQty;

    // Orders in this wave
    @Builder.Default
    private List<OrderKey> orderKeys = new ArrayList<>();

    // Audit
    private String addWho;
    private LocalDateTime addDate;
    private String editWho;
    private LocalDateTime editDate;

    // Business methods
    public boolean canRelease() {
        return status == WaveStatus.PLANNED && !orderKeys.isEmpty();
    }

    public boolean canAllocate() {
        return status == WaveStatus.RELEASED;
    }

    public boolean isComplete() {
        return status == WaveStatus.COMPLETED;
    }

    public void addOrder(OrderKey orderKey) {
        if (!orderKeys.contains(orderKey)) {
            orderKeys.add(orderKey);
            totalOrders++;
        }
    }

    public void removeOrder(OrderKey orderKey) {
        if (orderKeys.remove(orderKey)) {
            totalOrders--;
        }
    }

    public double getCompletionPercentage() {
        if (totalQty.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return pickedQty.divide(totalQty, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }
}
