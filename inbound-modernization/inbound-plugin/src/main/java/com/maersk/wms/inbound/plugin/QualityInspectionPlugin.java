package com.maersk.wms.inbound.plugin;

import com.maersk.wms.inbound.domain.ReceiptDetail;

/**
 * Plugin interface for quality inspection operations.
 * Allows client-specific customizations for QC processing.
 */
public interface QualityInspectionPlugin extends InboundPlugin {

    /**
     * Determine if received item requires inspection.
     */
    default boolean requiresInspection(ReceiptDetail detail, InboundPluginContext context) {
        return false;
    }

    /**
     * Get the inspection type required.
     */
    default String getInspectionType(ReceiptDetail detail, InboundPluginContext context) {
        return "STANDARD";
    }

    /**
     * Get the sample size for inspection.
     */
    default int getSampleSize(ReceiptDetail detail, InboundPluginContext context) {
        return 1;
    }

    /**
     * Called before inspection starts.
     */
    default PluginResult beforeInspection(ReceiptDetail detail, InboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called after inspection is completed.
     */
    default PluginResult afterInspection(ReceiptDetail detail,
                                         boolean passed,
                                         InboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called when inspection fails - determine action.
     */
    default PluginResult onInspectionFailure(ReceiptDetail detail,
                                             String failureReason,
                                             InboundPluginContext context) {
        return PluginResult.success();
    }
}
