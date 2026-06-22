package com.maersk.wms.inbound.service.document_service;

import com.maersk.wms.inbound.domain.document_service.Osd;
import com.maersk.wms.inbound.domain.document_service.OsdDetail;
import com.maersk.wms.inbound.domain.document_service.OsdStatus;
import com.maersk.wms.inbound.domain.document_service.OsdType;
import com.maersk.wms.inbound.domain.document_service.repository.OsdRepository;
import com.maersk.wms.inbound.service.document_service.dto.*;
import com.maersk.wms.inbound.shared.kernel.identifiers.StorerKey;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service for Over/Short/Damage (OSD) report management.
 * Part of inbound-service subdomain (document/).
 *
 * Responsibilities:
 * - Create OSD reports during receiving
 * - Track discrepancies (over, short, damage)
 * - Manage claims and resolutions
 * - Report OSD metrics
 */
@Service
@Transactional
public class OsdService {

    private final OsdRepository osdRepository;

    public OsdService(OsdRepository osdRepository) {
        this.osdRepository = osdRepository;
    }

    /**
     * Create OSD report for overage.
     */
    public Osd createOverageReport(CreateOsdRequest request) {
        validateCreateRequest(request);

        Osd osd = Osd.builder()
            .storerKey(new StorerKey(request.getStorerKey()))
            .type(OsdType.OVERAGE)
            .receiptKey(request.getReceiptKey())
            .receiptDetailKey(request.getReceiptDetailKey())
            .poKey(request.getPoKey())
            .asnKey(request.getAsnKey())
            .vendorKey(request.getVendorKey())
            .carrierCode(request.getCarrierCode())
            .reportedBy(request.getReportedBy())
            .notes(request.getNotes())
            .build();

        // Add details for overage
        if (request.getDetails() != null) {
            for (CreateOsdDetailRequest detailReq : request.getDetails()) {
                OsdDetail detail = createDetailFromRequest(detailReq);
                osd.addDetail(detail);
            }
        }

        osd.calculateTotals();
        return osdRepository.save(osd);
    }

    /**
     * Create OSD report for shortage.
     */
    public Osd createShortageReport(CreateOsdRequest request) {
        validateCreateRequest(request);

        Osd osd = Osd.builder()
            .storerKey(new StorerKey(request.getStorerKey()))
            .type(OsdType.SHORTAGE)
            .receiptKey(request.getReceiptKey())
            .receiptDetailKey(request.getReceiptDetailKey())
            .poKey(request.getPoKey())
            .asnKey(request.getAsnKey())
            .vendorKey(request.getVendorKey())
            .carrierCode(request.getCarrierCode())
            .reportedBy(request.getReportedBy())
            .notes(request.getNotes())
            .build();

        if (request.getDetails() != null) {
            for (CreateOsdDetailRequest detailReq : request.getDetails()) {
                OsdDetail detail = createDetailFromRequest(detailReq);
                osd.addDetail(detail);
            }
        }

        osd.calculateTotals();
        return osdRepository.save(osd);
    }

    /**
     * Create OSD report for damage.
     */
    public Osd createDamageReport(CreateOsdRequest request) {
        validateCreateRequest(request);

        Osd osd = Osd.builder()
            .storerKey(new StorerKey(request.getStorerKey()))
            .type(OsdType.DAMAGE)
            .receiptKey(request.getReceiptKey())
            .receiptDetailKey(request.getReceiptDetailKey())
            .poKey(request.getPoKey())
            .asnKey(request.getAsnKey())
            .vendorKey(request.getVendorKey())
            .carrierCode(request.getCarrierCode())
            .reportedBy(request.getReportedBy())
            .notes(request.getNotes())
            .build();

        if (request.getDetails() != null) {
            for (CreateOsdDetailRequest detailReq : request.getDetails()) {
                OsdDetail detail = createDetailFromRequest(detailReq);
                osd.addDetail(detail);
            }
        }

        osd.calculateTotals();
        return osdRepository.save(osd);
    }

    /**
     * Get OSD by key.
     */
    @Transactional(readOnly = true)
    public Optional<Osd> getOsd(String osdKey) {
        return osdRepository.findByKey(osdKey);
    }

    /**
     * Get OSD by receipt.
     */
    @Transactional(readOnly = true)
    public List<Osd> getByReceipt(String receiptKey) {
        return osdRepository.findByReceiptKey(receiptKey);
    }

    /**
     * Get OSDs by storer.
     */
    @Transactional(readOnly = true)
    public List<Osd> getByStorer(String storerKey) {
        return osdRepository.findByStorerKey(new StorerKey(storerKey));
    }

    /**
     * Get OSDs by type.
     */
    @Transactional(readOnly = true)
    public List<Osd> getByType(OsdType type) {
        return osdRepository.findByType(type);
    }

