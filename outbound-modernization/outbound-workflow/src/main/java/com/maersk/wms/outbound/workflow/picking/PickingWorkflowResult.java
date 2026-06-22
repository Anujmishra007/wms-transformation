package com.maersk.wms.outbound.workflow.picking;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

/**
 * Result of picking workflow execution.
 */
@Value
@Builder
public class PickingWorkflowResult {

    String pickListId;
    boolean success;
    PickingWorkflowStatus status;

    // Counts
    int totalPicks;
    int completedPicks;
    int shortedPicks;
    int skippedPicks;

    // Quantities
    BigDecimal totalQtyPicked;
    BigDecimal totalQtyShorted;

    // Timing
    long sessionDurationMs;
    double picksPerMinute;

    // Details
    List<CompletedPick> completedPickDetails;
    List<ShortedPick> shortedPickDetails;

    String message;

    @Value
    @Builder
    public static class CompletedPick {
        String pickDetailKey;
        BigDecimal qtyPicked;
        String toLpn;
    }

    @Value
    @Builder
    public static class ShortedPick {
        String pickDetailKey;
        BigDecimal expectedQty;
        BigDecimal actualQty;
        String reason;
    }
}
