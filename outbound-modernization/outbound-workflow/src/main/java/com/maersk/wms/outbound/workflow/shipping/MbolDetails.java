package com.maersk.wms.outbound.workflow.shipping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * MBOL details for query response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MbolDetails {

    private String mbolKey;
    private String waveKey;
    private String loadKey;
    private String storerKey;
    private String status;

    // Carrier info
    private String carrierCode;
    private String carrierName;
    private String serviceCode;
    private String serviceName;
    private String trackingNumber;
    private String proNumber;

    // Ship-to info
    private String shipToName;
    private String shipToAddress1;
    private String shipToAddress2;
    private String shipToCity;
    private String shipToState;
    private String shipToZip;
    private String shipToCountry;

    // Totals
    private int totalOrders;
    private int totalCartons;
    private int totalPallets;
    private BigDecimal totalWeight;
    private String weightUom;

    // Financial
    private BigDecimal freightCharge;
    private String currency;

    // Dates
    private LocalDateTime expectedShipDate;
    private LocalDateTime actualShipDate;
    private LocalDateTime estimatedDeliveryDate;

    // Orders
    private List<String> orderKeys;

    // Manifest
    private String manifestKey;
}
