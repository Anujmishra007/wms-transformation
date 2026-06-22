package com.maersk.wms.outbound.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Temporal workflow interface for order fulfillment.
 * Orchestrates the complete order fulfillment process from allocation to shipment.
 */
@WorkflowInterface
public interface OrderFulfillmentWorkflow {

    /**
     * Execute the order fulfillment workflow.
     */
    @WorkflowMethod
    OrderFulfillmentResult fulfill(OrderFulfillmentRequest request);
}
