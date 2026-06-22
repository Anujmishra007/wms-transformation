package com.maersk.wms.inbound.plugin;

import com.maersk.wms.inbound.domain.Asn;
import com.maersk.wms.inbound.domain.AsnDetail;

/**
 * Plugin interface for ASN operations.
 * Allows client-specific customizations during ASN processing.
 */
public interface AsnPlugin extends InboundPlugin {

    /**
     * Called before an ASN is created/imported.
     * Can modify the ASN or reject the creation.
     */
    default PluginResult beforeAsnCreate(Asn asn, InboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called after an ASN is created.
     */
    default PluginResult afterAsnCreate(Asn asn, InboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called when ASN arrives (check-in).
     */
    default PluginResult onAsnCheckIn(Asn asn, InboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called before ASN line validation.
     */
    default PluginResult validateAsnLine(AsnDetail detail, InboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called before ASN is converted to receipt.
     */
    default PluginResult beforeAsnToReceipt(Asn asn, InboundPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called after ASN is fully processed.
     */
    default PluginResult afterAsnComplete(Asn asn, InboundPluginContext context) {
        return PluginResult.success();
    }
}
