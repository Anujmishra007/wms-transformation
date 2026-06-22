package com.maersk.wms.outbound.domain.repository;

import com.maersk.wms.outbound.domain.Order;
import com.maersk.wms.outbound.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Order entity.
 */
public interface OrderRepository {

    Optional<Order> findByKey(String orderKey);

    Optional<Order> findByOrderKey(String orderKey);

    Optional<Order> findByExternalKey(String externalOrderKey, String storerKey);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByStorerKey(String storerKey);

    List<Order> findByWaveKey(String waveKey);

    List<Order> findOrdersReadyForAllocation(String storerKey);

    List<Order> findOrdersReadyForWaving(String storerKey);

    List<Order> findByShipByDateRange(LocalDateTime fromDate, LocalDateTime toDate);

    Order save(Order order);

    void delete(String orderKey);

    String generateOrderKey();
}
