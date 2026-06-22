package com.maersk.wms.inbound.service.putaway_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class UpdatePutawayStrategyRequest {
    private String description;
    private Boolean active;
    private Integer priority;
    private String defaultAlgorithm;
    private String defaultZone;
    private List<String> allowedZones;
    private List<String> preferredZones;
    private List<String> excludedZones;
    private Boolean allowCrossdock;
    private Boolean allowConsolidation;
    private Boolean checkCapacity;
    private Boolean consolidate;
    private Boolean enforceVelocityClass;
    private Boolean enforceAbcClass;
}
