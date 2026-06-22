package com.maersk.wms.inbound.domain.operations_service.dto;

import com.maersk.wms.inbound.domain.operations_service.ReturnType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Request DTO for creating a new return.
 */
@Data
@Builder
public class CreateReturnRequest {
    private String storerKey;
    private String orderKey;
    private String rmaNumber;
    private ReturnType returnType;
    private String customerCode;
    private String returnReason;
    private String trackingNumber;
    private String carrierCode;
    private String notes;
    private List<ReturnLineRequest> lines;

    @Data
    @Builder
    public static class ReturnLineRequest {
        private String sku;
        private int expectedQuantity;
        private String returnReason;
        private String disposition;
    }
}
