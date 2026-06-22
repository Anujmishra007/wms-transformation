package com.maersk.wms.inbound.domain.operations_service.repository;

import com.maersk.wms.inbound.domain.operations_service.Receipt;
import com.maersk.wms.inbound.domain.operations_service.ReceiptStatus;
import com.maersk.wms.inbound.shared.kernel.identifiers.ReceiptKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.StorerKey;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Receipt aggregate in Operations subdomain.
 * Part of inbound-operations-service.
 */
public interface ReceiptRepository {

    Optional<Receipt> findByKey(ReceiptKey receiptKey);

    Optional<Receipt> findByExternalKey(String externalReceiptKey);

    List<Receipt> findByStorerKey(StorerKey storerKey);

    List<Receipt> findByStatus(ReceiptStatus status);

    List<Receipt> findByStorerAndStatus(StorerKey storerKey, ReceiptStatus status);

    List<Receipt> findByPoKey(String poKey);

    List<Receipt> findByAsnKey(String asnKey);

    List<Receipt> findByDateRange(LocalDate from, LocalDate to);

    List<Receipt> findActiveByStorer(StorerKey storerKey);

    List<Receipt> findPendingPutaway();

    Receipt save(Receipt receipt);

    void delete(ReceiptKey receiptKey);

    boolean exists(ReceiptKey receiptKey);

    long countByStatus(ReceiptStatus status);

    long countByStorerAndStatus(StorerKey storerKey, ReceiptStatus status);
}
