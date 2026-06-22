package com.maersk.wms.inbound.service.putaway_service.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class UpdateCrossdockStrategyRequest {
    private String description;
    private Boolean active;
    private Integer priority;
    private Integer demandHorizonDays;
    private BigDecimal minMatchPercent;
    private List<String> crossdockZones;
    private List<String> stagingZones;
    private String defaultStagingZone;
}
