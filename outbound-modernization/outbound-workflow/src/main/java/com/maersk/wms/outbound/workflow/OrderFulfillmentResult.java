package com.maersk.wms.outbound.workflow;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Result of an order fulfillment workflow execution.
 */
@Data
@Builder
public class OrderFulfillmentResult {

    private String orderNumber;
    private OrderFulfillmentStatus status;
    private String message;

    // Allocation results
    private boolean fullyAllocated;
    private int allocatedLines;
    private int shortLines;
    private List<String> allocationIds;

    // Pick results
    private int pickedLines;
    private List<String> pickTaskIds;

    // Pack results
    private int cartonCount;
    private List<String> cartonIds;

    // Ship results
    private String shipmentId;
    private String trackingNumber;
    private String carrier;

    // Timing
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private long durationMs;

    // Errors if any
    private List<String> errors;
    private Map<String, String> metadata;
}
