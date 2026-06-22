package com.maersk.wms.inbound.service.operations_service;

import com.maersk.wms.inbound.domain.operations_service.*;
import com.maersk.wms.inbound.domain.operations_service.repository.TradeReturnRepository;
import com.maersk.wms.inbound.service.operations_service.dto.*;
import com.maersk.wms.inbound.shared.kernel.identifiers.StorerKey;
import com.maersk.wms.inbound.shared.kernel.valueobjects.Quantity;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Service for Returns Receipt operations.
 * Part of inbound-operations-service subdomain (operations/).
 *
 * Responsibilities:
 * - Create and manage trade returns
 * - Process RMA receipts
 * - Manage inspection workflow
 * - Assign dispositions
 * - Trigger return putaway
 */
@Service
@Transactional
public class ReturnsReceiptService {

    private final TradeReturnRepository tradeReturnRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ReturnsReceiptService(TradeReturnRepository tradeReturnRepository,
                                 ApplicationEventPublisher eventPublisher) {
        this.tradeReturnRepository = tradeReturnRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Create a new return receipt.
     */
    public TradeReturn createReturn(CreateReturnRequest request) {
        validateCreateRequest(request);

        TradeReturn tradeReturn = TradeReturn.builder()
            .storerKey(new StorerKey(request.getStorerKey()))
            .rmaNumber(request.getRmaNumber())
            .externalReference(request.getExternalReference())
            .returnType(request.getReturnType())
            .returnReason(request.getReturnReason())
            .originalOrderKey(request.getOriginalOrderKey())
            .customerKey(request.getCustomerKey())
            .customerName(request.getCustomerName())
            .carrierCode(request.getCarrierCode())
            .trackingNumber(request.getTrackingNumber())
            .notes(request.getNotes())
            .createdBy(request.getCreatedBy())
            .build();

        TradeReturn saved = tradeReturnRepository.save(tradeReturn);

        eventPublisher.publishEvent(new ReturnCreatedEvent(saved.getReturnKey()));
        return saved;
    }

    /**
     * Get return by key.
     */
    @Transactional(readOnly = true)
    public Optional<TradeReturn> getReturn(String returnKey) {
        return tradeReturnRepository.findByKey(returnKey);
    }

    /**
     * Get return by RMA number.
     */
    @Transactional(readOnly = true)
    public Optional<TradeReturn> getByRmaNumber(String rmaNumber) {
        return tradeReturnRepository.findByRmaNumber(rmaNumber);
    }

    /**
     * Get returns by storer.
     */
    @Transactional(readOnly = true)
    public List<TradeReturn> getByStorer(String storerKey) {
        return tradeReturnRepository.findByStorerKey(new StorerKey(storerKey));
    }

    /**
     * Get returns by status.
     */
    @Transactional(readOnly = true)
    public List<TradeReturn> getByStatus(ReturnStatus status) {
        return tradeReturnRepository.findByStatus(status);
    }

    /**
     * Get returns pending inspection.
     */
    @Transactional(readOnly = true)
    public List<TradeReturn> getPendingInspection() {
        return tradeReturnRepository.findPendingInspection();
    }

    /**
     * Get returns pending disposition.
     */
    @Transactional(readOnly = true)
    public List<TradeReturn> getPendingDisposition() {
        return tradeReturnRepository.findPendingDisposition();
    }

    /**
     * Start receiving return.
     */
    public TradeReturn startReceiving(String returnKey, String userId) {
        TradeReturn tradeReturn = tradeReturnRepository.findByKey(returnKey)
            .orElseThrow(() -> new IllegalArgumentException("Return not found: " + returnKey));

        tradeReturn.startReceiving(userId);
        return tradeReturnRepository.save(tradeReturn);
    }

    /**
     * Receive return line item.
     */
    public ReceiveReturnResult receiveLine(String returnKey, ReceiveReturnLineRequest request) {
        TradeReturn tradeReturn = tradeReturnRepository.findByKey(returnKey)
            .orElseThrow(() -> new IllegalArgumentException("Return not found: " + returnKey));

        if (!tradeReturn.canReceive()) {
            throw new IllegalStateException("Return not in receivable status");
        }

        ReturnDetail detail = ReturnDetail.builder()
            .skuKey(request.getSkuKey())
            .expectedQty(request.getExpectedQty())
            .receivedQty(request.getReceivedQty())
            .lpnKey(request.getLpnKey())
            .locationKey(request.getLocationKey())
            .lot(request.getLot())
            .serialNumber(request.getSerialNumber())
            .conditionCode(request.getConditionCode())
            .returnReason(request.getReturnReason())
            .originalOrderDetailKey(request.getOriginalOrderDetailKey())
            .receivedBy(request.getReceivedBy())
            .receivedAt(Instant.now())
            .build();

        tradeReturn.addDetail(detail);
        tradeReturnRepository.save(tradeReturn);

        eventPublisher.publishEvent(new ReturnLineReceivedEvent(returnKey, detail.getReturnDetailKey()));

        ReceiveReturnResult result = new ReceiveReturnResult();
        result.setReturnKey(returnKey);
        result.setReturnDetailKey(detail.getReturnDetailKey());
        result.setReceivedQty(request.getReceivedQty());
        result.setSuccess(true);
        return result;
    }

    /**
     * Complete receiving for return.
     */
    public TradeReturn completeReceiving(String returnKey, String userId) {
        TradeReturn tradeReturn = tradeReturnRepository.findByKey(returnKey)
            .orElseThrow(() -> new IllegalArgumentException("Return not found: " + returnKey));

        tradeReturn.completeReceiving(userId);
        TradeReturn saved = tradeReturnRepository.save(tradeReturn);

        eventPublisher.publishEvent(new ReturnReceivingCompletedEvent(returnKey));
        return saved;
    }

    /**
     * Start inspection for return line.
     */
    public TradeReturn startInspection(String returnKey, String returnDetailKey, String userId) {
        TradeReturn tradeReturn = tradeReturnRepository.findByKey(returnKey)
            .orElseThrow(() -> new IllegalArgumentException("Return not found: " + returnKey));

        tradeReturn.startInspection(returnDetailKey, userId);
        return tradeReturnRepository.save(tradeReturn);
    }

    /**
     * Complete inspection for return line.
     */
    public InspectionResult completeInspection(String returnKey, InspectReturnLineRequest request) {
        TradeReturn tradeReturn = tradeReturnRepository.findByKey(returnKey)
            .orElseThrow(() -> new IllegalArgumentException("Return not found: " + returnKey));

        tradeReturn.completeInspection(
            request.getReturnDetailKey(),
            request.getConditionCode(),
            request.getInspectionNotes(),
            request.getInspectedBy()
        );

        tradeReturnRepository.save(tradeReturn);

        InspectionResult result = new InspectionResult();
        result.setReturnKey(returnKey);
        result.setReturnDetailKey(request.getReturnDetailKey());
        result.setConditionCode(request.getConditionCode());
        result.setSuggestedDisposition(suggestDisposition(request.getConditionCode()));
        result.setSuccess(true);
        return result;
    }

    /**
     * Assign disposition to return line.
     */
    public DispositionResult assignDisposition(String returnKey, AssignDispositionRequest request) {
        TradeReturn tradeReturn = tradeReturnRepository.findByKey(returnKey)
            .orElseThrow(() -> new IllegalArgumentException("Return not found: " + returnKey));

        tradeReturn.assignDisposition(
            request.getReturnDetailKey(),
            request.getDisposition(),
            request.getAssignedBy()
        );

        tradeReturnRepository.save(tradeReturn);

        eventPublisher.publishEvent(new DispositionAssignedEvent(
            returnKey, request.getReturnDetailKey(), request.getDisposition()));

        DispositionResult result = new DispositionResult();
        result.setReturnKey(returnKey);
        result.setReturnDetailKey(request.getReturnDetailKey());
        result.setDisposition(request.getDisposition());
        result.setTargetZone(request.getDisposition().getTargetZone());
        result.setRefundEligible(request.getDisposition().isRefundable());
        result.setSuccess(true);
        return result;
    }

    /**
     * Complete disposition for entire return.
     */
    public TradeReturn completeDisposition(String returnKey, String userId) {
        TradeReturn tradeReturn = tradeReturnRepository.findByKey(returnKey)
            .orElseThrow(() -> new IllegalArgumentException("Return not found: " + returnKey));

        tradeReturn.completeDisposition(userId);
        TradeReturn saved = tradeReturnRepository.save(tradeReturn);

        eventPublisher.publishEvent(new ReturnDispositionCompletedEvent(returnKey));
        return saved;
    }

    /**
     * Start putaway for return.
     */
    public TradeReturn startPutaway(String returnKey) {
        TradeReturn tradeReturn = tradeReturnRepository.findByKey(returnKey)
            .orElseThrow(() -> new IllegalArgumentException("Return not found: " + returnKey));

        tradeReturn.startPutaway();
        return tradeReturnRepository.save(tradeReturn);
    }

    /**
     * Complete putaway for return.
     */
    public TradeReturn completePutaway(String returnKey) {
        TradeReturn tradeReturn = tradeReturnRepository.findByKey(returnKey)
            .orElseThrow(() -> new IllegalArgumentException("Return not found: " + returnKey));

        tradeReturn.completePutaway();
        return tradeReturnRepository.save(tradeReturn);
    }

    /**
     * Close return.
     */
    public TradeReturn closeReturn(String returnKey) {
        TradeReturn tradeReturn = tradeReturnRepository.findByKey(returnKey)
            .orElseThrow(() -> new IllegalArgumentException("Return not found: " + returnKey));

        tradeReturn.close();
        return tradeReturnRepository.save(tradeReturn);
    }

    /**
     * Cancel return.
     */
    public TradeReturn cancelReturn(String returnKey, String reason) {
        TradeReturn tradeReturn = tradeReturnRepository.findByKey(returnKey)
            .orElseThrow(() -> new IllegalArgumentException("Return not found: " + returnKey));

        tradeReturn.cancel(reason);
        return tradeReturnRepository.save(tradeReturn);
    }

    /**
     * Calculate refund for return.
     */
    @Transactional(readOnly = true)
    public RefundCalculation calculateRefund(String returnKey) {
        TradeReturn tradeReturn = tradeReturnRepository.findByKey(returnKey)
            .orElseThrow(() -> new IllegalArgumentException("Return not found: " + returnKey));

        BigDecimal totalRefund = BigDecimal.ZERO;
        int refundableLines = 0;

        for (ReturnDetail detail : tradeReturn.getDetails()) {
            if (detail.getDisposition() != null && detail.getDisposition().isRefundable()) {
                totalRefund = totalRefund.add(detail.getRefundAmount() != null
                    ? detail.getRefundAmount() : BigDecimal.ZERO);
                refundableLines++;
            }
        }

        RefundCalculation calc = new RefundCalculation();
        calc.setReturnKey(returnKey);
        calc.setTotalRefundAmount(totalRefund);
        calc.setRefundableLines(refundableLines);
        calc.setTotalLines(tradeReturn.getDetails().size());
        return calc;
    }

    private void validateCreateRequest(CreateReturnRequest request) {
        if (request.getStorerKey() == null || request.getStorerKey().isBlank()) {
            throw new IllegalArgumentException("Storer key is required");
        }
    }

    private ReturnDisposition suggestDisposition(String conditionCode) {
        return switch (conditionCode) {
            case "NEW", "A" -> ReturnDisposition.RESTOCK;
            case "USED", "B" -> ReturnDisposition.REFURBISH;
            case "DAMAGED", "D" -> ReturnDisposition.SCRAP;
            case "DEFECTIVE", "F" -> ReturnDisposition.RETURN_TO_VENDOR;
            default -> ReturnDisposition.QUALITY_HOLD;
        };
    }

    // Event classes
    public record ReturnCreatedEvent(String returnKey) {}
    public record ReturnLineReceivedEvent(String returnKey, String returnDetailKey) {}
    public record ReturnReceivingCompletedEvent(String returnKey) {}
    public record DispositionAssignedEvent(String returnKey, String returnDetailKey, ReturnDisposition disposition) {}
    public record ReturnDispositionCompletedEvent(String returnKey) {}
}
