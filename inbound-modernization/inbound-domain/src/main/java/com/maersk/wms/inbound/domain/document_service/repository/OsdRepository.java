package com.maersk.wms.inbound.domain.document_service.repository;

import com.maersk.wms.inbound.domain.document_service.Osd;
import com.maersk.wms.inbound.domain.document_service.OsdStatus;
import com.maersk.wms.inbound.domain.document_service.OsdType;
import com.maersk.wms.inbound.shared.kernel.identifiers.StorerKey;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for OSD in Document subdomain.
 */
public interface OsdRepository {
    Optional<Osd> findByKey(String osdKey);
    List<Osd> findByStorerAndStatus(StorerKey storerKey, OsdStatus status);
    List<Osd> findByReceiptKey(String receiptKey);
    List<Osd> findByType(OsdType type);
    List<Osd> findOpenOsds(StorerKey storerKey);
    List<Osd> findByDateRange(LocalDateTime from, LocalDateTime to);
    Osd save(Osd osd);
    void delete(String osdKey);
    boolean exists(String osdKey);
}