    /**
     * Get pending OSDs.
     */
    @Transactional(readOnly = true)
    public List<Osd> getPendingOsds() {
        return osdRepository.findPending();
    }

    /**
     * Add detail to existing OSD.
     */
    public Osd addDetail(String osdKey, CreateOsdDetailRequest request) {
        Osd osd = osdRepository.findByKey(osdKey)
            .orElseThrow(() -> new IllegalArgumentException("OSD not found: " + osdKey));

        if (osd.isResolved()) {
            throw new IllegalStateException("Cannot add details to resolved OSD");
        }

        OsdDetail detail = createDetailFromRequest(request);
        osd.addDetail(detail);
        osd.calculateTotals();

        return osdRepository.save(osd);
    }

    /**
     * Submit OSD for review.
     */
    public Osd submitForReview(String osdKey) {
        Osd osd = osdRepository.findByKey(osdKey)
            .orElseThrow(() -> new IllegalArgumentException("OSD not found: " + osdKey));

        osd.submitForReview();
        return osdRepository.save(osd);
    }

    /**
     * Approve OSD and initiate claim.
     */
    public Osd approveAndInitiateClaim(String osdKey, String claimNumber, String approvedBy) {
        Osd osd = osdRepository.findByKey(osdKey)
            .orElseThrow(() -> new IllegalArgumentException("OSD not found: " + osdKey));

        osd.approve(approvedBy);
        osd.initiateClaim(claimNumber);

        return osdRepository.save(osd);
    }

    /**
     * Resolve OSD with resolution details.
     */
    public Osd resolveOsd(String osdKey, ResolveOsdRequest request) {
        Osd osd = osdRepository.findByKey(osdKey)
            .orElseThrow(() -> new IllegalArgumentException("OSD not found: " + osdKey));

        osd.resolve(request.getResolution(), request.getResolvedBy());

        if (request.getCreditAmount() != null) {
            osd.setCreditAmount(request.getCreditAmount());
        }
        if (request.getDebitAmount() != null) {
            osd.setDebitAmount(request.getDebitAmount());
        }

        return osdRepository.save(osd);
    }

    /**
     * Reject OSD.
     */
    public Osd rejectOsd(String osdKey, String reason, String rejectedBy) {
        Osd osd = osdRepository.findByKey(osdKey)
            .orElseThrow(() -> new IllegalArgumentException("OSD not found: " + osdKey));

        osd.reject(reason, rejectedBy);
        return osdRepository.save(osd);
    }

    /**
     * Get OSD summary by vendor.
     */
    @Transactional(readOnly = true)
    public OsdVendorSummary getVendorSummary(String vendorKey) {
        List<Osd> osds = osdRepository.findByVendorKey(vendorKey);
        return calculateVendorSummary(vendorKey, osds);
    }

    private void validateCreateRequest(CreateOsdRequest request) {
        if (request.getStorerKey() == null || request.getStorerKey().isBlank()) {
            throw new IllegalArgumentException("Storer key is required");
        }
        if (request.getReceiptKey() == null || request.getReceiptKey().isBlank()) {
            throw new IllegalArgumentException("Receipt key is required");
        }
    }

    private OsdDetail createDetailFromRequest(CreateOsdDetailRequest request) {
        return OsdDetail.builder()
            .skuKey(request.getSkuKey())
            .expectedQty(request.getExpectedQty())
            .actualQty(request.getActualQty())
            .varianceQty(request.getVarianceQty())
            .damageQty(request.getDamageQty())
            .damageType(request.getDamageType())
            .lot(request.getLot())
            .lpnKey(request.getLpnKey())
            .notes(request.getNotes())
            .build();
    }

    private OsdVendorSummary calculateVendorSummary(String vendorKey, List<Osd> osds) {
        OsdVendorSummary summary = new OsdVendorSummary();
        summary.setVendorKey(vendorKey);
        summary.setTotalOsds(osds.size());
        summary.setTotalOverageQty(osds.stream()
            .filter(o -> o.getType() == OsdType.OVERAGE)
            .map(Osd::getTotalOverQty)
            .reduce(BigDecimal.ZERO, BigDecimal::add));
        summary.setTotalShortageQty(osds.stream()
            .filter(o -> o.getType() == OsdType.SHORTAGE)
            .map(Osd::getTotalShortQty)
            .reduce(BigDecimal.ZERO, BigDecimal::add));
        summary.setTotalDamageQty(osds.stream()
            .filter(o -> o.getType() == OsdType.DAMAGE)
            .map(Osd::getTotalDamagedQty)
            .reduce(BigDecimal.ZERO, BigDecimal::add));
        return summary;
    }
}
