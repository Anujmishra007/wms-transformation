package com.maersk.wms.outbound.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Temporal activity interface for wave operations.
 */
@ActivityInterface
public interface WaveActivities {

    @ActivityMethod
    WaveResult createWave(List<String> orderNumbers, String waveType,
                          String clientCode, String facilityCode);

    @ActivityMethod
    WaveResult releaseWave(String waveNumber, String clientCode, String facilityCode);

    @ActivityMethod
    void cancelWave(String waveNumber, String clientCode, String facilityCode);

    @ActivityMethod
    WaveResult closeWave(String waveNumber, String clientCode, String facilityCode);

    @ActivityMethod
    List<String> getEligibleOrders(String waveType, LocalDateTime targetShipDate,
                                   String clientCode, String facilityCode);

    @ActivityMethod
    WaveGroupResult groupOrdersForWave(List<String> orderNumbers,
                                       String clientCode, String facilityCode);

    @Data
    @Builder
    class WaveResult {
        private boolean success;
        private String waveNumber;
        private String status;
        private int orderCount;
        private int lineCount;
        private int totalUnits;
        private List<String> orderNumbers;
        private List<String> errors;
    }

    @Data
    @Builder
    class WaveGroupResult {
        private boolean success;
        private List<WaveGroup> groups;
        private List<String> excludedOrders;
        private List<String> errors;
    }

    @Data
    @Builder
    class WaveGroup {
        private String groupKey;
        private String carrier;
        private String shipMethod;
        private List<String> orderNumbers;
        private int lineCount;
        private int unitCount;
    }
}
