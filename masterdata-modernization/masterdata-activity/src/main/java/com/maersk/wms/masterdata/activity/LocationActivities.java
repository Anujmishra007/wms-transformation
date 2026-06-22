package com.maersk.wms.masterdata.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Temporal activity interface for location operations.
 */
@ActivityInterface
public interface LocationActivities {

    @ActivityMethod
    LocationResult createLocation(Map<String, Object> locationData, String clientCode, String facilityCode);

    @ActivityMethod
    LocationResult updateLocation(String locationCode, Map<String, Object> locationData, String clientCode, String facilityCode);

    @ActivityMethod
    LocationResult validateLocation(Map<String, Object> locationData, String clientCode, String facilityCode);

    @ActivityMethod
    List<LocationResult> createLocationBatch(List<Map<String, Object>> locations, String clientCode, String facilityCode);

    @ActivityMethod
    LocationResult generateLocationCode(String zone, String aisle, String bay, String level, String position, String clientCode, String facilityCode);

    @Data
    @Builder
    class LocationResult {
        private boolean success;
        private String locationCode;
        private String errorMessage;
        private String errorCode;
        private Map<String, Object> locationData;
    }
}
