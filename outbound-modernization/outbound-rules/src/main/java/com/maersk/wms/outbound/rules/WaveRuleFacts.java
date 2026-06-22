package com.maersk.wms.outbound.rules;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Facts for wave planning rule evaluation.
 */
@Data
@Builder
public class WaveRuleFacts {

    private String clientCode;
    private String facilityCode;
    private String waveType;
    private LocalDateTime targetShipDate;

    // Orders to be waved
    private List<WaveOrderFact> orders;

    // Wave constraints
    private int maxOrdersPerWave;
    private int maxLinesPerWave;
    private int maxUnitsPerWave;

    // Configuration
    private Map<String, String> clientConfig;

    @Data
    @Builder
    public static class WaveOrderFact {
        private String orderNumber;
        private String orderType;
        private String customerCode;
        private String carrier;
        private String shipMethod;
        private String shipToCountry;
        private String shipToState;
        private String shipToZip;
        private int priority;
        private int lineCount;
        private int totalUnits;
        private LocalDateTime requiredDate;
        private LocalDateTime cutoffTime;
        private boolean hazmat;
        private boolean oversized;
        private boolean refrigerated;
    }
}
