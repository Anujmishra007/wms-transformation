package com.maersk.wms.inbound.workflow.receiving;

import com.maersk.wms.inbound.activity.receiving.ReceivingActivities;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of ReceivingWorkflow.
 *
 * Orchestrates the 4-phase receiving process:
 * 1. Initialize - Create/load receipt from ASN/PO
 * 2. Receive - Process individual line items
 * 3. Validate - Verify counts, handle over/under receipts
 * 4. Complete - Finalize receipt and trigger putaway
 *
 * Legacy SP Reference: rdtfnc_Receiving, lsp_FinalizeReceipt_Wrapper
 */
public class ReceivingWorkflowImpl implements ReceivingWorkflow {

    // Activity stub
    private final ReceivingActivities activities;

    // Workflow state
    private ReceivingWorkflowStatus status = ReceivingWorkflowStatus.INITIALIZED;
    private ReceivingWorkflowState state;
    private String receiptKey;
    private String cancelReason;
    private final Map<String, LineReceiveState> lineStates = new HashMap<>();
    private final List<ReadyForPutawayItem> readyForPutaway = new ArrayList<>();

    // Signals received
    private boolean startReceivingSignal = false;
    private boolean completeReceivingSignal = false;
    private boolean triggerPutawaySignal = false;
    private boolean cancelSignal = false;
    private final List<ReceiveLineSignal> receiveSignals = new ArrayList<>();
    private final List<DamageReportSignal> damageSignals = new ArrayList<>();
    private final Map<String, Boolean> overReceiptApprovals = new HashMap<>();

    public ReceivingWorkflowImpl() {
        ActivityOptions options = ActivityOptions.newBuilder()
                .setStartToCloseTimeout(Duration.ofMinutes(5))
                .setRetryOptions(RetryOptions.newBuilder()
                        .setInitialInterval(Duration.ofSeconds(1))
                        .setMaximumInterval(Duration.ofMinutes(1))
                        .setBackoffCoefficient(2.0)
                        .setMaximumAttempts(3)
                        .build())
                .build();

        this.activities = Workflow.newActivityStub(ReceivingActivities.class, options);
    }

    @Override
    public ReceivingWorkflowResult execute(ReceivingWorkflowRequest request) {
        try {
            // Initialize state
            state = ReceivingWorkflowState.builder()
                    .receiptKey(request.getReceiptKey())
                    .asnKey(request.getAsnKey())
                    .poKey(request.getPoKey())
                    .storerKey(request.getStorerKey())
                    .build();

            // ==================== PHASE 1: INITIALIZE ====================
            status = ReceivingWorkflowStatus.INITIALIZING;
            receiptKey = initializeReceipt(request);
            state.setReceiptKey(receiptKey);
            status = ReceivingWorkflowStatus.AWAITING_START;

            // Wait for start signal or auto-start if configured
            if (!request.isAutoStart()) {
                Workflow.await(() -> startReceivingSignal || cancelSignal);
            }

            if (cancelSignal) {
                return handleCancellation();
            }

            // ==================== PHASE 2: RECEIVING ====================
            status = ReceivingWorkflowStatus.RECEIVING;
            activities.startReceiving(receiptKey, request.getUserId());

            // Process line items as signals arrive
            while (!completeReceivingSignal && !cancelSignal) {
                // Wait for signals with timeout
                boolean signalReceived = Workflow.await(
                        Duration.ofMinutes(request.getReceivingTimeoutMinutes()),
                        () -> !receiveSignals.isEmpty() || completeReceivingSignal ||
                              !damageSignals.isEmpty() || cancelSignal
                );

                if (!signalReceived) {
                    // Timeout - check if we should auto-complete
                    if (request.isAutoCompleteOnTimeout()) {
                        break;
                    }
                    continue;
                }

                if (cancelSignal) {
                    return handleCancellation();
                }

                // Process receive signals
                processReceiveSignals();

                // Process damage signals
                processDamageSignals();
            }

            // ==================== PHASE 3: VALIDATION ====================
            status = ReceivingWorkflowStatus.VALIDATING;
            validateReceipt();

            // Handle over-receipts if any
            if (hasOverReceipts() && !request.isAutoApproveOverReceipts()) {
                status = ReceivingWorkflowStatus.AWAITING_APPROVAL;
                Workflow.await(() -> allOverReceiptsHandled() || cancelSignal);

                if (cancelSignal) {
                    return handleCancellation();
                }
            }

            // ==================== PHASE 4: COMPLETE ====================
            status = ReceivingWorkflowStatus.COMPLETING;
            activities.completeReceiving(receiptKey, request.getUserId());

            // Trigger putaway if configured
            if (request.isAutoTriggerPutaway() || triggerPutawaySignal) {
                status = ReceivingWorkflowStatus.PUTAWAY_TRIGGERED;
                activities.triggerPutaway(receiptKey);
            }

            status = ReceivingWorkflowStatus.COMPLETED;

            return buildSuccessResult();

        } catch (Exception e) {
            status = ReceivingWorkflowStatus.FAILED;
            state.setErrorMessage(e.getMessage());
            return buildFailureResult(e);
        }
    }

    // ==================== SIGNAL HANDLERS ====================

    @Override
    public void startReceiving() {
        startReceivingSignal = true;
    }

    @Override
    public void receiveLineItem(ReceiveLineSignal signal) {
        receiveSignals.add(signal);
    }

    @Override
    public void completeReceiving() {
        completeReceivingSignal = true;
    }

    @Override
    public void triggerPutaway() {
        triggerPutawaySignal = true;
    }

    @Override
    public void cancelReceiving(String reason) {
        cancelSignal = true;
        cancelReason = reason;
    }

