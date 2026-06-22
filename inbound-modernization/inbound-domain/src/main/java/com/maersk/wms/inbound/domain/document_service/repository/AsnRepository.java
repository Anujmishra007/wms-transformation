package com.maersk.wms.inbound.domain.document_service.repository;

import com.maersk.wms.inbound.domain.document_service.Asn;
import com.maersk.wms.inbound.domain.document_service.AsnStatus;
import com.maersk.wms.inbound.shared.kernel.identifiers.StorerKey;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for ASN in Document subdomain.
 */
public interface AsnRepository {
    Optional<Asn> findByKey(String asnKey);
    Optional<Asn> findByExternalKey(String externalKey);
    List<Asn> findByStorerAndStatus(StorerKey storerKey, AsnStatus status);
    List<Asn> findExpectedInDateRange(LocalDateTime from, LocalDateTime to);
    List<Asn> findByCarrier(String carrierKey);
    List<Asn> findByVendor(String vendorKey);
    List<Asn> findByPoKey(String poKey);
    Asn save(Asn asn);
    void delete(String asnKey);
    boolean exists(String asnKey);
}
