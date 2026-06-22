package com.maersk.wms.inventory.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransferRequest {
    @NotBlank private String sku;
    private String lot;
    @NotBlank private String fromLocation;
    private String fromLpn;
    @NotBlank private String toLocation;
    private String toLpn;
    @NotNull @Positive private BigDecimal transferQty;
    @NotBlank private String transferType;
}
