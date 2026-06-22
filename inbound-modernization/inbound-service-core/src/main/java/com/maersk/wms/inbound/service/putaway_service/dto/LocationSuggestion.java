package com.maersk.wms.inbound.service.putaway_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Suggested putaway location.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationSuggestion {
    private String locationKey;
    private String zone;
    private String aisle;
    private String bay;
    private String level;
    private String position;
    private String locationType;
    private String strategyUsed;
    private String reason;
    private String scoreReason;
    private BigDecimal score;
    private BigDecimal availableCapacity;
    private boolean hasSameSku;
    private boolean empty;
}
