package com.maersk.wms.masterdata.workflow;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Request for location setup workflow.
 */
@Data
@Builder
public class LocationSetupRequest {

    private String clientCode;
    private String facilityCode;
    private String userId;

    // Zone to create locations in
    private String zone;
    private String locationType;

    // Range-based creation
    private List<String> aisles;
    private int bayStart;
    private int bayEnd;
    private int levelStart;
    private int levelEnd;
    private int positionStart;
    private int positionEnd;

    // Default attributes
    private BigDecimal defaultLength;
    private BigDecimal defaultWidth;
    private BigDecimal defaultHeight;
    private BigDecimal defaultMaxWeight;
    private boolean defaultPickLocation;
    private boolean defaultPutawayLocation;
    private boolean mixedSku;
    private boolean mixedLot;
}
