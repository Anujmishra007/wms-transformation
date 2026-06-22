package com.maersk.wms.inbound.workflow.returns;

import com.maersk.wms.inbound.activity.ReturnActivities;
import com.maersk.wms.inbound.activity.ReturnActivities.*;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the return workflow.
 * Orchestrates the complete return lifecycle: Create → Receive → Inspect → Disposition → Close
 *
 * Legacy SP References:
 * - rdtfnc_Return (5,547 lines) - Standard return flow
 * - rdtfnc_EcomReturn (6,099 lines) - E-commerce return flow
 * - rdt_Return_V7_* - Return V7 functions
 */
@Slf4j
public class ReturnWorkflowImpl implements ReturnWorkflow {

    private static final String TASK_QUEUE = "return-processing";

    // Activity stubs
    private final ReturnActivities activities;

    // Workflow state
    private ReturnWorkflowState state;
    private ReturnWorkflowRequest request;

    // Signals received
    private boolean startReceivingSignal = false;
    private boolean completeReceivingSignal = false;
    private boolean startInspectionSignal = false;
    private boolean completeInspectionSignal = false;
    private boolean closeReturnSignal = false;
    private boolean cancelSignal = false;
    private boolean processRefundSignal = false;
    private String cancelReason;

    private final List<ReceiveReturnLineSignal> pendingReceiveSignals = new ArrayList<>();
    private final List<InspectReturnLineSignal> pendingInspectSignals = new ArrayList<>();
    private final List<AssignDispositionSignal> pendingDispositionSignals = new ArrayList<>();
    private boolean autoAssignDispositionsSignal = false;

    public ReturnWorkflowImpl() {
        ActivityOptions options = ActivityOptions.newBuilder()
                .setStartToCloseTimeout(Duration.ofMinutes(5))
                .setRetryOptions(RetryOptions.newBuilder()
                        .setInitialInterval(Duration.ofSeconds(1))
                        .setMaximumInterval(Duration.ofMinutes(1))
                        .setBackoffCoefficient(2.0)
                        .setMaximumAttempts(3)
                        .build())
                .build();

        this.activities = Workflow.newActivityStub(ReturnActivities.class, options);
    }

    @Override
    public ReturnWorkflowResult execute(ReturnWorkflowRequest request) {
        this.request = request;
        initializeState();

        log.info("Starting return workflow for RMA: {}", request.getRmaNumber());

        try {
            // Phase 1: Create Return
            if (!createReturn()) {
                return buildFailureResult("Failed to create return");
            }

            // Check for cancellation
            if (checkCancellation()) {
                return buildCancellationResult();
            }

            // Phase 2: Receiving
            if (!executeReceivingPhase()) {
                return buildFailureResult("Receiving phase failed");
            }

            if (checkCancellation()) {
                return buildCancellationResult();
            }

            // Phase 3: Inspection (if required)
            if (request.isRequiresInspection()) {
                if (!executeInspectionPhase()) {
                    return buildFailureResult("Inspection phase failed");
                }

                if (checkCancellation()) {
                    return buildCancellationResult();
                }
            }

            // Phase 4: Disposition
            if (!executeDispositionPhase()) {
                return buildFailureResult("Disposition phase failed");
            }

            if (checkCancellation()) {
                return buildCancellationResult();
            }

            // Phase 5: Processing and Close
            if (!executeProcessingPhase()) {
                return buildFailureResult("Processing phase failed");
            }

            // Success
            state.setStatus(ReturnWorkflowStatus.COMPLETED);
            state.setClosedTime(LocalDateTime.now());

            log.info("Return workflow completed successfully for RMA: {}", request.getRmaNumber());

            return buildSuccessResult();

        } catch (Exception e) {
            log.error("Return workflow failed for RMA: {}", request.getRmaNumber(), e);
            state.setStatus(ReturnWorkflowStatus.FAILED);
            state.getErrors().add(e.getMessage());
            return buildFailureResult(e.getMessage());
        }
    }

    // ========== Workflow Phases ==========

