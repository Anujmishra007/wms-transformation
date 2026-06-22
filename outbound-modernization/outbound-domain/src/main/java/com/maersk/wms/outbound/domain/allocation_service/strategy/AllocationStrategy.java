package com.maersk.wms.outbound.domain.allocation_service.strategy;

import com.maersk.wms.outbound.domain.order_service.model.OrderDetail;
import java.util.List;

/**
 * Interface for allocation strategy implementations.
 * Part of Inventory Allocation Service - Strategies module.
 */
public interface AllocationStrategy {

    /**
     * Returns the strategy identifier.
     */
    String getStrategyId();

    /**
     * Returns the strategy name.
     */
    String getStrategyName();

    /**
     * Allocates inventory for the given order details.
     *
     * @param orderDetails the order details to allocate
     * @param context the allocation context
     * @return the allocation result
     */
    AllocationResult allocate(List<OrderDetail> orderDetails, AllocationContext context);

    /**
     * Checks if this strategy supports the given allocation context.
     */
    boolean supports(AllocationContext context);

    /**
     * Returns the priority of this strategy (lower = higher priority).
     */
    int getPriority();
}
