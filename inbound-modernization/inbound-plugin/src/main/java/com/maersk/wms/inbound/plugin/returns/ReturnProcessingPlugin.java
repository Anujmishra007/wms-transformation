package com.maersk.wms.inbound.plugin.returns;

import com.maersk.wms.inbound.domain.returns.TradeReturn;
import com.maersk.wms.inbound.plugin.InboundPlugin;
import com.maersk.wms.inbound.plugin.InboundPluginContext;
import com.maersk.wms.inbound.plugin.PluginResult;
import com.maersk.wms.inbound.service.returns.CreditMemoResult;
import com.maersk.wms.inbound.service.returns.InventoryUpdateResult;
import com.maersk.wms.inbound.service.returns.RefundCalculationResult;

/**
 * Plugin interface for return processing and closure operations.
 * Allows client-specific customization of return closure, credit, and inventory processing.
 *
 * Legacy SP References:
 * - rdt_Return_V7_Close - Return closure
 * - rdt_EcomReturn_Close - E-commerce return closure
 * - rdt_Return_ProcessCredit - Credit processing
 * - rdt_Return_UpdateInventory - Inventory updates from returns
 * - WM.lsp_Receipt_Close_Wrapper - Receipt closure wrapper
 */
public interface ReturnProcessingPlugin extends InboundPlugin {

    /**
     * Called before closing a return.
     * Can validate dispositions, check approvals, etc.
     */
    default PluginResult beforeReturnClose(TradeReturn tradeReturn, InboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called after closing a return.
     * Can trigger inventory updates, credit processing, notifications.
     */
    default PluginResult afterReturnClose(TradeReturn tradeReturn, InboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Calculate refund amount for the return.
     * Client-specific refund calculation logic.
     */
    default RefundCalculationResult calculateRefund(TradeReturn tradeReturn, InboundPluginContext context) {
        return null;  // Return null to use default logic
    }

    /**
     * Process inventory updates for the return.
     * Create inventory transactions for restocked, refurbished, scrapped items.
     */
    default InventoryUpdateResult processInventoryUpdates(TradeReturn tradeReturn, InboundPluginContext context) {
        return null;  // Return null to use default logic
    }

    /**
     * Generate credit memo for the return.
     * Integration with financial/ERP systems.
     */
    default CreditMemoResult generateCreditMemo(TradeReturn tradeReturn, InboundPluginContext context) {
        return null;  // Return null to use default logic
    }

    /**
     * Called before canceling a return.
     * Can validate cancellation is allowed, check dependencies.
     */
    default PluginResult beforeReturnCancel(TradeReturn tradeReturn, String reason, InboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called after canceling a return.
     * Can notify customers, update external systems.
     */
    default PluginResult afterReturnCancel(TradeReturn tradeReturn, InboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Send return notification to customer.
     * E.g., refund processed, return received, etc.
     */
    default void sendCustomerNotification(TradeReturn tradeReturn, String notificationType,
                                           InboundPluginContext context) {
        // Default: no-op
    }

    /**
     * Update external order management system.
     * Sync return status with OMS/ERP.
     */
    default void syncWithOrderManagement(TradeReturn tradeReturn, InboundPluginContext context) {
        // Default: no-op
    }

    /**
     * Get restocking fee percentage for the return.
     * Some clients have different restocking fees based on reason/product.
     */
    default java.math.BigDecimal getRestockingFeePercent(TradeReturn tradeReturn, InboundPluginContext context) {
        return new java.math.BigDecimal("0.15");  // Default 15%
    }

    /**
     * Check if shipping refund is applicable.
     */
    default boolean isShippingRefundApplicable(TradeReturn tradeReturn, InboundPluginContext context) {
        // Default: refund shipping only for defective returns
        return "DEFECTIVE".equals(tradeReturn.getReturnReasonCode());
    }

    /**
     * Get putaway strategy for restocked items.
     */
    default String getRestockPutawayStrategy(TradeReturn tradeReturn, String sku, InboundPluginContext context) {
        return "RETURN_RESTOCK";  // Default putaway strategy for returns
    }
}
