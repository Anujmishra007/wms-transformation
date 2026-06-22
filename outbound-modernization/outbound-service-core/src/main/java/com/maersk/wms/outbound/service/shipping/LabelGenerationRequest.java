package com.maersk.wms.outbound.service.shipping;

import com.maersk.wms.outbound.domain.shipping.LabelFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request for label generation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabelGenerationRequest {

    private String cbolKey;
    private String mbolKey;
    private String orderKey;

    private String carrierCode;
    private String serviceCode;
    private String accountNumber;

    private LabelFormat format;
    private String labelSize;  // 4x6, 4x8, etc.

    // Ship-to address
    private String shipToName;
    private String shipToCompany;
    private String shipToAddress1;
    private String shipToAddress2;
    private String shipToCity;
    private String shipToState;
    private String shipToZip;
    private String shipToCountry;
    private String shipToPhone;
    private String shipToEmail;

    // Ship-from address
    private String shipFromName;
    private String shipFromCompany;
    private String shipFromAddress1;
    private String shipFromAddress2;
    private String shipFromCity;
    private String shipFromState;
    private String shipFromZip;
    private String shipFromCountry;
    private String shipFromPhone;

    // Package dimensions
    private BigDecimal weight;
    private String weightUom;
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;
    private String dimensionUom;

    // Package info
    private String packageType;
    private String packaging;
    private int packageCount;
    private BigDecimal declaredValue;
    private String currency;

    // Options
    private boolean signatureRequired;
    private boolean saturdayDelivery;
    private boolean residentialDelivery;
    private boolean returnLabel;
    private String reference1;
    private String reference2;
    private String specialInstructions;
}
