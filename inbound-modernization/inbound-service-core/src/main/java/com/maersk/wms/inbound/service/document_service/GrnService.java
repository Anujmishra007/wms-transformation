package com.maersk.wms.inbound.service.document_service;

import com.maersk.wms.inbound.domain.document_service.Grn;
import com.maersk.wms.inbound.domain.document_service.GrnDetail;
import com.maersk.wms.inbound.domain.document_service.GrnStatus;
import com.maersk.wms.inbound.domain.document_service.repository.GrnRepository;
import com.maersk.wms.inbound.service.document_service.dto.*;
import com.maersk.wms.inbound.shared.kernel.identifiers.StorerKey;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for Goods Receipt Note (GRN) management.
 * Part of inbound-service subdomain (document/).
 *
 * Responsibilities:
 * - Generate GRN after receiving completion
 * - Manage GRN approval workflow
 * - Post GRN to ERP
 * - Print GRN documents
 */
@Service
@Transactional
public class GrnService {

    private final GrnRepository grnRepository;

    public GrnService(GrnRepository grnRepository) {
        this.grnRepository = grnRepository;
    }

    /**
     * Generate GRN for completed receipt.
     */
    public Grn generateGrn(GenerateGrnRequest request) {
        validateCreateRequest(request);

        // Check if GRN already exists for this receipt
        Optional<Grn> existing = grnRepository.findByReceiptKey(request.getReceiptKey());
        if (existing.isPresent()) {
            throw new IllegalStateException("GRN already exists for receipt: " + request.getReceiptKey());
        }

        Grn grn = Grn.builder()
            .storerKey(new StorerKey(request.getStorerKey()))
            .receiptKey(request.getReceiptKey())
            .poKey(request.getPoKey())
            .asnKey(request.getAsnKey())
            .vendorKey(request.getVendorKey())
            .vendorInvoice(request.getVendorInvoice())
            .deliveryNote(request.getDeliveryNote())
            .generatedBy(request.getGeneratedBy())
            .notes(request.getNotes())
            .build();

        // Add details
        if (request.getDetails() != null) {
            for (GenerateGrnDetailRequest detailReq : request.getDetails()) {
                GrnDetail detail = createDetailFromRequest(detailReq);
                grn.addDetail(detail);
            }
        }

        grn.calculateTotals();
        return grnRepository.save(grn);
    }

    /**
     * Get GRN by key.
     */
    @Transactional(readOnly = true)
    public Optional<Grn> getGrn(String grnKey) {
        return grnRepository.findByKey(grnKey);
    }

    /**
     * Get GRN by GRN number.
     */
    @Transactional(readOnly = true)
    public Optional<Grn> getByGrnNumber(String grnNumber) {
        return grnRepository.findByGrnNumber(grnNumber);
    }

    /**
     * Get GRN by receipt.
     */
    @Transactional(readOnly = true)
    public Optional<Grn> getByReceipt(String receiptKey) {
        return grnRepository.findByReceiptKey(receiptKey);
    }

    /**
     * Get GRNs by storer.
     */
    @Transactional(readOnly = true)
    public List<Grn> getByStorer(String storerKey) {
        return grnRepository.findByStorerKey(new StorerKey(storerKey));
    }

    /**
     * Get GRNs by status.
     */
    @Transactional(readOnly = true)
    public List<Grn> getByStatus(GrnStatus status) {
        return grnRepository.findByStatus(status);
    }

    /**
     * Get GRNs pending ERP posting.
     */
    @Transactional(readOnly = true)
    public List<Grn> getPendingErpPosting() {
        return grnRepository.findPendingErpPosting();
    }

    /**
     * Get GRNs by PO.
     */
    @Transactional(readOnly = true)
    public List<Grn> getByPoKey(String poKey) {
        return grnRepository.findByPoKey(poKey);
    }

    /**
     * Approve GRN.
     */
    public Grn approveGrn(String grnKey, String approvedBy) {
        Grn grn = grnRepository.findByKey(grnKey)
            .orElseThrow(() -> new IllegalArgumentException("GRN not found: " + grnKey));

        grn.approve(approvedBy);
        return grnRepository.save(grn);
    }

    /**
     * Post GRN to ERP.
     */
    public Grn postToErp(String grnKey, String erpReference) {
        Grn grn = grnRepository.findByKey(grnKey)
            .orElseThrow(() -> new IllegalArgumentException("GRN not found: " + grnKey));

        grn.postToErp(erpReference);
        return grnRepository.save(grn);
    }

    /**
     * Mark ERP posting as failed.
     */
    public Grn markErpPostingFailed(String grnKey, String errorMessage) {
        Grn grn = grnRepository.findByKey(grnKey)
            .orElseThrow(() -> new IllegalArgumentException("GRN not found: " + grnKey));

        grn.markErpPostingFailed(errorMessage);
        return grnRepository.save(grn);
    }

    /**
     * Finalize GRN.
     */
    public Grn finalizeGrn(String grnKey) {
        Grn grn = grnRepository.findByKey(grnKey)
            .orElseThrow(() -> new IllegalArgumentException("GRN not found: " + grnKey));

        grn.finalize();
        return grnRepository.save(grn);
    }

    /**
     * Cancel GRN.
     */
    public Grn cancelGrn(String grnKey, String reason) {
        Grn grn = grnRepository.findByKey(grnKey)
            .orElseThrow(() -> new IllegalArgumentException("GRN not found: " + grnKey));

        grn.cancel(reason);
        return grnRepository.save(grn);
    }

    /**
     * Add vendor invoice to GRN.
     */
    public Grn addVendorInvoice(String grnKey, String vendorInvoice) {
        Grn grn = grnRepository.findByKey(grnKey)
            .orElseThrow(() -> new IllegalArgumentException("GRN not found: " + grnKey));

        grn.setVendorInvoice(vendorInvoice);
        return grnRepository.save(grn);
    }

    /**
     * Print GRN document.
     */
    public GrnPrintResult printGrn(String grnKey) {
        Grn grn = grnRepository.findByKey(grnKey)
            .orElseThrow(() -> new IllegalArgumentException("GRN not found: " + grnKey));

        grn.markPrinted();
        grnRepository.save(grn);

        // Return print result (actual printing handled by infrastructure)
        GrnPrintResult result = new GrnPrintResult();
        result.setGrnKey(grnKey);
        result.setGrnNumber(grn.getGrnNumber());
        result.setSuccess(true);
        return result;
    }

    private void validateCreateRequest(GenerateGrnRequest request) {
        if (request.getStorerKey() == null || request.getStorerKey().isBlank()) {
            throw new IllegalArgumentException("Storer key is required");
        }
        if (request.getReceiptKey() == null || request.getReceiptKey().isBlank()) {
            throw new IllegalArgumentException("Receipt key is required");
        }
    }

    private GrnDetail createDetailFromRequest(GenerateGrnDetailRequest request) {
        return GrnDetail.builder()
            .skuKey(request.getSkuKey())
            .receivedQty(request.getReceivedQty())
            .acceptedQty(request.getAcceptedQty())
            .rejectedQty(request.getRejectedQty())
            .uom(request.getUom())
            .lot(request.getLot())
            .expiryDate(request.getExpiryDate())
            .unitCost(request.getUnitCost())
            .lineNumber(request.getLineNumber())
            .poDetailKey(request.getPoDetailKey())
            .receiptDetailKey(request.getReceiptDetailKey())
            .build();
    }
}
