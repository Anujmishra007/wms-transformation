package com.maersk.wms.outbound.domain.shipping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Commercial Bill of Lading (CBOL) entity - represents individual packages/cartons.
 * Maps to CBOL table in the legacy system.
 *
 * Legacy SP References:
 * - WM.lsp_CBOL_PopulateMBOL_Wrapper
 * - WM.lsp_CBOLMarkShip
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommercialBillOfLading {

    private String cbolKey;
    private String externalCbolKey;
    private String mbolKey;
    private String storerKey;
    private String orderKey;
    private String waveKey;

    private CbolStatus status;

    private String trackingNumber;
    private String ssccBarcode;

    private String carrierKey;
    private String carrierCode;
    private String carrierServiceCode;

    private String packageType;  // CARTON, PALLET, ENVELOPE
    private BigDecimal weight;
    private String weightUom;
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;
    private String dimensionUom;

    private String shipToName;
    private String shipToAddress1;
    private String shipToAddress2;
    private String shipToCity;
    private String shipToState;
    private String shipToZip;
    private String shipToCountry;

    private BigDecimal freightCharge;
    private BigDecimal declaredValue;
    private String currency;

    private String labelUrl;
    private String labelFormat;
    private LocalDateTime labelGeneratedAt;

    private LocalDateTime shipDate;
    private LocalDateTime deliveryDate;

    // User-defined fields
    private String userDefine01;
    private String userDefine02;
    private String userDefine03;

    private String addWho;
    private LocalDateTime addDate;
    private String editWho;
    private LocalDateTime editDate;

    @Builder.Default
    private List<CbolDetail> details = new ArrayList<>();

    /**
     * Check if CBOL has a valid label.
     */
    public boolean hasLabel() {
        return trackingNumber != null && !trackingNumber.isEmpty();
    }
}
