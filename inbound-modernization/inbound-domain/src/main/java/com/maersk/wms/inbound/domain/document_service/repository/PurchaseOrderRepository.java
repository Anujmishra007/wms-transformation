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
    Optional<PurchaseOrder> findByExternalPoNumber(String externalPoNumber);
    List<PurchaseOrder> findByStorerKey(StorerKey storerKey);
    List<PurchaseOrder> findByStorerAndStatus(StorerKey storerKey, PoStatus status);
    List<PurchaseOrder> findByStatus(PoStatus status);
    List<PurchaseOrder> findByVendor(String vendorKey);
    List<PurchaseOrder> findOpenPOs(StorerKey storerKey);
    List<PurchaseOrder> findPending();
    List<PurchaseOrder> findExpectedInDateRange(LocalDateTime from, LocalDateTime to);
    List<PurchaseOrder> findAll();
    PurchaseOrder save(PurchaseOrder po);
    void delete(String poKey);
    boolean exists(String poKey);
}
