package com.maersk.wms.inbound.workflow.returns;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Result object for return workflow.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnWorkflowResult {

    private boolean success;
    private String returnKey;
    private String rmaNumber;
    private String status;
    private String message;

    // Quantity summary
    private BigDecimal totalExpectedQty;
    private BigDecimal totalReceivedQty;
    private BigDecimal totalAcceptedQty;
    private BigDecimal totalRejectedQty;
    private BigDecimal totalDamagedQty;
    private BigDecimal variance;

    // Disposition summary
    private BigDecimal restockQty;
    private BigDecimal refurbishQty;
    private BigDecimal disposeQty;
    private BigDecimal vendorReturnQty;

    // Line counts
    private int totalLinesExpected;
    private int totalLinesReceived;
    private int totalLinesInspected;
    private int totalLinesDispositioned;

    // Financial
    private BigDecimal refundAmount;
    private BigDecimal restockingFee;
    private BigDecimal netRefund;
    private String creditMemoNumber;
    private String currency;

    // Timing
    private LocalDateTime workflowStartTime;
    private LocalDateTime receivingStartTime;
    private LocalDateTime receivingEndTime;
    private LocalDateTime inspectionStartTime;
    private LocalDateTime inspectionEndTime;
    private LocalDateTime closedTime;
    private LocalDateTime workflowEndTime;

    // Generated artifacts
    private List<String> putawayTaskKeys;
    private List<String> inventoryTransactionKeys;

    // Errors and warnings
    private List<String> errors;
    private List<String> warnings;

    // Line-level results
    private List<ReturnLineResult> lineResults;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReturnLineResult {
        private String sku;
        private BigDecimal receivedQty;
        private BigDecimal acceptedQty;
        private BigDecimal rejectedQty;
        private String inspectionGrade;
        private String disposition;
        private String dispositionLocation;
        private BigDecimal refundAmount;
    }
}
