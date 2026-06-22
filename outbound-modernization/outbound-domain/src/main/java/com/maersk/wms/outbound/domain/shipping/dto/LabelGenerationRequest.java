package com.maersk.wms.outbound.domain.shipping.dto;

import com.maersk.wms.outbound.domain.shipping.LabelFormat;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Request DTO for label generation operations.
 */
@Data
@Builder
public class LabelGenerationRequest {
    private String shipmentKey;
    private String orderKey;
    private String lpn;
    private String cbolKey;
    private String mbolKey;
    private String carrierCode;
    private String serviceCode;
    private String labelFormat;
    private LabelFormat format;
    private int labelSize;
    private String printerName;
    private Map<String, String> customFields;

    // Address fields
    private String shipToName;
    private String shipToAddress1;
    private String shipToAddress2;
    private String shipToCity;
    private String shipToState;
    private String shipToZip;
    private String shipToCountry;

    // Package dimensions
    private BigDecimal weight;
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;
}
