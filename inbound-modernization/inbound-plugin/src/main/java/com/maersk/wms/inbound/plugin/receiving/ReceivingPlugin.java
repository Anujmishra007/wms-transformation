package com.maersk.wms.inbound.plugin.receiving;

import com.maersk.wms.inbound.domain.receiving.Receipt;
import com.maersk.wms.inbound.domain.receiving.ReceiptDetail;

/**
 * Plugin interface for Receiving subdomain customization.
 *
 * Allows client-specific customization of receiving behavior.
 *
 * Legacy SP Reference: Extension SPs like rdt_Receiving_SP01-xx
 */
public interface ReceivingPlugin {

    /**
     * Called before creating a receipt.
     * Can modify request or throw exception to prevent creation.
     */
    default void beforeCreateReceipt(ReceivingPluginContext context) {}

    /**
     * Called after a receipt is created.
     */
    default void afterCreateReceipt(Receipt receipt, ReceivingPluginContext context) {}

    /**
     * Called before receiving a line item.
     * Can modify the receive request.
     */
    default void beforeReceiveItem(ReceiptDetail detail, ReceivingPluginContext context) {}

    /**
     * Called after receiving a line item.
     * Can trigger additional processing (e.g., label printing).
     */
    default void afterReceiveItem(ReceiptDetail detail, ReceivingPluginContext context) {}

    /**
     * Called to validate a receive operation.
     * Return false to reject the receive.
     */
    default boolean validateReceive(ReceiptDetail detail, ReceivingPluginContext context) {
        return true;
    }

    /**
     * Called before completing a receipt.
     */
    default void beforeCompleteReceipt(Receipt receipt, ReceivingPluginContext context) {}

    /**
     * Called after completing a receipt.
     */
    default void afterCompleteReceipt(Receipt receipt, ReceivingPluginContext context) {}

    /**
     * Get plugin priority (higher = runs first).
     */
    default int getPriority() {
        return 0;
    }

    /**
     * Check if this plugin should run for the given context.
     */
    default boolean appliesTo(ReceivingPluginContext context) {
        return true;
    }
}