    private boolean createReturn() {
        updateStatus(ReturnWorkflowStatus.VALIDATING, "Validating RMA and order");

        // Create the return record
        updateStatus(ReturnWorkflowStatus.CREATING_RETURN, "Creating return record");

        CreateReturnInput input = CreateReturnInput.builder()
                .storerKey(request.getStorerKey())
                .returnType(request.getReturnType())
                .originalOrderKey(request.getOriginalOrderKey())
                .originalOrderNumber(request.getOriginalOrderNumber())
                .rmaNumber(request.getRmaNumber())
                .rmaDate(request.getRmaDate())
                .rmaExpiryDate(request.getRmaExpiryDate())
                .returnReasonCode(request.getReturnReasonCode())
                .customerKey(request.getCustomerKey())
                .customerName(request.getCustomerName())
                .customerEmail(request.getCustomerEmail())
                .shipFromName(request.getShipFromName())
                .shipFromAddress1(request.getShipFromAddress1())
                .shipFromCity(request.getShipFromCity())
                .shipFromState(request.getShipFromState())
                .shipFromZip(request.getShipFromZip())
                .shipFromCountry(request.getShipFromCountry())
                .carrierCode(request.getCarrierCode())
                .trackingNumber(request.getTrackingNumber())
                .expectedArrivalDate(request.getExpectedArrivalDate())
                .notes(request.getNotes())
                .build();

        ReturnResult result = activities.createReturn(input, request.getClientCode(), request.getWarehouseCode());

        if (!result.isSuccess()) {
            state.getErrors().addAll(result.getErrors());
            return false;
        }

        state.setReturnKey(result.getReturnKey());

        // Initialize expected lines
        if (request.getExpectedLines() != null) {
            for (ReturnWorkflowRequest.ReturnLineRequest line : request.getExpectedLines()) {
                state.getLines().add(ReturnWorkflowState.ReturnLineState.builder()
                        .sku(line.getSku())
                        .expectedQty(line.getExpectedQty())
                        .receivedQty(BigDecimal.ZERO)
                        .received(false)
                        .inspected(false)
                        .dispositioned(false)
                        .build());
            }
        }

        return true;
    }

    private boolean executeReceivingPhase() {
        updateStatus(ReturnWorkflowStatus.AWAITING_RECEIPT, "Awaiting package receipt");

        // Wait for start signal or auto-start
        if (!request.isAutoStartReceiving()) {
            Workflow.await(() -> startReceivingSignal || cancelSignal);
            if (cancelSignal) return false;
        }

        // Start receiving
        updateStatus(ReturnWorkflowStatus.RECEIVING, "Receiving in progress");
        state.setReceivingStarted(true);
        state.setReceivingStartTime(LocalDateTime.now());

        ReturnResult startResult = activities.startReceiving(
                state.getReturnKey(), request.getClientCode(), request.getWarehouseCode());

        if (!startResult.isSuccess()) {
            state.getErrors().addAll(startResult.getErrors());
            return false;
        }

        // Process receive signals until complete
        while (!completeReceivingSignal && !cancelSignal) {
            // Wait for signals
            Workflow.await(Duration.ofMinutes(30), () ->
                    !pendingReceiveSignals.isEmpty() || completeReceivingSignal || cancelSignal);

            // Process pending receive signals
            while (!pendingReceiveSignals.isEmpty()) {
                ReceiveReturnLineSignal signal = pendingReceiveSignals.remove(0);
                processReceiveLineSignal(signal);
            }

            // Auto-complete if all expected lines received
            if (request.isAutoClose() && state.allLinesReceived()) {
                completeReceivingSignal = true;
            }
        }

        if (cancelSignal) return false;

        // Complete receiving
        ReturnResult completeResult = activities.completeReceiving(
                state.getReturnKey(), request.getClientCode(), request.getWarehouseCode());

        if (!completeResult.isSuccess()) {
            state.getErrors().addAll(completeResult.getErrors());
            return false;
        }

        state.setReceivingCompleted(true);
        state.setReceivingEndTime(LocalDateTime.now());
        updateStatus(ReturnWorkflowStatus.RECEIVING_COMPLETED, "Receiving completed");

        return true;
    }

