package com.maersk.wms.inbound.domain.document_service.repository;

import com.maersk.wms.inbound.domain.document_service.PoStatus;
import com.maersk.wms.inbound.domain.document_service.PurchaseOrder;
import com.maersk.wms.inbound.shared.kernel.identifiers.StorerKey;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for PurchaseOrder in Document subdomain.
 */
public interface PurchaseOrderRepository {
    Optional<PurchaseOrder> findByKey(String poKey);
    Optional<PurchaseOrder> findByExternalKey(String externalKey);
    List<PurchaseOrder> findByStorerAndStatus(StorerKey storerKey, PoStatus status);
    List<PurchaseOrder> findByVendor(String vendorKey);
    List<PurchaseOrder> findOpenPOs(StorerKey storerKey);
    List<PurchaseOrder> findExpectedInDateRange(LocalDateTime from, LocalDateTime to);
    PurchaseOrder save(PurchaseOrder po);
    void delete(String poKey);
    boolean exists(String poKey);
}
