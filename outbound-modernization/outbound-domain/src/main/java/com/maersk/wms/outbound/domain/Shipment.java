package com.maersk.wms.outbound.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Shipment entity representing a physical shipment.
 * Maps to SHIPMENT table in the legacy system.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Shipment {

    private String shipmentKey;
    private String externalShipmentKey;
    private String storerKey;

    private ShipmentStatus status;
    private ShipmentType type;

    private String carrierCode;
    private String carrierName;
    private String serviceLevel;
    private String trackingNumber;
    private String proNumber;

    private String shipToName;
    private String shipToAddress1;
    private String shipToAddress2;
    private String shipToCity;
    private String shipToState;
    private String shipToZip;
    private String shipToCountry;

    private String loadKey;
    private String door;
    private String trailerNumber;
    private String sealNumber;

    private LocalDateTime expectedShipDate;
    private LocalDateTime actualShipDate;
    private LocalDateTime deliveryDate;

    private int totalCartons;
    private int totalPallets;
    private BigDecimal totalWeight;
    private BigDecimal totalVolume;
    private BigDecimal freightCharge;
    private String currency;

    private String manifestKey;
    private String bolNumber;

    private String addWho;
    private LocalDateTime addDate;
    private String editWho;
    private LocalDateTime editDate;

    @Builder.Default
    private List<String> orderKeys = new ArrayList<>();

    @Builder.Default
    private List<ShipmentDetail> details = new ArrayList<>();

    /**
     * Check if shipment can be manifested.
     */
    public boolean canManifest() {
        return status == ShipmentStatus.PACKED || status == ShipmentStatus.STAGED;
    }

    /**
     * Check if shipment can ship.
     */
    public boolean canShip() {
        return status == ShipmentStatus.MANIFESTED || status == ShipmentStatus.LOADED;
    }
}
