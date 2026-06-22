package com.maersk.wms.inbound.domain.document_service.repository;

import com.maersk.wms.inbound.domain.document_service.Grn;
import com.maersk.wms.inbound.domain.document_service.GrnStatus;
import com.maersk.wms.inbound.shared.kernel.identifiers.StorerKey;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for GRN in Document subdomain.
 */
public interface GrnRepository {
    Optional<Grn> findByKey(String grnKey);
    Optional<Grn> findByGrnNumber(String grnNumber);
    Optional<Grn> findByReceiptKey(String receiptKey);
    List<Grn> findByStorerAndStatus(StorerKey storerKey, GrnStatus status);
    List<Grn> findPendingApproval(StorerKey storerKey);
    List<Grn> findNotPostedToErp(StorerKey storerKey);
    List<Grn> findByDateRange(LocalDateTime from, LocalDateTime to);
    Grn save(Grn grn);
    void delete(String grnKey);
    boolean exists(String grnKey);
}
