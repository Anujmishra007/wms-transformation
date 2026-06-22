package com.maersk.wms.inbound.service.document_service;

import com.maersk.wms.inbound.domain.document_service.PoStatus;
import com.maersk.wms.inbound.domain.document_service.PurchaseOrder;
import com.maersk.wms.inbound.domain.document_service.PurchaseOrderDetail;
import com.maersk.wms.inbound.domain.document_service.repository.PurchaseOrderRepository;
import com.maersk.wms.inbound.service.document_service.dto.*;
import com.maersk.wms.inbound.shared.kernel.identifiers.StorerKey;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for Purchase Order management.
 * Part of inbound-service subdomain (document/).
 *
 * Responsibilities:
 * - Create, update, close POs
 * - Manage PO details/lines
 * - Track PO status lifecycle
 * - Validate PO against business rules
 */
@Service
@Transactional
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;

    public PurchaseOrderService(PurchaseOrderRepository purchaseOrderRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    /**
     * Create a new Purchase Order.
     */
    public PurchaseOrder createPurchaseOrder(CreatePoRequest request) {
        // Validate request
        validateCreateRequest(request);

        // Build PO from request
        PurchaseOrder po = PurchaseOrder.builder()
            .storerKey(new StorerKey(request.getStorerKey()))
            .externalPoNumber(request.getExternalPoNumber())
            .poType(request.getPoType())
            .vendorKey(request.getVendorKey())
            .vendorName(request.getVendorName())
            .expectedDate(request.getExpectedDate())
            .buyerName(request.getBuyerName())
            .buyerReference(request.getBuyerReference())
            .notes(request.getNotes())
            .build();

        // Add details
        if (request.getDetails() != null) {
            for (CreatePoDetailRequest detailReq : request.getDetails()) {
                PurchaseOrderDetail detail = createDetailFromRequest(detailReq);
                po.addDetail(detail);
            }
        }

        return purchaseOrderRepository.save(po);
    }

    /**
     * Get PO by key.
     */
    @Transactional(readOnly = true)
    public Optional<PurchaseOrder> getPurchaseOrder(String poKey) {
        return purchaseOrderRepository.findByKey(poKey);
    }

    /**
     * Get PO by external number.
     */
    @Transactional(readOnly = true)
    public Optional<PurchaseOrder> getByExternalNumber(String externalPoNumber) {
        return purchaseOrderRepository.findByExternalPoNumber(externalPoNumber);
    }

    /**
     * Get all POs for a storer.
     */
    @Transactional(readOnly = true)
    public List<PurchaseOrder> getByStorer(String storerKey) {
        return purchaseOrderRepository.findByStorerKey(new StorerKey(storerKey));
    }

    /**
     * Get POs by status.
     */
    @Transactional(readOnly = true)
    public List<PurchaseOrder> getByStatus(PoStatus status) {
        return purchaseOrderRepository.findByStatus(status);
    }

    /**
     * Add detail line to existing PO.
     */
    public PurchaseOrder addDetail(String poKey, CreatePoDetailRequest request) {
        PurchaseOrder po = purchaseOrderRepository.findByKey(poKey)
            .orElseThrow(() -> new IllegalArgumentException("PO not found: " + poKey));

        if (!po.canBeModified()) {
            throw new IllegalStateException("PO cannot be modified in status: " + po.getStatus());
        }

        PurchaseOrderDetail detail = createDetailFromRequest(request);
        po.addDetail(detail);

        return purchaseOrderRepository.save(po);
    }

    /**
     * Update PO header.
     */
    public PurchaseOrder updatePurchaseOrder(String poKey, UpdatePoRequest request) {
        PurchaseOrder po = purchaseOrderRepository.findByKey(poKey)
            .orElseThrow(() -> new IllegalArgumentException("PO not found: " + poKey));

        if (!po.canBeModified()) {
            throw new IllegalStateException("PO cannot be modified in status: " + po.getStatus());
        }

        if (request.getExpectedDate() != null) {
            po.setExpectedDate(request.getExpectedDate());
        }
        if (request.getBuyerName() != null) {
            po.setBuyerName(request.getBuyerName());
        }
        if (request.getNotes() != null) {
            po.setNotes(request.getNotes());
        }

        return purchaseOrderRepository.save(po);
    }

    /**
     * Approve PO for receiving.
     */
    public PurchaseOrder approvePurchaseOrder(String poKey) {
        PurchaseOrder po = purchaseOrderRepository.findByKey(poKey)
            .orElseThrow(() -> new IllegalArgumentException("PO not found: " + poKey));

        po.approve();
        return purchaseOrderRepository.save(po);
    }

    /**
     * Cancel PO.
     */
    public PurchaseOrder cancelPurchaseOrder(String poKey, String reason) {
        PurchaseOrder po = purchaseOrderRepository.findByKey(poKey)
            .orElseThrow(() -> new IllegalArgumentException("PO not found: " + poKey));

        po.cancel(reason);
        return purchaseOrderRepository.save(po);
    }

    /**
     * Close PO after all receiving complete.
     */
    public PurchaseOrder closePurchaseOrder(String poKey) {
        PurchaseOrder po = purchaseOrderRepository.findByKey(poKey)
            .orElseThrow(() -> new IllegalArgumentException("PO not found: " + poKey));

        po.close();
        return purchaseOrderRepository.save(po);
    }

    /**
     * Record receipt against PO detail.
     */
    public void recordReceipt(String poKey, String poDetailKey, RecordReceiptRequest request) {
        PurchaseOrder po = purchaseOrderRepository.findByKey(poKey)
            .orElseThrow(() -> new IllegalArgumentException("PO not found: " + poKey));

        po.recordReceipt(poDetailKey, request.getQuantity(), request.getReceiptKey());
        purchaseOrderRepository.save(po);
    }

    private void validateCreateRequest(CreatePoRequest request) {
        if (request.getStorerKey() == null || request.getStorerKey().isBlank()) {
            throw new IllegalArgumentException("Storer key is required");
        }
        if (request.getVendorKey() == null || request.getVendorKey().isBlank()) {
            throw new IllegalArgumentException("Vendor key is required");
        }
    }

    private PurchaseOrderDetail createDetailFromRequest(CreatePoDetailRequest request) {
        return PurchaseOrderDetail.builder()
            .skuKey(request.getSkuKey())
            .expectedQty(request.getExpectedQty())
            .uom(request.getUom())
            .packKey(request.getPackKey())
            .lot(request.getLot())
            .expiryDate(request.getExpiryDate())
            .unitCost(request.getUnitCost())
            .lineNumber(request.getLineNumber())
            .build();
    }
}
