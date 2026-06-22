package com.maersk.wms.outbound.kafka;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Event published when an order is allocated.
 */
@Data
@Builder
public class OrderAllocatedEvent {

    private String eventId;
    private LocalDateTime eventTime;
    private String clientCode;
    private String facilityCode;

    private String orderNumber;
    private boolean fullyAllocated;
    private int allocatedLines;
    private int shortLines;
    private BigDecimal totalAllocatedQty;
    private BigDecimal shortQty;

    private List<AllocationEvent> allocations;

    @Data
    @Builder
    public static class AllocationEvent {
        private String allocationId;
        private String lineNumber;
        private String sku;
        private String location;
        private String lpn;
        private String lot;
        private BigDecimal quantity;
    }
}
