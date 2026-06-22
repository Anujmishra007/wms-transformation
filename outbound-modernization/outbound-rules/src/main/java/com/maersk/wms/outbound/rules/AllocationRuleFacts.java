package com.maersk.wms.outbound.rules;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Facts for allocation rule evaluation.
 */
@Data
@Builder
public class AllocationRuleFacts {

    private String clientCode;
    private String facilityCode;
    private String orderNumber;
    private String orderType;
    private String sku;
    private BigDecimal requiredQty;
    private String requestedLot;
    private LocalDateTime requiredDate;

    // Order attributes
    private String customerCode;
    private String shipToCountry;
    private String carrier;
    private int priority;

    // Inventory candidates
    private List<InventoryCandidate> candidates;

    // Configuration
    private Map<String, String> clientConfig;

    @Data
    @Builder
    public static class InventoryCandidate {
        private String location;
        private String lpn;
        private String lot;
        private BigDecimal availableQty;
        private LocalDateTime receiptDate;
        private LocalDateTime expirationDate;
        private String lottable01;
        private String lottable02;
        private String lottable03;
        private String lottable04;
        private String lottable05;
        private String inventoryStatus;
        private String locationType;
        private int zonePriority;
        private int pickPathSequence;
    }
}
