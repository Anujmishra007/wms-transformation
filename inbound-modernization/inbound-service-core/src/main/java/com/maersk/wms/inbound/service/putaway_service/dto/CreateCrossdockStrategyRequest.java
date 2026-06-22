package com.maersk.wms.inbound.service.putaway_service.dto;

import com.maersk.wms.inbound.domain.putaway_service.CrossdockStrategyType;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateCrossdockStrategyRequest {
    private String strategyName;
    private String description;
    private CrossdockStrategyType strategyType;
    private boolean active;
    private int priority;
    private boolean matchBySku;
    private boolean matchByLot;
    private boolean matchByStorer;
    private boolean matchByOrder;
    private boolean matchByWave;
    private boolean matchByShipment;
    private boolean allowPartialMatch;
    private Integer demandHorizonDays;
    private Integer demandHorizonHours;
    private boolean checkFutureDemand;
    private boolean checkCurrentDemand;
    private BigDecimal minMatchPercent;
    private BigDecimal minQuantity;
    private BigDecimal maxQuantity;
    private boolean prioritizeOldestDemand;
    private boolean prioritizeHighestPriority;
    private boolean prioritizeNearestShipDate;
    private List<String> crossdockZones;
    private List<String> stagingZones;
    private String defaultStagingZone;
    private List<String> allowedSkuTypes;
    private List<String> excludedSkuTypes;
    private boolean excludeHazmat;
    private boolean excludeTemperatureControlled;
    private List<String> allowedOrderTypes;
    private List<String> allowedCarriers;
    private boolean checkTimeWindow;
    private Integer minLeadTimeHours;
    private Integer maxLeadTimeHours;
    private boolean fallbackToStorage;
    private String noMatchAction;
}
