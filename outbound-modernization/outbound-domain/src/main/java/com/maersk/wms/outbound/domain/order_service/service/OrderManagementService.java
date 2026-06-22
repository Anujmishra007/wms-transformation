package com.maersk.wms.outbound.domain.order_service.service;

import com.maersk.wms.outbound.domain.order_service.model.Order;
import com.maersk.wms.outbound.domain.order_service.model.OrderStatus;
import com.maersk.wms.outbound.shared.kernel.identifiers.OrderKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.StorerKey;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for Order Management.
 * Part of Order Service bounded context.
 */
public interface OrderManagementService {

    /**
     * Creates a new order.
     */
    Order createOrder(CreateOrderCommand command);

    /**
     * Gets an order by key.
     */
    Optional<Order> getOrder(OrderKey orderKey);

    /**
     * Finds orders by storer and status.
     */
    List<Order> findOrders(StorerKey storerKey, OrderStatus status);

    /**
     * Releases an order for allocation.
     */
    Order releaseOrder(OrderKey orderKey, String userId);

    /**
     * Puts an order on hold.
     */
    Order holdOrder(OrderKey orderKey, String holdReason, String userId);

    /**
     * Cancels an order.
     */
    Order cancelOrder(OrderKey orderKey, String cancelReason, String userId);

    /**
     * Updates order status.
     */
    Order updateOrderStatus(OrderKey orderKey, OrderStatus newStatus, String userId);

    /**
     * Command for creating an order.
     */
    record CreateOrderCommand(
            StorerKey storerKey,
            String externalOrderKey,
            String consigneeKey,
            String shipToName,
            String shipToAddress1,
            String shipToCity,
            String shipToState,
            String shipToZip,
            String shipToCountry,
            String carrierCode,
            String userId
    ) {}
}
