package com.maersk.wms.outbound.plugin.shipping;

import com.maersk.wms.outbound.domain.Order;
import com.maersk.wms.outbound.domain.shipping.Carrier;
import com.maersk.wms.outbound.domain.shipping.MasterBillOfLading;
import com.maersk.wms.outbound.plugin.OutboundPlugin;
import com.maersk.wms.outbound.plugin.OutboundPluginContext;
import com.maersk.wms.outbound.service.shipping.CarrierChangeValidation;
import com.maersk.wms.outbound.service.shipping.CarrierSelectionResult;

import java.util.List;

/**
 * Plugin interface for carrier selection operations.
 * Allows client-specific carrier routing and selection logic.
 *
 * Legacy Reference:
 * - Carrier selection rules from StorerConfig
 * - CODELKUP carrier routing
 * - Client-specific carrier preferences
 */
public interface CarrierSelectionPlugin extends OutboundPlugin {

    /**
     * Filter available carriers for a facility.
     * Called before carrier selection to narrow down options.
     */
    default List<Carrier> filterAvailableCarriers(List<Carrier> carriers, OutboundPluginContext context) {
        return carriers;
    }

    /**
     * Select the best carrier for an order.
     * Core carrier selection logic with rate shopping support.
     *
     * @param order The order needing carrier assignment
     * @param availableCarriers Pre-filtered list of available carriers
     * @param context Plugin context with client/facility info
     * @return Selection result with carrier, service, and rate info
     */
    CarrierSelectionResult selectCarrier(Order order, List<Carrier> availableCarriers, OutboundPluginContext context);

    /**
     * Validate a carrier change request.
     * Called when user/system wants to change the carrier assignment.
     */
    default CarrierChangeValidation validateCarrierChange(
            String currentCarrierCode,
            String newCarrierCode,
            String orderKey,
            OutboundPluginContext context) {
        return CarrierChangeValidation.builder()
                .valid(true)
                .build();
    }

    /**
     * Get carrier preferences for a customer/consignee.
     * Some customers have preferred carriers.
     */
    default String getPreferredCarrier(String consigneeKey, OutboundPluginContext context) {
        return null;
    }

    /**
     * Check if carrier is allowed for destination.
     * Some carriers don't service certain regions.
     */
    default boolean isCarrierAllowedForDestination(
            String carrierCode,
            String country,
            String state,
            String postalCode,
            OutboundPluginContext context) {
        return true;
    }

    /**
     * Check if order qualifies for rate shopping.
     */
    default boolean shouldRateShop(Order order, OutboundPluginContext context) {
        return false;
    }

    /**
     * Get carrier cutoff time for same-day shipping.
     */
    default String getCarrierCutoffTime(String carrierCode, String facility, OutboundPluginContext context) {
        return "17:00";
    }

    /**
     * Select the best carrier for an MBOL (Master Bill of Lading).
     * Used for LTL/FTL shipments that consolidate multiple orders.
     *
     * @param mbol The MBOL needing carrier assignment
     * @param availableCarriers Pre-filtered list of available carriers
     * @param context Plugin context with client/facility info
     * @return Selection result with carrier, service, and rate info
     */
    default CarrierSelectionResult selectCarrierForMbol(MasterBillOfLading mbol, List<Carrier> availableCarriers,
                                                         OutboundPluginContext context) {
        return CarrierSelectionResult.builder()
                .selected(false)
                .reason("No MBOL carrier selection implemented")
                .build();
    }

    /**
     * Calculate freight rates for an MBOL.
     * Called to get carrier quotes for rate shopping.
     *
     * @param mbol The MBOL to rate
     * @param carrier The carrier to get rates from
     * @param context Plugin context
     * @return Rate calculation result
     */
    default CarrierSelectionResult calculateRates(MasterBillOfLading mbol, Carrier carrier,
                                                   OutboundPluginContext context) {
        return CarrierSelectionResult.builder()
                .selected(false)
                .reason("No rate calculation implemented")
                .build();
    }
}
