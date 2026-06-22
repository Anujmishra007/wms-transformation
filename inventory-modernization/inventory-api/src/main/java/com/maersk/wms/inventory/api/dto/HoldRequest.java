package com.maersk.wms.inventory.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class HoldRequest {
    @NotBlank private String holdCode;
    @NotBlank private String scope;
    private String sku;
    private String lot;
    private String location;
    private String lpn;
    @NotBlank private String reasonCode;
    private String comments;
}
