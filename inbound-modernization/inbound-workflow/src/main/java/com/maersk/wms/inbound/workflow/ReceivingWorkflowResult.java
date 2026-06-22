package com.maersk.wms.inbound.workflow;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Result object for receiving workflow.
 */
@Data
@Builder
public class ReceivingWorkflowResult {

    private boolean success;
    private String receiptKey;
    private String status;
    private String message;

    private BigDecimal totalExpectedQty;
    private BigDecimal totalReceivedQty;
    private BigDecimal totalDamagedQty;
    private BigDecimal variance;

    private int totalLinesExpected;
    private int totalLinesReceived;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private List<String> putawayTaskKeys;
    private List<String> errors;
    private List<String> warnings;
}
