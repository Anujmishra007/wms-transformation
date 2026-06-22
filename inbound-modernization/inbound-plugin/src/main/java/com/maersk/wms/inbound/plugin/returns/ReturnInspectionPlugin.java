package com.maersk.wms.inbound.plugin.returns;

import com.maersk.wms.inbound.domain.returns.ReturnDetail;
import com.maersk.wms.inbound.domain.returns.TradeReturn;
import com.maersk.wms.inbound.plugin.InboundPlugin;
import com.maersk.wms.inbound.plugin.InboundPluginContext;
import com.maersk.wms.inbound.plugin.PluginResult;
import com.maersk.wms.inbound.service.returns.AssignDispositionRequest;
import com.maersk.wms.inbound.service.returns.DispositionResult;
import com.maersk.wms.inbound.service.returns.InspectReturnLineRequest;

/**
 * Plugin interface for return inspection operations.
 * Allows client-specific customization of inspection and disposition logic.
 *
 * Legacy SP References:
 * - rdt_Return_V7_Inspect - Return inspection
 * - rdt_EcomReturn_Inspect - E-commerce return inspection
 * - rdt_PieceReturn_Confirm - Piece return confirmation
 * - rdt_Return_V7_AssignDisposition - Disposition assignment
 * - rdt_EcomReturn_Disposition - E-commerce disposition
 */
public interface ReturnInspectionPlugin extends InboundPlugin {

    /**
     * Called before starting inspection for a return.
     * Can check inspection prerequisites, assign inspectors, etc.
     */
    default PluginResult beforeStartInspection(TradeReturn tradeReturn, InboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called before inspecting a line item.
     * Can validate inspection criteria, check inspector permissions, etc.
     */
    default PluginResult beforeInspectLine(TradeReturn tradeReturn, ReturnDetail detail,
                                            InspectReturnLineRequest request, InboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called after inspecting a line item.
     * Can trigger auto-disposition, update quality metrics, etc.
     */
    default PluginResult afterInspectLine(TradeReturn tradeReturn, ReturnDetail detail,
                                           InboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called before assigning disposition to a line.
     * Can validate disposition is allowed for the grade/reason.
     */
    default PluginResult beforeAssignDisposition(TradeReturn tradeReturn, ReturnDetail detail,
                                                  AssignDispositionRequest request, InboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called after assigning disposition.
     * Can create putaway tasks, update inventory holds, etc.
     */
    default PluginResult afterAssignDisposition(TradeReturn tradeReturn, ReturnDetail detail,
                                                 InboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Determine disposition based on inspection results.
     * Client-specific rules for auto-disposition.
     */
    default DispositionResult determineDisposition(TradeReturn tradeReturn, ReturnDetail detail,
                                                    InboundPluginContext context) {
        return null;  // Return null to use default logic
    }

    /**
     * Called before completing inspection for a return.
     * Can validate all dispositions assigned, check approval requirements, etc.
     */
    default PluginResult beforeCompleteInspection(TradeReturn tradeReturn, InboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called after completing inspection.
     * Can trigger disposition processing, notifications, etc.
     */
    default PluginResult afterCompleteInspection(TradeReturn tradeReturn, InboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Get inspection checklist for a SKU.
     * Returns list of inspection points/criteria.
     */
    default InspectionChecklist getInspectionChecklist(String sku, InboundPluginContext context) {
        return null;
    }

    /**
     * Check if inspection is required for the return/SKU.
     * Some items may bypass inspection.
     */
    default boolean isInspectionRequired(TradeReturn tradeReturn, String sku, InboundPluginContext context) {
        return true;  // Default: inspection required
    }

    /**
     * Get allowed dispositions for a grade.
     * Some grades may only allow certain dispositions.
     */
    default java.util.List<com.maersk.wms.inbound.domain.returns.ReturnDisposition> getAllowedDispositions(
            String inspectionGrade, InboundPluginContext context) {
        return java.util.Arrays.asList(com.maersk.wms.inbound.domain.returns.ReturnDisposition.values());
    }

    /**
     * Check if disposition requires approval.
     * High-value items or certain dispositions may need manager approval.
     */
    default boolean requiresApproval(TradeReturn tradeReturn, ReturnDetail detail,
                                      com.maersk.wms.inbound.domain.returns.ReturnDisposition disposition,
                                      InboundPluginContext context) {
        return false;
    }
}
