package com.maersk.wms.inbound.domain.operations_service.repository;

import com.maersk.wms.inbound.domain.operations_service.Crossdock;
import com.maersk.wms.inbound.domain.operations_service.CrossdockStatus;
import com.maersk.wms.inbound.domain.operations_service.CrossdockType;
import com.maersk.wms.inbound.shared.kernel.identifiers.LpnKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.ReceiptKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.SkuKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.StorerKey;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Crossdock aggregate in Operations subdomain.
 * Part of inbound-operations-service.
 */
public interface CrossdockRepository {

    Optional<Crossdock> findByKey(String crossdockKey);

    List<Crossdock> findByReceiptKey(ReceiptKey receiptKey);

    List<Crossdock> findByStorerKey(StorerKey storerKey);

    List<Crossdock> findByStatus(CrossdockStatus status);

    List<Crossdock> findByType(CrossdockType type);

    List<Crossdock> findByStorerAndStatus(StorerKey storerKey, CrossdockStatus status);

    List<Crossdock> findByOrderKey(String orderKey);

    List<Crossdock> findByWaveKey(String waveKey);

    List<Crossdock> findBySkuKey(SkuKey skuKey);

    List<Crossdock> findByInboundLpn(LpnKey lpnKey);

    List<Crossdock> findByShipmentKey(String shipmentKey);

    List<Crossdock> findByLoadKey(String loadKey);

    List<Crossdock> findPendingAllocation();

    List<Crossdock> findReadyToPick();

    List<Crossdock> findReadyToShip();

    List<Crossdock> findOpportunistic();

    List<Crossdock> findPlanned();

    List<Crossdock> findByDateRange(LocalDate from, LocalDate to);

    List<Crossdock> findByPriority(int minPriority);

    Crossdock save(Crossdock crossdock);

    void delete(String crossdockKey);

    boolean exists(String crossdockKey);

    long countByStatus(CrossdockStatus status);

    long countByStorerAndStatus(StorerKey storerKey, CrossdockStatus status);

    long countByType(CrossdockType type);
}