    private boolean executeInspectionPhase() {
        updateStatus(ReturnWorkflowStatus.AWAITING_INSPECTION, "Awaiting inspection");

        // Wait for start signal or auto-start
        if (!request.isAutoStartInspection()) {
            Workflow.await(() -> startInspectionSignal || cancelSignal);
            if (cancelSignal) return false;
        }

        // Start inspection
        updateStatus(ReturnWorkflowStatus.INSPECTING, "Inspection in progress");
        state.setInspectionStarted(true);
        state.setInspectionStartTime(LocalDateTime.now());

        ReturnResult startResult = activities.startInspection(
                state.getReturnKey(), request.getClientCode(), request.getWarehouseCode());

        if (!startResult.isSuccess()) {
            state.getErrors().addAll(startResult.getErrors());
            return false;
        }

        // Process inspect signals until complete
        while (!completeInspectionSignal && !cancelSignal) {
            Workflow.await(Duration.ofMinutes(30), () ->
                    !pendingInspectSignals.isEmpty() || completeInspectionSignal || cancelSignal);

            while (!pendingInspectSignals.isEmpty()) {
                InspectReturnLineSignal signal = pendingInspectSignals.remove(0);
                processInspectLineSignal(signal);
            }

            // Auto-complete if all lines inspected
            if (request.isAutoClose() && state.allLinesInspected()) {
                completeInspectionSignal = true;
            }
        }

        if (cancelSignal) return false;

        // Complete inspection
        ReturnResult completeResult = activities.completeInspection(
                state.getReturnKey(), request.getClientCode(), request.getWarehouseCode());

        if (!completeResult.isSuccess()) {
            state.getErrors().addAll(completeResult.getErrors());
            return false;
        }

        state.setInspectionCompleted(true);
        state.setInspectionEndTime(LocalDateTime.now());
        updateStatus(ReturnWorkflowStatus.INSPECTION_COMPLETED, "Inspection completed");

        return true;
    }

    private boolean executeDispositionPhase() {
        updateStatus(ReturnWorkflowStatus.ASSIGNING_DISPOSITION, "Assigning dispositions");

        // Auto-assign if requested
        if (request.isAutoAssignDisposition() || autoAssignDispositionsSignal) {
            for (ReturnWorkflowState.ReturnLineState line : state.getLines()) {
                if (line.isInspected() && !line.isDispositioned()) {
                    ReturnLineResult result = activities.autoAssignDisposition(
                            state.getReturnKey(), line.getSku(),
                            request.getClientCode(), request.getWarehouseCode());

                    if (result.isSuccess()) {
                        line.setDispositioned(true);
                        line.setDisposition(result.getDisposition());
                    } else {
                        state.getWarnings().addAll(result.getErrors());
                    }
                }
            }
        }

        // Process manual disposition signals
        while (!state.allLinesDispositioned() && !cancelSignal) {
            Workflow.await(Duration.ofMinutes(30), () ->
                    !pendingDispositionSignals.isEmpty() ||
                    autoAssignDispositionsSignal ||
                    state.allLinesDispositioned() ||
                    cancelSignal);

            while (!pendingDispositionSignals.isEmpty()) {
                AssignDispositionSignal signal = pendingDispositionSignals.remove(0);
                processDispositionSignal(signal);
            }

            if (autoAssignDispositionsSignal) {
                autoAssignDispositionsSignal = false;
                // Auto-assign remaining
                for (ReturnWorkflowState.ReturnLineState line : state.getLines()) {
                    if (!line.isDispositioned()) {
                        activities.autoAssignDisposition(
                                state.getReturnKey(), line.getSku(),
                                request.getClientCode(), request.getWarehouseCode());
                    }
                }
            }
        }

        if (cancelSignal) return false;

        state.setDispositionCompleted(true);
        updateStatus(ReturnWorkflowStatus.DISPOSITION_COMPLETED, "All dispositions assigned");

        return true;
    }

