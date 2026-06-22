package com.maersk.wms.outbound.api;

import com.maersk.wms.outbound.service.OrderService;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Response for allocation operations.
 */
@Data
@Builder
public class AllocationResponse {

    private boolean success;
    private String orderNumber;
    private String status;
    private boolean fullyAllocated;
    private int allocatedLines;
    private int shortLines;
    private List<String> allocationIds;
    private String errorMessage;

    public static AllocationResponse fromResult(OrderService.AllocationResult result) {
        AllocationResponseBuilder builder = AllocationResponse.builder()
                .success(result.isSuccess())
                .errorMessage(result.getErrorMessage())
                .allocationIds(result.getAllocationIds());

        if (result.getOrder() != null) {
            builder.orderNumber(result.getOrder().getOrderKey())
                    .status(result.getOrder().getStatus() != null ? result.getOrder().getStatus().name() : null)
                    .fullyAllocated(result.isSuccess());
        }

        return builder.build();
    }
}
