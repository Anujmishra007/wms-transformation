package com.maersk.wms.outbound.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Shipment detail entity representing items in a shipment.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentDetail {

    private String shipmentKey;
    private String shipmentLineNumber;
    private String orderKey;
    private String orderLineNumber;

    private String sku;
    private String skuDescription;
    private String lot;
    private String lpn;

    private BigDecimal qtyShipped;
    private String uom;

    private BigDecimal weight;
    private BigDecimal volume;

    private String cartonId;
    private String palletId;

    private String addWho;
    private LocalDateTime addDate;
}
