package com.maersk.wms.outbound.domain.order_service.repository;

import com.maersk.wms.outbound.domain.order_service.model.Order;
import com.maersk.wms.outbound.domain.order_service.model.OrderStatus;
import com.maersk.wms.outbound.shared.kernel.identifiers.OrderKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.StorerKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.WaveKey;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Order persistence.
 */
public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findByKey(OrderKey orderKey);

    Optional<Order> findByExternalKey(StorerKey storerKey, String externalOrderKey);

    List<Order> findByStorerAndStatus(StorerKey storerKey, OrderStatus status);

    List<Order> findByWave(WaveKey waveKey);

    List<Order> findReadyForAllocation(StorerKey storerKey);

    void delete(OrderKey orderKey);
}
