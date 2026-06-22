package com.maersk.wms.outbound.domain.shipping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * CBOL Detail - items within a package.
 * Maps to CBOLDETAIL / CartonShipmentDetail tables.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CbolDetail {

    private String cbolDetailKey;
    private String cbolKey;
    private String orderKey;
    private String orderLineNumber;
    private String pickDetailKey;

    private String sku;
    private String skuDescription;
    private BigDecimal quantity;
    private String uom;

    private String lot;
    private String lotAttribute;
    private String serialNumber;

    private BigDecimal weight;
    private String weightUom;

    private String addWho;
    private LocalDateTime addDate;
}