    private boolean executeProcessingPhase() {
        // Process inventory updates
        updateStatus(ReturnWorkflowStatus.PROCESSING_INVENTORY, "Processing inventory updates");

        InventoryResult invResult = activities.processInventoryUpdates(
                state.getReturnKey(), request.getClientCode(), request.getWarehouseCode());

        if (!invResult.isSuccess()) {
            state.getWarnings().addAll(invResult.getErrors());
        }

        // Calculate refund
        updateStatus(ReturnWorkflowStatus.CALCULATING_REFUND, "Calculating refund");

        RefundResult refundResult = activities.calculateRefund(
                state.getReturnKey(), request.getClientCode(), request.getWarehouseCode());

        if (refundResult.isSuccess()) {
            state.setCalculatedRefund(refundResult.getNetRefund());
            state.setRestockingFee(refundResult.getRestockingFee());
        }

        // Generate credit memo if requested
        if (request.isGenerateCreditMemo()) {
            updateStatus(ReturnWorkflowStatus.GENERATING_CREDIT_MEMO, "Generating credit memo");

            CreditMemoResult creditResult = activities.generateCreditMemo(
                    state.getReturnKey(), request.getClientCode(), request.getWarehouseCode());

            if (creditResult.isSuccess()) {
                state.setCreditMemoNumber(creditResult.getCreditMemoNumber());
            } else {
                state.getWarnings().addAll(creditResult.getErrors());
            }
        }

        // Wait for close signal or auto-close
        if (!request.isAutoClose()) {
            updateStatus(ReturnWorkflowStatus.CLOSING, "Ready for closure");
            Workflow.await(() -> closeReturnSignal || cancelSignal);
            if (cancelSignal) return false;
        }

        // Close return
        updateStatus(ReturnWorkflowStatus.CLOSING, "Closing return");

        ReturnResult closeResult = activities.closeReturn(
                state.getReturnKey(), request.getClientCode(), request.getWarehouseCode());

        if (!closeResult.isSuccess()) {
            state.getErrors().addAll(closeResult.getErrors());
            return false;
        }

        state.setClosed(true);
        updateStatus(ReturnWorkflowStatus.CLOSED, "Return closed");

        return true;
    }

    // ========== Signal Processing ==========

    private void processReceiveLineSignal(ReceiveReturnLineSignal signal) {
        ReceiveLineInput input = ReceiveLineInput.builder()
                .returnKey(state.getReturnKey())
                .sku(signal.getSku())
                .quantity(signal.getQuantity())
                .expectedQty(signal.getExpectedQty())
                .returnReasonCode(signal.getReturnReasonCode())
                .lot(signal.getLot())
                .toId(signal.getToId())
                .toLoc(signal.getToLoc())
                .serialNumber(signal.getSerialNumber())
                .build();

        ReturnLineResult result = activities.receiveLineItem(
                input, request.getClientCode(), request.getWarehouseCode());

        if (result.isSuccess()) {
            ReturnWorkflowState.ReturnLineState lineState = state.findLine(signal.getSku());
            if (lineState == null) {
                lineState = ReturnWorkflowState.ReturnLineState.builder()
                        .sku(signal.getSku())
                        .build();
            }
            lineState.setReceived(true);
            lineState.setReceivedQty(signal.getQuantity());
            state.updateLine(lineState);

            // Update totals
            state.setTotalReceivedQty(
                    (state.getTotalReceivedQty() != null ? state.getTotalReceivedQty() : BigDecimal.ZERO)
                            .add(signal.getQuantity()));
        } else {
            state.getErrors().addAll(result.getErrors());
        }
    }

