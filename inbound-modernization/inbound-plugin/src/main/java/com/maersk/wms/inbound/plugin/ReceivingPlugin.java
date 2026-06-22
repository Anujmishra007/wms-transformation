package com.maersk.wms.inbound.plugin;

import com.maersk.wms.inbound.domain.operations_service.Receipt;
import com.maersk.wms.inbound.domain.operations_service.ReceiptDetail;

/**
 * Plugin interface for receiving operations.
 * Allows client-specific customizations during receipt processing.
 */
public interface ReceivingPlugin extends InboundPlugin {

    /**
     * Called before a receipt is created.
     * Can modify the receipt or reject the creation.
     */
    default PluginResult beforeReceiptCreate(Receipt receipt, InboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called after a receipt is created.
     */
    default PluginResult afterReceiptCreate(Receipt receipt, InboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called before receiving a line item.
     * Can modify quantities, lottables, or reject the receive.
     */
    default PluginResult beforeReceive(ReceiptDetail detail, InboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called after receiving a line item.
     */
    default PluginResult afterReceive(ReceiptDetail detail, InboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called before a receipt is closed.
     * Can perform final validations or reject closing.
     */
    default PluginResult beforeReceiptClose(Receipt receipt, InboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called after a receipt is closed.
     */
    default PluginResult afterReceiptClose(Receipt receipt, InboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Validate lot attributes for receiving.
     */
    default PluginResult validateLotAttributes(ReceiptDetail detail, InboundPluginContext context) {
        return PluginResult.success();
    }
}
