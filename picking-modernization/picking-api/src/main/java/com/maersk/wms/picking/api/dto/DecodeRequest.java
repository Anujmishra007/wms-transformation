package com.maersk.wms.picking.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DecodeRequest {
    @NotBlank(message = "Barcode is required")
    private String barcode;

    @NotBlank(message = "Expected type is required")
    private String expectedType;

    private String taskId;
    private String expectedLocation;
    private String expectedSku;
}
