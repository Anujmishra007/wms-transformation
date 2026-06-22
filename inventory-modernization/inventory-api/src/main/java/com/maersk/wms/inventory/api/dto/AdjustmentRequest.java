package com.maersk.wms.inventory.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class AdjustmentRequest {
    @NotBlank private String sku;
    @NotBlank private String location;
    private String lpn;
    private String lot;
    @NotBlank private String adjustmentType;
    private BigDecimal systemQty;
    @NotNull private BigDecimal adjustedQty;
    @NotBlank private String reasonCode;
    private String comments;
}
