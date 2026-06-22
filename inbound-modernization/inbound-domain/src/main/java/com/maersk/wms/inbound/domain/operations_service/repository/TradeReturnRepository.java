package com.maersk.wms.inbound.domain.operations_service.repository;

import com.maersk.wms.inbound.domain.operations_service.ReturnStatus;
import com.maersk.wms.inbound.domain.operations_service.ReturnType;
import com.maersk.wms.inbound.domain.operations_service.TradeReturn;
import com.maersk.wms.inbound.shared.kernel.identifiers.StorerKey;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for TradeReturn aggregate in Operations subdomain.
 * Part of inbound-operations-service.
 */
public interface TradeReturnRepository {

    Optional<TradeReturn> findByKey(String returnKey);

    Optional<TradeReturn> findByRmaNumber(String rmaNumber);

    Optional<TradeReturn> findByExternalReference(String externalReference);

    List<TradeReturn> findByStorerKey(StorerKey storerKey);

    List<TradeReturn> findByStatus(ReturnStatus status);

    List<TradeReturn> findByType(ReturnType type);

    List<TradeReturn> findByStorerAndStatus(StorerKey storerKey, ReturnStatus status);

    List<TradeReturn> findByOriginalOrderKey(String originalOrderKey);

    List<TradeReturn> findByCustomerKey(String customerKey);

    List<TradeReturn> findByDateRange(LocalDate from, LocalDate to);

    List<TradeReturn> findPendingInspection();

    List<TradeReturn> findPendingDisposition();

    List<TradeReturn> findPendingPutaway();

    TradeReturn save(TradeReturn tradeReturn);

    void delete(String returnKey);

    boolean exists(String returnKey);

    boolean existsByRmaNumber(String rmaNumber);

    long countByStatus(ReturnStatus status);

    long countByStorerAndStatus(StorerKey storerKey, ReturnStatus status);
}
