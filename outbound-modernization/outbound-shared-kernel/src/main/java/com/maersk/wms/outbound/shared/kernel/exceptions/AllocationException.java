package com.maersk.wms.outbound.shared.kernel.exceptions;

/**
 * Exception for allocation-related errors.
 */
public class AllocationException extends OutboundException {

    public AllocationException(String message) {
        super("ALLOCATION_ERROR", message);
    }

    public AllocationException(String errorCode, String message) {
        super(errorCode, message);
    }

    public static AllocationException insufficientInventory(String sku, String requestedQty, String availableQty) {
        return new AllocationException("INSUFFICIENT_INVENTORY",
                String.format("Insufficient inventory for SKU %s: requested %s, available %s",
                        sku, requestedQty, availableQty));
    }

    public static AllocationException alreadyAllocated(String orderKey) {
        return new AllocationException("ALREADY_ALLOCATED",
                String.format("Order %s is already allocated", orderKey));
    }

    public static AllocationException invalidStrategy(String strategy) {
        return new AllocationException("INVALID_STRATEGY",
                String.format("Invalid allocation strategy: %s", strategy));
    }
}