    private void processInspectLineSignal(InspectReturnLineSignal signal) {
        InspectLineInput input = InspectLineInput.builder()
                .returnKey(state.getReturnKey())
                .sku(signal.getSku())
                .inspectionStatus(signal.getInspectionStatus())
                .inspectionGrade(signal.getInspectionGrade())
                .inspectionNotes(signal.getInspectionNotes())
                .acceptedQty(signal.getAcceptedQty())
                .rejectedQty(signal.getRejectedQty())
                .damagedQty(signal.getDamagedQty())
                .defectCode(signal.getDefectCode())
                .build();

        ReturnLineResult result = activities.inspectLineItem(
                input, request.getClientCode(), request.getWarehouseCode());

        if (result.isSuccess()) {
            ReturnWorkflowState.ReturnLineState lineState = state.findLine(signal.getSku());
            if (lineState != null) {
                lineState.setInspected(true);
                lineState.setInspectionGrade(signal.getInspectionGrade());
                lineState.setAcceptedQty(signal.getAcceptedQty());
                lineState.setRejectedQty(signal.getRejectedQty());
                lineState.setDamagedQty(signal.getDamagedQty());
                state.updateLine(lineState);
            }

            // Update totals
            state.setTotalAcceptedQty(
                    (state.getTotalAcceptedQty() != null ? state.getTotalAcceptedQty() : BigDecimal.ZERO)
                            .add(signal.getAcceptedQty() != null ? signal.getAcceptedQty() : BigDecimal.ZERO));
            state.setTotalRejectedQty(
                    (state.getTotalRejectedQty() != null ? state.getTotalRejectedQty() : BigDecimal.ZERO)
                            .add(signal.getRejectedQty() != null ? signal.getRejectedQty() : BigDecimal.ZERO));
        } else {
            state.getErrors().addAll(result.getErrors());
        }
    }

    private void processDispositionSignal(AssignDispositionSignal signal) {
        AssignDispositionInput input = AssignDispositionInput.builder()
                .returnKey(state.getReturnKey())
                .sku(signal.getSku())
                .disposition(signal.getDisposition())
                .dispositionLocation(signal.getDispositionLocation())
                .notes(signal.getDispositionNotes())
                .build();

        ReturnLineResult result = activities.assignDisposition(
                input, request.getClientCode(), request.getWarehouseCode());

        if (result.isSuccess()) {
            ReturnWorkflowState.ReturnLineState lineState = state.findLine(signal.getSku());
            if (lineState != null) {
                lineState.setDispositioned(true);
                lineState.setDisposition(signal.getDisposition());
                lineState.setDispositionLocation(signal.getDispositionLocation());
                state.updateLine(lineState);
            }
        } else {
            state.getErrors().addAll(result.getErrors());
        }
    }

    // ========== Signal Methods ==========

    @Override
    public void startReceiving() {
        this.startReceivingSignal = true;
    }

    @Override
    public void receiveLineItem(ReceiveReturnLineSignal signal) {
        this.pendingReceiveSignals.add(signal);
    }

    @Override
    public void completeReceiving() {
        this.completeReceivingSignal = true;
    }

    @Override
    public void startInspection() {
        this.startInspectionSignal = true;
    }

    @Override
    public void inspectLineItem(InspectReturnLineSignal signal) {
        this.pendingInspectSignals.add(signal);
    }

    @Override
    public void completeInspection() {
        this.completeInspectionSignal = true;
    }

    @Override
    public void assignDisposition(AssignDispositionSignal signal) {
        this.pendingDispositionSignals.add(signal);
    }

    @Override
    public void autoAssignAllDispositions() {
        this.autoAssignDispositionsSignal = true;
    }

    @Override
    public void closeReturn() {
        this.closeReturnSignal = true;
    }

    @Override
    public void cancelReturn(String reason) {
        this.cancelSignal = true;
        this.cancelReason = reason;
    }

    @Override
    public void processRefund() {
        this.processRefundSignal = true;
    }

    // ========== Query Methods ==========

    @Override
    public ReturnWorkflowStatus getStatus() {
        return state.getStatus();
    }

    @Override
    public ReturnWorkflowState getState() {
        return state;
    }

    @Override
    public int getReceivedLineCount() {
        return state.getReceivedLineCount();
    }

    @Override
    public int getInspectedLineCount() {
        return state.getInspectedLineCount();
    }

    @Override
    public List<String> getPendingDispositionSkus() {
        return state.getPendingDispositionSkus();
    }

    @Override
    public BigDecimal getCalculatedRefund() {
        return state.getCalculatedRefund();
    }

    // ========== Helper Methods ==========

