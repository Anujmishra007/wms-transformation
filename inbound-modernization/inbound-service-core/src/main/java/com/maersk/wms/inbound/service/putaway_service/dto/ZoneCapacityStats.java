package com.maersk.wms.inbound.service.putaway_service.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ZoneCapacityStats {
    private String zone;
    private int totalLocations;
    private long availableLocations;
    private long reservedLocations;
    private long fullLocations;
    private BigDecimal totalCapacity;
    private BigDecimal usedCapacity;
    private BigDecimal availableCapacity;
    private BigDecimal utilizationPercent;
}
