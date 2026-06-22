package com.maersk.wms.inbound.service.document_service;

import com.maersk.wms.inbound.domain.document_service.Asn;
import com.maersk.wms.inbound.domain.document_service.AsnDetail;
import com.maersk.wms.inbound.domain.document_service.AsnStatus;
import com.maersk.wms.inbound.domain.document_service.repository.AsnRepository;
import com.maersk.wms.inbound.service.document_service.dto.*;
import com.maersk.wms.inbound.shared.kernel.identifiers.StorerKey;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Service for Advanced Shipping Notice (ASN) management.
 * Part of inbound-service subdomain (document/).
 *
 * Responsibilities:
 * - Create ASN from EDI/manual entry
 * - Process ASN arrival
 * - Manage ASN status lifecycle
 * - Validate ASN against PO
 */
@Service
@Transactional
public class AsnService {

    private final AsnRepository asnRepository;
    private final PurchaseOrderService purchaseOrderService;

    public AsnService(AsnRepository asnRepository, PurchaseOrderService purchaseOrderService) {
        this.asnRepository = asnRepository;
        this.purchaseOrderService = purchaseOrderService;
    }

    /**
     * Create a new ASN.
     */
    public Asn createAsn(CreateAsnRequest request) {
        // Validate request
        validateCreateRequest(request);

        // Build ASN from request
        Asn asn = Asn.builder()
            .storerKey(new StorerKey(request.getStorerKey()))
            .externalAsnNumber(request.getExternalAsnNumber())
            .asnType(request.getAsnType())
            .poKey(request.getPoKey())
            .vendorKey(request.getVendorKey())
            .carrierCode(request.getCarrierCode())
            .carrierName(request.getCarrierName())
            .trailerNumber(request.getTrailerNumber())
            .sealNumber(request.getSealNumber())
            .billOfLading(request.getBillOfLading())
            .proNumber(request.getProNumber())
            .expectedDate(request.getExpectedDate())
            .shipDate(request.getShipDate())
            .notes(request.getNotes())
            .build();

        // Add details
        if (request.getDetails() != null) {
            for (CreateAsnDetailRequest detailReq : request.getDetails()) {
                AsnDetail detail = createDetailFromRequest(detailReq);
                asn.addDetail(detail);
            }
        }

        return asnRepository.save(asn);
    }

    /**
     * Get ASN by key.
     */
    @Transactional(readOnly = true)
    public Optional<Asn> getAsn(String asnKey) {
        return asnRepository.findByKey(asnKey);
    }

    /**
     * Get ASN by external number.
     */
    @Transactional(readOnly = true)
    public Optional<Asn> getByExternalNumber(String externalAsnNumber) {
        return asnRepository.findByExternalAsnNumber(externalAsnNumber);
    }

    /**
     * Get all ASNs for a storer.
     */
    @Transactional(readOnly = true)
    public List<Asn> getByStorer(String storerKey) {
        return asnRepository.findByStorerKey(new StorerKey(storerKey));
    }

    /**
     * Get ASNs by PO.
     */
    @Transactional(readOnly = true)
    public List<Asn> getByPoKey(String poKey) {
        return asnRepository.findByPoKey(poKey);
    }

    /**
     * Get ASNs by status.
     */
    @Transactional(readOnly = true)
    public List<Asn> getByStatus(AsnStatus status) {
        return asnRepository.findByStatus(status);
    }

    /**
     * Get expected ASNs.
     */
    @Transactional(readOnly = true)
    public List<Asn> getExpectedAsns() {
        return asnRepository.findExpected();
    }

    /**
     * Record ASN arrival at dock door.
     */
    public Asn recordArrival(String asnKey, AsnArrivalRequest request) {
        Asn asn = asnRepository.findByKey(asnKey)
            .orElseThrow(() -> new IllegalArgumentException("ASN not found: " + asnKey));

        asn.recordArrival(request.getDockDoor(), request.getArrivalTime() != null
            ? request.getArrivalTime() : Instant.now());

        // Update trailer info if provided
        if (request.getTrailerNumber() != null) {
            asn.setTrailerNumber(request.getTrailerNumber());
        }
        if (request.getSealNumber() != null) {
            asn.setSealNumber(request.getSealNumber());
        }

        return asnRepository.save(asn);
    }

    /**
     * Start receiving process for ASN.
     */
    public Asn startReceiving(String asnKey) {
        Asn asn = asnRepository.findByKey(asnKey)
            .orElseThrow(() -> new IllegalArgumentException("ASN not found: " + asnKey));

        asn.startReceiving();
        return asnRepository.save(asn);
    }

    /**
     * Complete receiving for ASN.
     */
    public Asn completeReceiving(String asnKey, String receiptKey) {
        Asn asn = asnRepository.findByKey(asnKey)
            .orElseThrow(() -> new IllegalArgumentException("ASN not found: " + asnKey));

        asn.completeReceiving(receiptKey);
        return asnRepository.save(asn);
    }

    /**
     * Close ASN.
     */
    public Asn closeAsn(String asnKey) {
        Asn asn = asnRepository.findByKey(asnKey)
            .orElseThrow(() -> new IllegalArgumentException("ASN not found: " + asnKey));

        asn.close();
        return asnRepository.save(asn);
    }

    /**
     * Cancel ASN.
     */
    public Asn cancelAsn(String asnKey, String reason) {
        Asn asn = asnRepository.findByKey(asnKey)
            .orElseThrow(() -> new IllegalArgumentException("ASN not found: " + asnKey));

        asn.cancel(reason);
        return asnRepository.save(asn);
    }

    /**
     * Add detail line to existing ASN.
     */
    public Asn addDetail(String asnKey, CreateAsnDetailRequest request) {
        Asn asn = asnRepository.findByKey(asnKey)
            .orElseThrow(() -> new IllegalArgumentException("ASN not found: " + asnKey));

        if (!asn.canBeModified()) {
            throw new IllegalStateException("ASN cannot be modified in status: " + asn.getStatus());
        }

        AsnDetail detail = createDetailFromRequest(request);
        asn.addDetail(detail);

        return asnRepository.save(asn);
    }

    private void validateCreateRequest(CreateAsnRequest request) {
        if (request.getStorerKey() == null || request.getStorerKey().isBlank()) {
            throw new IllegalArgumentException("Storer key is required");
        }
    }

    private AsnDetail createDetailFromRequest(CreateAsnDetailRequest request) {
        return AsnDetail.builder()
            .skuKey(request.getSkuKey())
            .expectedQty(request.getExpectedQty())
            .uom(request.getUom())
            .packKey(request.getPackKey())
            .lot(request.getLot())
            .expiryDate(request.getExpiryDate())
            .poKey(request.getPoKey())
            .poDetailKey(request.getPoDetailKey())
            .lineNumber(request.getLineNumber())
            .build();
    }
}
