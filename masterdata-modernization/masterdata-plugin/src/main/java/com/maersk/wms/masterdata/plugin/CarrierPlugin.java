package com.maersk.wms.masterdata.plugin;

import com.maersk.wms.masterdata.domain.Carrier;

/**
 * Plugin interface for carrier operations.
 * Allows client-specific customizations for carrier master data.
 */
public interface CarrierPlugin extends MasterDataPlugin {

    /**
     * Called before carrier creation.
     */
    default PluginResult beforeCarrierCreate(Carrier carrier, MasterDataPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called after carrier creation.
     */
    default PluginResult afterCarrierCreate(Carrier carrier, MasterDataPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called before carrier update.
     */
    default PluginResult beforeCarrierUpdate(Carrier carrier, MasterDataPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called after carrier update.
     */
    default PluginResult afterCarrierUpdate(Carrier carrier, MasterDataPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Validate carrier data.
     */
    default PluginResult validateCarrier(Carrier carrier, MasterDataPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Transform/enrich carrier data before save.
     */
    default Carrier transformCarrier(Carrier carrier, MasterDataPluginContext context) {
        return carrier;
    }

    /**
     * Validate carrier service configuration.
     */
    default PluginResult validateCarrierServices(Carrier carrier, MasterDataPluginContext context) {
        return PluginResult.success();
    }
}
