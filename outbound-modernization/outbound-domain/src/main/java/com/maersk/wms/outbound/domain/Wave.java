package com.maersk.wms.outbound.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Wave entity for grouping orders for batch processing.
 * Maps to WAVE table in the legacy system.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Wave {

    private String waveKey;
    private String storerKey;
    private String waveType;
    private String waveDescription;

    private WaveStatus status;

    private LocalDateTime plannedStartTime;
    private LocalDateTime actualStartTime;
    private LocalDateTime completedTime;

    private int totalOrders;
    private int totalLines;
    private BigDecimal totalQty;
    private BigDecimal totalWeight;
    private BigDecimal totalVolume;

    private int ordersAllocated;
    private int ordersPicked;
    private int ordersPacked;
    private int ordersShipped;

    private String carrierCode;
    private String routeKey;
    private String door;

    private String createdBy;
    private String releasedBy;

    private String addWho;
    private LocalDateTime addDate;
    private String editWho;
    private LocalDateTime editDate;

    @Builder.Default
    private List<String> orderKeys = new ArrayList<>();

    /**
     * Check if wave can be released.
     */
    public boolean canRelease() {
        return status == WaveStatus.PLANNED && !orderKeys.isEmpty();
    }

    /**
     * Check if wave is complete.
     */
    public boolean isComplete() {
        return totalOrders > 0 && ordersShipped >= totalOrders;
    }

    /**
     * Get completion percentage.
     */
    public int getCompletionPercentage() {
        if (totalOrders == 0) return 0;
        return (int) ((ordersShipped * 100.0) / totalOrders);
    }
}
