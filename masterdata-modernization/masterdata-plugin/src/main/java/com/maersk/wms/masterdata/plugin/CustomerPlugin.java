package com.maersk.wms.masterdata.plugin;

import com.maersk.wms.masterdata.domain.Customer;

/**
 * Plugin interface for customer operations.
 * Allows client-specific customizations for customer master data.
 */
public interface CustomerPlugin extends MasterDataPlugin {

    /**
     * Called before customer creation.
     */
    default PluginResult beforeCustomerCreate(Customer customer, MasterDataPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called after customer creation.
     */
    default PluginResult afterCustomerCreate(Customer customer, MasterDataPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called before customer update.
     */
    default PluginResult beforeCustomerUpdate(Customer customer, MasterDataPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Called after customer update.
     */
    default PluginResult afterCustomerUpdate(Customer customer, MasterDataPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Validate customer data.
     */
    default PluginResult validateCustomer(Customer customer, MasterDataPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Transform/enrich customer data before save.
     */
    default Customer transformCustomer(Customer customer, MasterDataPluginContext context) {
        return customer;
    }

    /**
     * Validate customer address.
     */
    default PluginResult validateAddress(Customer customer, MasterDataPluginContext context) {
        return PluginResult.success();
    }

    /**
     * Determine default carrier for customer.
     */
    default String determineDefaultCarrier(Customer customer, MasterDataPluginContext context) {
        return customer.getDefaultCarrier();
    }

    /**
     * Determine service level for customer.
     */
    default String determineServiceLevel(Customer customer, MasterDataPluginContext context) {
        return customer.getServiceLevel();
    }
}
