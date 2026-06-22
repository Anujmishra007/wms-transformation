package com.maersk.wms.inbound.workflow.returns;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Internal state object for return workflow.
 * Maintains the current state of the workflow for query methods.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnWorkflowState {

    private String returnKey;
    private String rmaNumber;
    private ReturnWorkflowStatus status;
    private String statusMessage;

    // Phase tracking
    private boolean receivingStarted;
    private boolean receivingCompleted;
    private boolean inspectionStarted;
    private boolean inspectionCompleted;
    private boolean dispositionCompleted;
    private boolean closed;
    private boolean cancelled;

    // Timestamps
    private LocalDateTime workflowStartTime;
    private LocalDateTime receivingStartTime;
    private LocalDateTime receivingEndTime;
    private LocalDateTime inspectionStartTime;
    private LocalDateTime inspectionEndTime;
    private LocalDateTime closedTime;

    // Quantity tracking
    private BigDecimal totalExpectedQty;
    private BigDecimal totalReceivedQty;
    private BigDecimal totalAcceptedQty;
    private BigDecimal totalRejectedQty;

    // Line tracking
    @Builder.Default
    private List<ReturnLineState> lines = new ArrayList<>();

    // Financial
    private BigDecimal calculatedRefund;
    private BigDecimal restockingFee;
    private String creditMemoNumber;

    // Errors
    @Builder.Default
    private List<String> errors = new ArrayList<>();

    @Builder.Default
    private List<String> warnings = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReturnLineState {
        private String sku;
        private BigDecimal expectedQty;
        private BigDecimal receivedQty;
        private BigDecimal acceptedQty;
        private BigDecimal rejectedQty;
        private BigDecimal damagedQty;
        private boolean received;
        private boolean inspected;
        private boolean dispositioned;
        private String inspectionGrade;
        private String disposition;
        private String dispositionLocation;
        private BigDecimal lineRefund;
    }

    /**
     * Get count of received lines.
     */
    public int getReceivedLineCount() {
        return (int) lines.stream().filter(ReturnLineState::isReceived).count();
    }

    /**
     * Get count of inspected lines.
     */
    public int getInspectedLineCount() {
        return (int) lines.stream().filter(ReturnLineState::isInspected).count();
    }

    /**
     * Get count of dispositioned lines.
     */
    public int getDispositionedLineCount() {
        return (int) lines.stream().filter(ReturnLineState::isDispositioned).count();
    }

    /**
     * Get SKUs pending disposition.
     */
    public List<String> getPendingDispositionSkus() {
        return lines.stream()
                .filter(l -> l.isInspected() && !l.isDispositioned())
                .map(ReturnLineState::getSku)
                .toList();
    }

    /**
     * Check if all lines are received.
     */
    public boolean allLinesReceived() {
        return !lines.isEmpty() && lines.stream().allMatch(ReturnLineState::isReceived);
    }

    /**
     * Check if all lines are inspected.
     */
    public boolean allLinesInspected() {
        return !lines.isEmpty() && lines.stream().allMatch(ReturnLineState::isInspected);
    }

    /**
     * Check if all lines have disposition.
     */
    public boolean allLinesDispositioned() {
        return !lines.isEmpty() && lines.stream().allMatch(ReturnLineState::isDispositioned);
    }

    /**
     * Add or update a line state.
     */
    public void updateLine(ReturnLineState lineState) {
        lines.removeIf(l -> l.getSku().equals(lineState.getSku()));
        lines.add(lineState);
    }

    /**
     * Find line state by SKU.
     */
    public ReturnLineState findLine(String sku) {
        return lines.stream()
                .filter(l -> l.getSku().equals(sku))
                .findFirst()
                .orElse(null);
    }
}
