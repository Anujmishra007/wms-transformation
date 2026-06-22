package com.maersk.wms.outbound.plugin.shipping;

import com.maersk.wms.outbound.domain.Wave;
import com.maersk.wms.outbound.domain.shipping.MasterBillOfLading;
import com.maersk.wms.outbound.plugin.OutboundPlugin;
import com.maersk.wms.outbound.plugin.OutboundPluginContext;
import com.maersk.wms.outbound.plugin.PluginResult;
import com.maersk.wms.outbound.domain.shipping.dto.MbolValidationResult;

/**
 * Plugin interface for Master Bill of Lading operations.
 *
 * Legacy Reference:
 * - WM.lsp_WaveGenMBOL
 * - WM.lsp_MBOLPPLLoadPlan_Wrapper
 * - WM.lsp_MBOLPPLOrderType2_Wrapper
 * - nsp_BackEndValidateMBOL
 * - nsp_BackEndShipped
 */
public interface MbolPlugin extends OutboundPlugin {

    /**
     * Called before MBOL generation from wave.
     */
    default PluginResult beforeMbolGeneration(Wave wave, OutboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called after MBOL generation.
     */
    default PluginResult afterMbolGeneration(MasterBillOfLading mbol, OutboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Populate MBOL from load plan.
     * Client-specific load plan to MBOL mapping.
     */
    default void populateFromLoadPlan(MasterBillOfLading mbol, String loadKey, OutboundPluginContext context) {
        // Default: no-op
    }

    /**
     * Validate MBOL before shipping.
     */
    default MbolValidationResult validateMbol(MasterBillOfLading mbol, OutboundPluginContext context) {
        return MbolValidationResult.success(mbol.getMbolKey());
    }

    /**
     * Called before MBOL is marked as shipped.
     */
    default PluginResult beforeMbolShip(MasterBillOfLading mbol, OutboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called after MBOL is shipped.
     * Can be used for ASN generation, EDI transmission, etc.
     */
    default PluginResult afterMbolShip(MasterBillOfLading mbol, OutboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Calculate freight charges for MBOL.
     */
    default void calculateFreight(MasterBillOfLading mbol, OutboundPluginContext context) {
        // Default: no calculation
    }

    /**
     * Apply ship-to address defaulting rules.
     * Some clients consolidate ship-to for LTL/FTL shipments.
     */
    default void applyShipToDefaults(MasterBillOfLading mbol, OutboundPluginContext context) {
        // Default: no-op
    }

    /**
     * Check if MBOL can be split.
     */
    default boolean canSplitMbol(MasterBillOfLading mbol, OutboundPluginContext context) {
        return true;
    }

    /**
     * Generate BOL document.
     */
    default byte[] generateBolDocument(MasterBillOfLading mbol, String format, OutboundPluginContext context) {
        return null;
    }
}
