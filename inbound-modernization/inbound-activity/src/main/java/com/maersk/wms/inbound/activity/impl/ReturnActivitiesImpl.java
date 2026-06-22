package com.maersk.wms.inbound.activity.impl;

import com.maersk.wms.inbound.activity.ReturnActivities;
import com.maersk.wms.inbound.domain.returns.*;
import com.maersk.wms.inbound.plugin.InboundPluginContext;
import com.maersk.wms.inbound.service.returns.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of ReturnActivities for Temporal workflow execution.
 * Delegates to return services with proper context.
 *
 * Legacy SP References:
 * - rdtfnc_Return, rdtfnc_EcomReturn
 * - rdt_Return_V7_*, rdt_EcomReturn_*
 * - rdt_Return_ProcessCredit, rdt_Return_UpdateInventory
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReturnActivitiesImpl implements ReturnActivities {

    private final ReturnReceivingService receivingService;
    private final ReturnInspectionService inspectionService;
    private final ReturnProcessingService processingService;

    // ========== Return Creation ==========

    @Override
    public ReturnResult createReturn(CreateReturnInput input, String clientCode, String warehouseCode) {
        log.info("Activity: createReturn - RMA: {} client: {} warehouse: {}",
                input.getRmaNumber(), clientCode, warehouseCode);

        try {
            InboundPluginContext context = buildContext(clientCode, warehouseCode);

            CreateReturnRequest request = CreateReturnRequest.builder()
                    .storerKey(input.getStorerKey())
                    .returnType(ReturnType.valueOf(input.getReturnType()))
                    .originalOrderKey(input.getOriginalOrderKey())
                    .originalOrderNumber(input.getOriginalOrderNumber())
                    .rmaNumber(input.getRmaNumber())
                    .rmaDate(input.getRmaDate())
                    .rmaExpiryDate(input.getRmaExpiryDate())
                    .returnReasonCode(input.getReturnReasonCode())
                    .customerKey(input.getCustomerKey())
                    .customerName(input.getCustomerName())
                    .customerEmail(input.getCustomerEmail())
                    .shipFromName(input.getShipFromName())
                    .shipFromAddress1(input.getShipFromAddress1())
                    .shipFromCity(input.getShipFromCity())
                    .shipFromState(input.getShipFromState())
                    .shipFromZip(input.getShipFromZip())
                    .shipFromCountry(input.getShipFromCountry())
                    .carrierCode(input.getCarrierCode())
                    .trackingNumber(input.getTrackingNumber())
                    .expectedArrivalDate(input.getExpectedArrivalDate())
                    .notes(input.getNotes())
                    .build();

            TradeReturn tradeReturn = receivingService.createReturn(request, context);

            return ReturnResult.builder()
                    .success(true)
                    .returnKey(tradeReturn.getReturnKey())
                    .rmaNumber(tradeReturn.getRmaNumber())
                    .status(tradeReturn.getStatus().name())
                    .returnType(tradeReturn.getReturnType().name())
                    .originalOrderKey(tradeReturn.getOriginalOrderKey())
                    .build();
        } catch (Exception e) {
            log.error("Failed to create return", e);
            return ReturnResult.builder()
                    .success(false)
                    .errors(List.of(e.getMessage()))
                    .build();
        }
    }

    @Override
    public ReturnResult populateFromOrder(String orderKey, String storerKey, String returnType,
                                           String clientCode, String warehouseCode) {
        log.info("Activity: populateFromOrder - order: {} client: {} warehouse: {}",
                orderKey, clientCode, warehouseCode);

        try {
            InboundPluginContext context = buildContext(clientCode, warehouseCode);
            TradeReturn tradeReturn = receivingService.populateFromOrder(
                    orderKey, storerKey, ReturnType.valueOf(returnType), context);

            return ReturnResult.builder()
                    .success(true)
                    .returnKey(tradeReturn.getReturnKey())
                    .status(tradeReturn.getStatus().name())
                    .originalOrderKey(tradeReturn.getOriginalOrderKey())
                    .build();
        } catch (Exception e) {
            log.error("Failed to populate return from order: {}", orderKey, e);
            return ReturnResult.builder()
                    .success(false)
                    .errors(List.of(e.getMessage()))
                    .build();
        }
    }

    @Override
    public ReturnResult findByTrackingNumber(String trackingNumber, String clientCode, String warehouseCode) {
        log.info("Activity: findByTrackingNumber - tracking: {} client: {} warehouse: {}",
                trackingNumber, clientCode, warehouseCode);

        try {
            InboundPluginContext context = buildContext(clientCode, warehouseCode);
            Optional<TradeReturn> returnOpt = receivingService.findByTrackingNumber(trackingNumber, context);

            if (returnOpt.isEmpty()) {
                return ReturnResult.builder()
                        .success(false)
                        .errors(List.of("Return not found for tracking: " + trackingNumber))
                        .build();
            }

            TradeReturn tradeReturn = returnOpt.get();
            return ReturnResult.builder()
                    .success(true)
                    .returnKey(tradeReturn.getReturnKey())
                    .rmaNumber(tradeReturn.getRmaNumber())
                    .status(tradeReturn.getStatus().name())
                    .build();
        } catch (Exception e) {
            log.error("Failed to find return by tracking: {}", trackingNumber, e);
            return ReturnResult.builder()
                    .success(false)
                    .errors(List.of(e.getMessage()))
                    .build();
        }
    }

    @Override
    public ReturnResult findByRmaNumber(String rmaNumber, String clientCode, String warehouseCode) {
        log.info("Activity: findByRmaNumber - RMA: {} client: {} warehouse: {}",
                rmaNumber, clientCode, warehouseCode);

        try {
            Optional<TradeReturn> returnOpt = receivingService.getReturn(rmaNumber);

            if (returnOpt.isEmpty()) {
                return ReturnResult.builder()
                        .success(false)
                        .errors(List.of("Return not found for RMA: " + rmaNumber))
                        .build();
            }

            TradeReturn tradeReturn = returnOpt.get();
            return ReturnResult.builder()
                    .success(true)
                    .returnKey(tradeReturn.getReturnKey())
                    .rmaNumber(tradeReturn.getRmaNumber())
                    .status(tradeReturn.getStatus().name())
                    .build();
        } catch (Exception e) {
            log.error("Failed to find return by RMA: {}", rmaNumber, e);
            return ReturnResult.builder()
                    .success(false)
                    .errors(List.of(e.getMessage()))
                    .build();
        }
    }

    // ========== Return Receiving ==========

    @Override
    public ReturnResult startReceiving(String returnKey, String clientCode, String warehouseCode) {
        log.info("Activity: startReceiving - return: {} client: {} warehouse: {}",
                returnKey, clientCode, warehouseCode);

        try {
            InboundPluginContext context = buildContext(clientCode, warehouseCode);
            TradeReturn tradeReturn = receivingService.startReceiving(returnKey, context);

            return ReturnResult.builder()
                    .success(true)
                    .returnKey(tradeReturn.getReturnKey())
                    .status(tradeReturn.getStatus().name())
                    .build();
        } catch (Exception e) {
            log.error("Failed to start receiving: {}", returnKey, e);
            return ReturnResult.builder()
                    .success(false)
                    .errors(List.of(e.getMessage()))
                    .build();
        }
    }

    @Override
    public ReturnLineResult receiveLineItem(ReceiveLineInput input, String clientCode, String warehouseCode) {
        log.info("Activity: receiveLineItem - return: {} SKU: {} client: {} warehouse: {}",
                input.getReturnKey(), input.getSku(), clientCode, warehouseCode);

        try {
            InboundPluginContext context = buildContext(clientCode, warehouseCode);

            ReceiveReturnLineRequest request = ReceiveReturnLineRequest.builder()
                    .returnKey(input.getReturnKey())
                    .sku(input.getSku())
                    .quantity(input.getQuantity())
                    .expectedQty(input.getExpectedQty())
                    .returnReasonCode(input.getReturnReasonCode())
                    .lot(input.getLot())
                    .toId(input.getToId())
                    .toLoc(input.getToLoc())
                    .serialNumber(input.getSerialNumber())
                    .build();

            ReturnDetail detail = receivingService.receiveLineItem(request, context);

            return ReturnLineResult.builder()
                    .success(true)
                    .returnKey(input.getReturnKey())
                    .sku(detail.getSku())
                    .quantity(detail.getReceivedQty())
                    .build();
        } catch (Exception e) {
            log.error("Failed to receive line item", e);
            return ReturnLineResult.builder()
                    .success(false)
                    .errors(List.of(e.getMessage()))
                    .build();
        }
    }

    @Override
    public ReturnResult completeReceiving(String returnKey, String clientCode, String warehouseCode) {
        log.info("Activity: completeReceiving - return: {} client: {} warehouse: {}",
                returnKey, clientCode, warehouseCode);

        try {
            InboundPluginContext context = buildContext(clientCode, warehouseCode);
            TradeReturn tradeReturn = receivingService.completeReceiving(returnKey, context);

            return ReturnResult.builder()
                    .success(true)
                    .returnKey(tradeReturn.getReturnKey())
                    .status(tradeReturn.getStatus().name())
                    .totalReceived(tradeReturn.getTotalReceivedQty())
                    .build();
        } catch (Exception e) {
            log.error("Failed to complete receiving: {}", returnKey, e);
            return ReturnResult.builder()
                    .success(false)
                    .errors(List.of(e.getMessage()))
                    .build();
        }
    }

    // ========== Return Inspection ==========

    @Override
    public ReturnResult startInspection(String returnKey, String clientCode, String warehouseCode) {
        log.info("Activity: startInspection - return: {} client: {} warehouse: {}",
                returnKey, clientCode, warehouseCode);

        try {
            InboundPluginContext context = buildContext(clientCode, warehouseCode);
            TradeReturn tradeReturn = inspectionService.startInspection(returnKey, context);

            return ReturnResult.builder()
                    .success(true)
                    .returnKey(tradeReturn.getReturnKey())
                    .status(tradeReturn.getStatus().name())
                    .build();
        } catch (Exception e) {
            log.error("Failed to start inspection: {}", returnKey, e);
            return ReturnResult.builder()
                    .success(false)
                    .errors(List.of(e.getMessage()))
                    .build();
        }
    }

    @Override
    public ReturnLineResult inspectLineItem(InspectLineInput input, String clientCode, String warehouseCode) {
        log.info("Activity: inspectLineItem - return: {} SKU: {} grade: {} client: {} warehouse: {}",
                input.getReturnKey(), input.getSku(), input.getInspectionGrade(), clientCode, warehouseCode);

        try {
            InboundPluginContext context = buildContext(clientCode, warehouseCode);

            InspectReturnLineRequest request = InspectReturnLineRequest.builder()
                    .returnKey(input.getReturnKey())
                    .sku(input.getSku())
                    .inspectionStatus(input.getInspectionStatus())
                    .inspectionGrade(input.getInspectionGrade())
                    .inspectionNotes(input.getInspectionNotes())
                    .acceptedQty(input.getAcceptedQty())
                    .rejectedQty(input.getRejectedQty())
                    .damagedQty(input.getDamagedQty())
                    .defectCode(input.getDefectCode())
                    .build();

            ReturnDetail detail = inspectionService.inspectLineItem(request, context);

            return ReturnLineResult.builder()
                    .success(true)
                    .returnKey(input.getReturnKey())
                    .sku(detail.getSku())
                    .inspectionGrade(detail.getInspectionGrade())
                    .build();
        } catch (Exception e) {
            log.error("Failed to inspect line item", e);
            return ReturnLineResult.builder()
                    .success(false)
                    .errors(List.of(e.getMessage()))
                    .build();
        }
    }

    @Override
    public ReturnLineResult assignDisposition(AssignDispositionInput input, String clientCode, String warehouseCode) {
        log.info("Activity: assignDisposition - return: {} SKU: {} disposition: {} client: {} warehouse: {}",
                input.getReturnKey(), input.getSku(), input.getDisposition(), clientCode, warehouseCode);

        try {
            InboundPluginContext context = buildContext(clientCode, warehouseCode);

            AssignDispositionRequest request = AssignDispositionRequest.builder()
                    .returnKey(input.getReturnKey())
                    .sku(input.getSku())
                    .disposition(ReturnDisposition.valueOf(input.getDisposition()))
                    .dispositionLocation(input.getDispositionLocation())
                    .dispositionNotes(input.getNotes())
                    .build();

            ReturnDetail detail = inspectionService.assignDisposition(request, context);

            return ReturnLineResult.builder()
                    .success(true)
                    .returnKey(input.getReturnKey())
                    .sku(detail.getSku())
                    .disposition(detail.getDisposition().name())
                    .build();
        } catch (Exception e) {
            log.error("Failed to assign disposition", e);
            return ReturnLineResult.builder()
                    .success(false)
                    .errors(List.of(e.getMessage()))
                    .build();
        }
    }

    @Override
    public ReturnLineResult autoAssignDisposition(String returnKey, String sku, String clientCode, String warehouseCode) {
        log.info("Activity: autoAssignDisposition - return: {} SKU: {} client: {} warehouse: {}",
                returnKey, sku, clientCode, warehouseCode);

        try {
            InboundPluginContext context = buildContext(clientCode, warehouseCode);
            ReturnDetail detail = inspectionService.autoAssignDisposition(returnKey, sku, context);

            return ReturnLineResult.builder()
                    .success(true)
                    .returnKey(returnKey)
                    .sku(detail.getSku())
                    .disposition(detail.getDisposition().name())
                    .build();
        } catch (Exception e) {
            log.error("Failed to auto-assign disposition", e);
            return ReturnLineResult.builder()
                    .success(false)
                    .errors(List.of(e.getMessage()))
                    .build();
        }
    }

    @Override
    public ReturnResult completeInspection(String returnKey, String clientCode, String warehouseCode) {
        log.info("Activity: completeInspection - return: {} client: {} warehouse: {}",
                returnKey, clientCode, warehouseCode);

        try {
            InboundPluginContext context = buildContext(clientCode, warehouseCode);
            TradeReturn tradeReturn = inspectionService.completeInspection(returnKey, context);

            return ReturnResult.builder()
                    .success(true)
                    .returnKey(tradeReturn.getReturnKey())
                    .status(tradeReturn.getStatus().name())
                    .build();
        } catch (Exception e) {
            log.error("Failed to complete inspection: {}", returnKey, e);
            return ReturnResult.builder()
                    .success(false)
                    .errors(List.of(e.getMessage()))
                    .build();
        }
    }

    // ========== Return Processing ==========

    @Override
    public ReturnResult closeReturn(String returnKey, String clientCode, String warehouseCode) {
        log.info("Activity: closeReturn - return: {} client: {} warehouse: {}",
                returnKey, clientCode, warehouseCode);

        try {
            InboundPluginContext context = buildContext(clientCode, warehouseCode);
            TradeReturn tradeReturn = processingService.closeReturn(returnKey, context);

            return ReturnResult.builder()
                    .success(true)
                    .returnKey(tradeReturn.getReturnKey())
                    .status(tradeReturn.getStatus().name())
                    .build();
        } catch (Exception e) {
            log.error("Failed to close return: {}", returnKey, e);
            return ReturnResult.builder()
                    .success(false)
                    .errors(List.of(e.getMessage()))
                    .build();
        }
    }

    @Override
    public RefundResult calculateRefund(String returnKey, String clientCode, String warehouseCode) {
        log.info("Activity: calculateRefund - return: {} client: {} warehouse: {}",
                returnKey, clientCode, warehouseCode);

        try {
            InboundPluginContext context = buildContext(clientCode, warehouseCode);
            RefundCalculationResult result = processingService.calculateRefund(returnKey, context);

            return RefundResult.builder()
                    .success(true)
                    .returnKey(returnKey)
                    .grossRefund(result.getGrossRefund())
                    .restockingFee(result.getRestockingFee())
                    .netRefund(result.getNetRefund())
                    .currency(result.getCurrency())
                    .build();
        } catch (Exception e) {
            log.error("Failed to calculate refund: {}", returnKey, e);
            return RefundResult.builder()
                    .success(false)
                    .errors(List.of(e.getMessage()))
                    .build();
        }
    }

    @Override
    public InventoryResult processInventoryUpdates(String returnKey, String clientCode, String warehouseCode) {
        log.info("Activity: processInventoryUpdates - return: {} client: {} warehouse: {}",
                returnKey, clientCode, warehouseCode);

        try {
            InboundPluginContext context = buildContext(clientCode, warehouseCode);
            InventoryUpdateResult result = processingService.processInventoryUpdates(returnKey, context);

            return InventoryResult.builder()
                    .success(result.isSuccess())
                    .returnKey(returnKey)
                    .restockedQty(result.getRestockedQty())
                    .refurbishedQty(result.getRefurbishedQty())
                    .disposedQty(result.getDisposedQty())
                    .vendorReturnQty(result.getVendorReturnQty())
                    .errors(result.getErrors())
                    .build();
        } catch (Exception e) {
            log.error("Failed to process inventory updates: {}", returnKey, e);
            return InventoryResult.builder()
                    .success(false)
                    .errors(List.of(e.getMessage()))
                    .build();
        }
    }

    @Override
    public CreditMemoResult generateCreditMemo(String returnKey, String clientCode, String warehouseCode) {
        log.info("Activity: generateCreditMemo - return: {} client: {} warehouse: {}",
                returnKey, clientCode, warehouseCode);

        try {
            InboundPluginContext context = buildContext(clientCode, warehouseCode);
            com.maersk.wms.inbound.service.returns.CreditMemoResult result =
                    processingService.generateCreditMemo(returnKey, context);

            return CreditMemoResult.builder()
                    .success(result.isSuccess())
                    .returnKey(returnKey)
                    .creditMemoNumber(result.getCreditMemoNumber())
                    .amount(result.getAmount())
                    .currency(result.getCurrency())
                    .errors(result.isSuccess() ? null : List.of(result.getErrorMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Failed to generate credit memo: {}", returnKey, e);
            return CreditMemoResult.builder()
                    .success(false)
                    .errors(List.of(e.getMessage()))
                    .build();
        }
    }

    @Override
    public ReturnResult cancelReturn(String returnKey, String reason, String clientCode, String warehouseCode) {
        log.info("Activity: cancelReturn - return: {} reason: {} client: {} warehouse: {}",
                returnKey, reason, clientCode, warehouseCode);

        try {
            InboundPluginContext context = buildContext(clientCode, warehouseCode);
            TradeReturn tradeReturn = processingService.cancelReturn(returnKey, reason, context);

            return ReturnResult.builder()
                    .success(true)
                    .returnKey(tradeReturn.getReturnKey())
                    .status(tradeReturn.getStatus().name())
                    .build();
        } catch (Exception e) {
            log.error("Failed to cancel return: {}", returnKey, e);
            return ReturnResult.builder()
                    .success(false)
                    .errors(List.of(e.getMessage()))
                    .build();
        }
    }

    // ========== Queries ==========

    @Override
    public List<ReturnSummaryResult> getPendingReturns(String facility, String clientCode, String warehouseCode) {
        log.info("Activity: getPendingReturns - facility: {} client: {} warehouse: {}",
                facility, clientCode, warehouseCode);

        List<TradeReturn> returns = receivingService.getPendingReturns(facility);

        return returns.stream()
                .map(this::toSummaryResult)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReturnSummaryResult> getReturnsRequiringInspection(String facility, String clientCode, String warehouseCode) {
        log.info("Activity: getReturnsRequiringInspection - facility: {} client: {} warehouse: {}",
                facility, clientCode, warehouseCode);

        List<TradeReturn> returns = inspectionService.getReturnsRequiringInspection(facility);

        return returns.stream()
                .map(this::toSummaryResult)
                .collect(Collectors.toList());
    }

    @Override
    public ReturnSummaryResult getReturnSummary(String returnKey, String clientCode, String warehouseCode) {
        log.info("Activity: getReturnSummary - return: {} client: {} warehouse: {}",
                returnKey, clientCode, warehouseCode);

        ReturnSummary summary = processingService.getReturnSummary(returnKey);

        return ReturnSummaryResult.builder()
                .returnKey(summary.getReturnKey())
                .rmaNumber(summary.getRmaNumber())
                .status(summary.getStatus().name())
                .returnType(summary.getReturnType().name())
                .originalOrderKey(summary.getOriginalOrderKey())
                .customerName(summary.getCustomerName())
                .totalExpected(summary.getTotalExpected())
                .totalReceived(summary.getTotalReceived())
                .totalAccepted(summary.getTotalAccepted())
                .restockQty(summary.getRestockQty())
                .refurbishQty(summary.getRefurbishQty())
                .disposeQty(summary.getDisposeQty())
                .refundAmount(summary.getRefundAmount())
                .lineCount(summary.getLineCount())
                .build();
    }

    // ========== Helper Methods ==========

    private InboundPluginContext buildContext(String clientCode, String warehouseCode) {
        return InboundPluginContext.builder()
                .clientCode(clientCode)
                .warehouseCode(warehouseCode)
                .userId("SYSTEM")
                .operationType("RETURN")
                .build();
    }

    private ReturnSummaryResult toSummaryResult(TradeReturn tradeReturn) {
        return ReturnSummaryResult.builder()
                .returnKey(tradeReturn.getReturnKey())
                .rmaNumber(tradeReturn.getRmaNumber())
                .status(tradeReturn.getStatus().name())
                .returnType(tradeReturn.getReturnType() != null ? tradeReturn.getReturnType().name() : null)
                .originalOrderKey(tradeReturn.getOriginalOrderKey())
                .customerName(tradeReturn.getCustomerName())
                .totalExpected(tradeReturn.getTotalExpectedQty())
                .totalReceived(tradeReturn.getTotalReceivedQty())
                .totalAccepted(tradeReturn.getTotalAcceptedQty())
                .restockQty(tradeReturn.getRestockQty())
                .refurbishQty(tradeReturn.getRefurbishQty())
                .disposeQty(tradeReturn.getDisposeQty())
                .refundAmount(tradeReturn.getRefundAmount())
                .lineCount(tradeReturn.getDetails().size())
                .build();
    }
}
