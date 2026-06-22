package com.maersk.wms.inbound.service.operations_service;

import com.maersk.wms.inbound.domain.operations_service.Receipt;
import com.maersk.wms.inbound.domain.operations_service.ReceiptDetail;
import com.maersk.wms.inbound.domain.operations_service.ReceiptStatus;
import com.maersk.wms.inbound.domain.operations_service.repository.ReceiptRepository;
import com.maersk.wms.inbound.service.operations_service.dto.*;
import com.maersk.wms.inbound.shared.kernel.identifiers.ReceiptKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.StorerKey;
import com.maersk.wms.inbound.shared.kernel.valueobjects.Quantity;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Service for Receiving workflow execution.
 * Part of inbound-operations-service subdomain (operations/).
 *
 * Responsibilities:
 * - Create and manage receipts
 * - Execute receiving workflow
 * - Record received quantities
 * - Manage receipt status lifecycle
 * - Trigger putaway task creation
 */
@Service
@Transactional
public class ReceivingService {

    private final ReceiptRepository receiptRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ReceivingService(ReceiptRepository receiptRepository,
                           ApplicationEventPublisher eventPublisher) {
        this.receiptRepository = receiptRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Create a new receipt for receiving.
     */
    public Receipt createReceipt(CreateReceiptRequest request) {
        validateCreateRequest(request);

        Receipt receipt = Receipt.builder()
            .storerKey(new StorerKey(request.getStorerKey()))
            .receiptType(request.getReceiptType())
            .poKey(request.getPoKey())
            .asnKey(request.getAsnKey())
            .vendorKey(request.getVendorKey())
            .carrierCode(request.getCarrierCode())
            .trailerNumber(request.getTrailerNumber())
            .dockDoor(request.getDockDoor())
            .expectedDate(request.getExpectedDate())
            .notes(request.getNotes())
            .createdBy(request.getCreatedBy())
            .build();

        Receipt saved = receiptRepository.save(receipt);

        // Publish receipt created event
        eventPublisher.publishEvent(new ReceiptCreatedEvent(saved.getReceiptKey()));

        return saved;
    }

    /**
     * Get receipt by key.
     */
    @Transactional(readOnly = true)
    public Optional<Receipt> getReceipt(ReceiptKey receiptKey) {
        return receiptRepository.findByKey(receiptKey);
    }

    /**
     * Get receipts by storer.
     */
    @Transactional(readOnly = true)
    public List<Receipt> getByStorer(String storerKey) {
        return receiptRepository.findByStorerKey(new StorerKey(storerKey));
    }

    /**
     * Get receipts by status.
     */
    @Transactional(readOnly = true)
    public List<Receipt> getByStatus(ReceiptStatus status) {
        return receiptRepository.findByStatus(status);
    }

    /**
     * Get active receipts for storer.
     */
    @Transactional(readOnly = true)
    public List<Receipt> getActiveReceipts(String storerKey) {
        return receiptRepository.findActiveByStorer(new StorerKey(storerKey));
    }

    /**
     * Start receiving process.
     */
    public Receipt startReceiving(ReceiptKey receiptKey, String userId) {
        Receipt receipt = receiptRepository.findByKey(receiptKey)
            .orElseThrow(() -> new IllegalArgumentException("Receipt not found: " + receiptKey));

        receipt.startReceiving(userId);
        Receipt saved = receiptRepository.save(receipt);

        eventPublisher.publishEvent(new ReceivingStartedEvent(receiptKey));
        return saved;
    }

    /**
     * Receive a line item.
     */
    public ReceiveResult receiveLine(ReceiptKey receiptKey, ReceiveLineRequest request) {
        Receipt receipt = receiptRepository.findByKey(receiptKey)
            .orElseThrow(() -> new IllegalArgumentException("Receipt not found: " + receiptKey));

        if (!receipt.canReceive()) {
            throw new IllegalStateException("Receipt not in receivable status: " + receipt.getStatus());
        }

        // Create or update receipt detail
        ReceiptDetail detail = ReceiptDetail.builder()
            .skuKey(request.getSkuKey())
            .receivedQty(new Quantity(request.getQuantity(), request.getUom()))
            .lpnKey(request.getLpnKey())
            .locationKey(request.getLocationKey())
            .lot(request.getLot())
            .expiryDate(request.getExpiryDate())
            .poKey(request.getPoKey())
            .poDetailKey(request.getPoDetailKey())
            .conditionCode(request.getConditionCode())
            .receivedBy(request.getReceivedBy())
            .receivedAt(Instant.now())
            .build();

        receipt.addDetail(detail);
        receipt.updateTotals();

        Receipt saved = receiptRepository.save(receipt);

        // Publish line received event
        eventPublisher.publishEvent(new LineReceivedEvent(receiptKey, detail.getReceiptDetailKey()));

        ReceiveResult result = new ReceiveResult();
        result.setReceiptKey(receiptKey.getValue());
        result.setReceiptDetailKey(detail.getReceiptDetailKey());
        result.setReceivedQty(request.getQuantity());
        result.setLpnKey(request.getLpnKey() != null ? request.getLpnKey().getValue() : null);
        result.setSuccess(true);

        return result;
    }

    /**
     * Complete receiving for a receipt.
     */
    public Receipt completeReceiving(ReceiptKey receiptKey, String userId) {
        Receipt receipt = receiptRepository.findByKey(receiptKey)
            .orElseThrow(() -> new IllegalArgumentException("Receipt not found: " + receiptKey));

        receipt.completeReceiving(userId);
        Receipt saved = receiptRepository.save(receipt);

        // Publish receiving completed event (triggers putaway task creation)
        eventPublisher.publishEvent(new ReceivingCompletedEvent(receiptKey));

        return saved;
    }

    /**
     * Close receipt after all operations complete.
     */
    public Receipt closeReceipt(ReceiptKey receiptKey) {
        Receipt receipt = receiptRepository.findByKey(receiptKey)
            .orElseThrow(() -> new IllegalArgumentException("Receipt not found: " + receiptKey));

        receipt.close();
        return receiptRepository.save(receipt);
    }

    /**
     * Cancel receipt.
     */
    public Receipt cancelReceipt(ReceiptKey receiptKey, String reason) {
        Receipt receipt = receiptRepository.findByKey(receiptKey)
            .orElseThrow(() -> new IllegalArgumentException("Receipt not found: " + receiptKey));

        receipt.cancel(reason);
        Receipt saved = receiptRepository.save(receipt);

        eventPublisher.publishEvent(new ReceiptCancelledEvent(receiptKey, reason));
        return saved;
    }

    /**
     * Record short receipt.
     */
    public Receipt recordShortReceipt(ReceiptKey receiptKey, ShortReceiptRequest request) {
        Receipt receipt = receiptRepository.findByKey(receiptKey)
            .orElseThrow(() -> new IllegalArgumentException("Receipt not found: " + receiptKey));

        receipt.recordShort(request.getReceiptDetailKey(),
            new Quantity(request.getShortQty(), request.getUom()),
            request.getReason());

        return receiptRepository.save(receipt);
    }

    /**
     * Record overage.
     */
    public Receipt recordOverage(ReceiptKey receiptKey, OverageRequest request) {
        Receipt receipt = receiptRepository.findByKey(receiptKey)
            .orElseThrow(() -> new IllegalArgumentException("Receipt not found: " + receiptKey));

        receipt.recordOverage(request.getReceiptDetailKey(),
            new Quantity(request.getOverageQty(), request.getUom()),
            request.getReason());

        return receiptRepository.save(receipt);
    }

    /**
     * Record damage.
     */
    public Receipt recordDamage(ReceiptKey receiptKey, DamageRequest request) {
        Receipt receipt = receiptRepository.findByKey(receiptKey)
            .orElseThrow(() -> new IllegalArgumentException("Receipt not found: " + receiptKey));

        receipt.recordDamage(request.getReceiptDetailKey(),
            new Quantity(request.getDamagedQty(), request.getUom()),
            request.getDamageType(),
            request.getReason());

        return receiptRepository.save(receipt);
    }

    /**
     * Get receipts pending putaway.
     */
    @Transactional(readOnly = true)
    public List<Receipt> getPendingPutaway() {
        return receiptRepository.findPendingPutaway();
    }

    private void validateCreateRequest(CreateReceiptRequest request) {
        if (request.getStorerKey() == null || request.getStorerKey().isBlank()) {
            throw new IllegalArgumentException("Storer key is required");
        }
        if (request.getReceiptType() == null) {
            throw new IllegalArgumentException("Receipt type is required");
        }
    }

    // Event classes
    public record ReceiptCreatedEvent(ReceiptKey receiptKey) {}
    public record ReceivingStartedEvent(ReceiptKey receiptKey) {}
    public record LineReceivedEvent(ReceiptKey receiptKey, String receiptDetailKey) {}
    public record ReceivingCompletedEvent(ReceiptKey receiptKey) {}
    public record ReceiptCancelledEvent(ReceiptKey receiptKey, String reason) {}
}