    private void initializeState() {
        state = ReturnWorkflowState.builder()
                .rmaNumber(request.getRmaNumber())
                .status(ReturnWorkflowStatus.INITIATED)
                .workflowStartTime(LocalDateTime.now())
                .totalExpectedQty(BigDecimal.ZERO)
                .totalReceivedQty(BigDecimal.ZERO)
                .totalAcceptedQty(BigDecimal.ZERO)
                .totalRejectedQty(BigDecimal.ZERO)
                .lines(new ArrayList<>())
                .errors(new ArrayList<>())
                .warnings(new ArrayList<>())
                .build();
    }

    private void updateStatus(ReturnWorkflowStatus status, String message) {
        state.setStatus(status);
        state.setStatusMessage(message);
        log.info("Return workflow status: {} - {} (RMA: {})", status, message, state.getRmaNumber());
    }

    private boolean checkCancellation() {
        if (cancelSignal) {
            state.setStatus(ReturnWorkflowStatus.CANCELLED);
            state.setCancelled(true);

            // Call cancel activity
            activities.cancelReturn(state.getReturnKey(), cancelReason,
                    request.getClientCode(), request.getWarehouseCode());

            return true;
        }
        return false;
    }

    private ReturnWorkflowResult buildSuccessResult() {
        return ReturnWorkflowResult.builder()
                .success(true)
                .returnKey(state.getReturnKey())
                .rmaNumber(state.getRmaNumber())
                .status(state.getStatus().name())
                .message("Return processed successfully")
                .totalReceivedQty(state.getTotalReceivedQty())
                .totalAcceptedQty(state.getTotalAcceptedQty())
                .totalRejectedQty(state.getTotalRejectedQty())
                .totalLinesReceived(state.getReceivedLineCount())
                .totalLinesInspected(state.getInspectedLineCount())
                .totalLinesDispositioned(state.getDispositionedLineCount())
                .refundAmount(state.getCalculatedRefund())
                .restockingFee(state.getRestockingFee())
                .creditMemoNumber(state.getCreditMemoNumber())
                .workflowStartTime(state.getWorkflowStartTime())
                .receivingStartTime(state.getReceivingStartTime())
                .receivingEndTime(state.getReceivingEndTime())
                .inspectionStartTime(state.getInspectionStartTime())
                .inspectionEndTime(state.getInspectionEndTime())
                .closedTime(state.getClosedTime())
                .workflowEndTime(LocalDateTime.now())
                .warnings(state.getWarnings())
                .lineResults(buildLineResults())
                .build();
    }

    private ReturnWorkflowResult buildFailureResult(String message) {
        return ReturnWorkflowResult.builder()
                .success(false)
                .returnKey(state.getReturnKey())
                .rmaNumber(state.getRmaNumber())
                .status(ReturnWorkflowStatus.FAILED.name())
                .message(message)
                .errors(state.getErrors())
                .warnings(state.getWarnings())
                .workflowStartTime(state.getWorkflowStartTime())
                .workflowEndTime(LocalDateTime.now())
                .build();
    }

    private ReturnWorkflowResult buildCancellationResult() {
        return ReturnWorkflowResult.builder()
                .success(false)
                .returnKey(state.getReturnKey())
                .rmaNumber(state.getRmaNumber())
                .status(ReturnWorkflowStatus.CANCELLED.name())
                .message("Return cancelled: " + cancelReason)
                .warnings(state.getWarnings())
                .workflowStartTime(state.getWorkflowStartTime())
                .workflowEndTime(LocalDateTime.now())
                .build();
    }

    private List<ReturnWorkflowResult.ReturnLineResult> buildLineResults() {
        return state.getLines().stream()
                .map(line -> ReturnWorkflowResult.ReturnLineResult.builder()
                        .sku(line.getSku())
                        .receivedQty(line.getReceivedQty())
                        .acceptedQty(line.getAcceptedQty())
                        .rejectedQty(line.getRejectedQty())
                        .inspectionGrade(line.getInspectionGrade())
                        .disposition(line.getDisposition())
                        .dispositionLocation(line.getDispositionLocation())
                        .refundAmount(line.getLineRefund())
                        .build())
                .toList();
    }
}
