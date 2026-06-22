package com.maersk.wms.inbound.plugin.returns;

import com.maersk.wms.inbound.domain.operations_service.ReturnDetail;
import com.maersk.wms.inbound.domain.operations_service.ReturnType;
import com.maersk.wms.inbound.domain.operations_service.TradeReturn;
import com.maersk.wms.inbound.plugin.InboundPlugin;
import com.maersk.wms.inbound.plugin.InboundPluginContext;
import com.maersk.wms.inbound.plugin.PluginResult;
import com.maersk.wms.inbound.domain.operations_service.dto.CreateReturnRequest;
import com.maersk.wms.inbound.domain.operations_service.dto.ReceiveReturnLineRequest;

import java.util.Optional;

/**
 * Plugin interface for return receiving operations.
 * Allows client-specific customization of return receiving logic.
 *
 * Legacy SP References:
 * - rdtfnc_Return (5,547 lines) - Standard return
 * - rdtfnc_EcomReturn (6,099 lines) - E-commerce return
 * - rdtfnc_PieceReturn - Piece-level return
 * - rdtfnc_CPVReturn - CPV return
 * - rdtfnc_ReturnByTrackNo - Return by tracking number
 * - rdt_EcomReturn_CaptureDetailInfo
 * - rdt_Return_V7_CaptureInfo
 */
public interface ReturnReceivingPlugin extends InboundPlugin {

    /**
     * Called before a return is created.
     * Can validate RMA, check order eligibility, etc.
     */
    default PluginResult beforeReturnCreate(CreateReturnRequest request, InboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called after a return is created.
     * Can trigger notifications, update external systems, etc.
     */
    default PluginResult afterReturnCreate(TradeReturn tradeReturn, InboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Populate return from original shipped order.
     * Client-specific logic for pulling order/shipment data.
     *
     * Legacy Reference: WM.lsp_ASN_PopulateSOs_Wrapper, WM.lsp_ASN_PopulateSODs_Wrapper
     */
    default TradeReturn populateFromOrder(String orderKey, String storerKey, ReturnType returnType,
                                           InboundPluginContext context) {
        return null;
    }

    /**
     * Lookup return by tracking number.
     * Can check external systems, carrier APIs, etc.
     *
     * Legacy Reference: rdtfnc_ReturnByTrackNo
     */
    default Optional<TradeReturn> lookupByTrackingNumber(String trackingNumber, InboundPluginContext context) {
        return Optional.empty();
    }

    /**
     * Called before starting to receive a return.
     * Can validate RMA expiry, check warehouse capacity, etc.
     */
    default PluginResult beforeStartReceiving(TradeReturn tradeReturn, InboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called before receiving a line item.
     * Can validate SKU, check return eligibility, apply receiving rules.
     */
    default PluginResult beforeReceiveLine(TradeReturn tradeReturn, ReturnDetail detail,
                                            ReceiveReturnLineRequest request, InboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called after receiving a line item.
     * Can update inventory holds, trigger inspections, etc.
     */
    default PluginResult afterReceiveLine(TradeReturn tradeReturn, ReturnDetail detail,
                                           InboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called before completing receiving.
     * Can validate all lines received, check for variances, etc.
     */
    default PluginResult beforeCompleteReceiving(TradeReturn tradeReturn, InboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called after completing receiving.
     * Can trigger inspection workflow, update order management, etc.
     */
    default PluginResult afterCompleteReceiving(TradeReturn tradeReturn, InboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Validate return is eligible for the specified return type.
     * E.g., E-commerce returns may have different rules than trade returns.
     */
    default boolean isReturnTypeAllowed(String orderKey, ReturnType returnType, InboundPluginContext context) {
        return true;
    }

    /**
     * Get return window in days for the order.
     * Some products may have different return windows.
     */
    default int getReturnWindowDays(String orderKey, String sku, InboundPluginContext context) {
        return 30;  // Default 30-day return window
    }

    /**
     * Check if over-receiving is allowed for this return.
     */
    default boolean allowOverReceive(TradeReturn tradeReturn, String sku, InboundPluginContext context) {
        return false;
    }

    /**
     * Get default return reason for a SKU.
     * Some clients have default reasons for certain product categories.
     */
    default String getDefaultReturnReason(String sku, InboundPluginContext context) {
        return null;
    }
}
