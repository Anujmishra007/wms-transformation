package com.maersk.wms.inbound.service.putaway_service.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class UpdatePutawayAlgorithmRequest {
    private String description;
    private Boolean active;
    private Integer priority;
    private Boolean preferEmpty;
    private Boolean preferConsolidation;
    private Boolean preferNearPick;
    private BigDecimal consolidationWeight;
    private BigDecimal proximityWeight;
    private BigDecimal distanceWeight;
    private BigDecimal capacityWeight;
    private BigDecimal velocityWeight;
    private List<String> allowedZones;
    private List<String> preferredZones;
    private Map<String, Object> parameters;
}
