package com.maersk.wms.printing.acl.outbound;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Anti-Corruption Layer facade for Outbound Service.
 * Provides shipment and order data for shipping label generation.
 */
public interface OutboundFacade {

    /**
     * Get shipment details for shipping label generation.
     */
    Optional<ShipmentLabelData> getShipmentDetails(String shipmentKey, String warehouseKey);

    /**
     * Get order details for shipping label generation.
     */
    Optional<OrderLabelData> getOrderDetails(String orderKey, String warehouseKey);

    /**
     * Get carton details for carton label generation.
     */
    Optional<CartonLabelData> getCartonDetails(String cartonId, String warehouseKey);

    /**
     * Get pallet details for pallet label generation.
     */
    Optional<PalletLabelData> getPalletDetails(String palletId, String warehouseKey);

    /**
     * Get wave details for wave-level labels.
     */
    Optional<WaveLabelData> getWaveDetails(String waveKey, String warehouseKey);

    /**
     * Get pick details for pick labels.
     */
    Optional<PickLabelData> getPickDetails(String pickKey, String warehouseKey);

    /**
     * Get all cartons for a shipment.
     */
    List<CartonLabelData> getCartonsForShipment(String shipmentKey, String warehouseKey);

    // DTOs for outbound data
    record ShipmentLabelData(
            String shipmentKey,
            String shipmentNumber,
            String orderKey,
            String carrierCode,
            String carrierName,
            String serviceType,
            String trackingNumber,
            AddressData shipToAddress,
            AddressData shipFromAddress,
            double weight,
            String weightUom,
            int cartonCount,
            Map<String, String> carrierAttributes
    ) {}

    record OrderLabelData(
            String orderKey,
            String orderNumber,
            String externalOrderNumber,
            String storerKey,
            String customerName,
            AddressData shipToAddress,
            String orderType,
            int lineCount,
            Map<String, String> attributes
    ) {}

    record CartonLabelData(
            String cartonId,
            String cartonNumber,
            String shipmentKey,
            String trackingNumber,
            double weight,
            String weightUom,
            double length,
            double width,
            double height,
            String dimensionUom,
            int sequenceNumber,
            int totalCartons,
            Map<String, String> attributes
    ) {}

    record PalletLabelData(
            String palletId,
            String palletNumber,
            String shipmentKey,
            int cartonCount,
            double weight,
            String weightUom,
            String ssccCode,
            Map<String, String> attributes
    ) {}

    record WaveLabelData(
            String waveKey,
            String waveNumber,
            int orderCount,
            int lineCount,
            int totalUnits,
            Map<String, String> attributes
    ) {}

    record PickLabelData(
            String pickKey,
            String orderKey,
            String skuCode,
            String locationCode,
            double quantity,
            String uom,
            int pickSequence,
            Map<String, String> attributes
    ) {}

    record AddressData(
            String name,
            String company,
            String address1,
            String address2,
            String address3,
            String city,
            String state,
            String postalCode,
            String country,
            String phone,
            String email
    ) {}
}