    @Override
    public void approveOverReceipt(String lineNumber, boolean approved) {
        overReceiptApprovals.put(lineNumber, approved);
    }

    @Override
    public void reportDamage(DamageReportSignal signal) {
        damageSignals.add(signal);
    }

    // ==================== QUERY HANDLERS ====================

    @Override
    public ReceivingWorkflowStatus getStatus() {
        return status;
    }

    @Override
    public ReceivingWorkflowState getState() {
        return state;
    }

    @Override
    public ReceiptProgress getProgress() {
        int totalLines = state.getTotalLines();
        int receivedLines = (int) lineStates.values().stream()
                .filter(ls -> ls.getReceivedQty().compareTo(java.math.BigDecimal.ZERO) > 0)
                .count();

        return ReceiptProgress.builder()
                .receiptKey(receiptKey)
                .totalLines(totalLines)
                .receivedLines(receivedLines)
                .percentComplete(totalLines > 0 ? (double) receivedLines / totalLines * 100 : 0)
                .status(status)
                .build();
    }

    @Override
    public List<ReadyForPutawayItem> getItemsReadyForPutaway() {
        return new ArrayList<>(readyForPutaway);
    }

    // ==================== PRIVATE METHODS ====================

    private String initializeReceipt(ReceivingWorkflowRequest request) {
        if (request.getReceiptKey() != null && !request.getReceiptKey().isBlank()) {
            // Use existing receipt
            return request.getReceiptKey();
        } else if (request.getAsnKey() != null && !request.getAsnKey().isBlank()) {
            // Create receipt from ASN
            return activities.createReceiptFromAsn(request.getAsnKey(), request.getUserId());
        } else {
            // Create new receipt
            return activities.createReceipt(request.getStorerKey(), request.getPoKey(), request.getUserId());
        }
    }

    private void processReceiveSignals() {
        while (!receiveSignals.isEmpty()) {
            ReceiveLineSignal signal = receiveSignals.remove(0);
            try {
                var input = com.maersk.wms.inbound.activity.receiving.ReceivingActivities.ReceiveLineInput.builder()
                        .lineNumber(signal.getLineNumber())
                        .sku(signal.getSku())
                        .packKey(signal.getPackKey())
                        .uom(signal.getUom())
                        .quantity(signal.getQuantity())
                        .lpn(signal.getLpn())
                        .location(signal.getLocation())
                        .conditionCode(signal.getConditionCode())
                        .userId(signal.getUserId())
                        .build();
                var result = activities.receiveLineItem(receiptKey, input);

                // Update line state
                LineReceiveState lineState = lineStates.computeIfAbsent(
                        signal.getLineNumber(),
                        k -> new LineReceiveState(signal.getLineNumber())
                );
                lineState.setReceivedQty(lineState.getReceivedQty().add(signal.getQuantity()));
                lineState.setStatus(result.getStatus().name());

                // Track items ready for putaway
                if (result.getStatus().isReceived()) {
                    readyForPutaway.add(ReadyForPutawayItem.builder()
                            .lineNumber(signal.getLineNumber())
                            .lpn(signal.getLpn())
                            .sku(signal.getSku())
                            .quantity(signal.getQuantity())
                            .location(signal.getLocation())
                            .build());
                }

                state.setLinesReceived(state.getLinesReceived() + 1);

            } catch (Exception e) {
                state.setErrorMessage("Failed to receive line " + signal.getLineNumber() + ": " + e.getMessage());
            }
        }
    }

    private void processDamageSignals() {
        while (!damageSignals.isEmpty()) {
            DamageReportSignal signal = damageSignals.remove(0);
            var input = com.maersk.wms.inbound.activity.receiving.ReceivingActivities.DamageReportInput.builder()
                    .lineNumber(signal.getLineNumber())
                    .sku(signal.getSku())
                    .damagedQty(signal.getDamagedQty())
                    .damageCode(signal.getDamageCode())
                    .notes(signal.getDamageDescription())
                    .userId(signal.getUserId())
                    .build();
            activities.reportDamage(receiptKey, input);
        }
    }

    private void validateReceipt() {
        // Validation is performed by activity
        activities.validateReceipt(receiptKey);
    }

    private boolean hasOverReceipts() {
        return lineStates.values().stream().anyMatch(LineReceiveState::isOverReceived);
    }

    private boolean allOverReceiptsHandled() {
        return lineStates.values().stream()
                .filter(LineReceiveState::isOverReceived)
                .allMatch(ls -> overReceiptApprovals.containsKey(ls.getLineNumber()));
    }

    private ReceivingWorkflowResult handleCancellation() {
        status = ReceivingWorkflowStatus.CANCELLED;
        if (receiptKey != null) {
            activities.cancelReceipt(receiptKey, cancelReason);
        }
        return ReceivingWorkflowResult.builder()
                .receiptKey(receiptKey)
                .success(false)
                .status(status)
                .message("Receiving cancelled: " + cancelReason)
                .build();
    }

    private ReceivingWorkflowResult buildSuccessResult() {
        return ReceivingWorkflowResult.builder()
                .receiptKey(receiptKey)
                .success(true)
                .status(status)
                .linesReceived(state.getLinesReceived())
                .readyForPutaway(readyForPutaway)
                .message("Receiving completed successfully")
                .build();
    }

    private ReceivingWorkflowResult buildFailureResult(Exception e) {
        return ReceivingWorkflowResult.builder()
                .receiptKey(receiptKey)
                .success(false)
                .status(status)
                .linesReceived(state.getLinesReceived())
                .message("Receiving failed: " + e.getMessage())
                .build();
    }
}
