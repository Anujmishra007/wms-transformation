package com.maersk.wms.outbound.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Temporal activity interface for packing operations.
 */
@ActivityInterface
public interface PackingActivities {

    @ActivityMethod
    PackResult packOrder(String orderNumber, String clientCode, String facilityCode);

    @ActivityMethod
    PackResult packItems(String orderNumber, List<String> allocationIds, String cartonType,
                         String clientCode, String facilityCode);

    @ActivityMethod
    void unpackOrder(List<String> cartonIds);

    @ActivityMethod
    void unpackCarton(String cartonId, String clientCode, String facilityCode);

    @ActivityMethod
    PackResult closeCarton(String cartonId, String clientCode, String facilityCode);

    @ActivityMethod
    CartonizationResult planCartonization(String orderNumber, String clientCode, String facilityCode);

    @Data
    @Builder
    class PackResult {
        private boolean success;
        private int cartonCount;
        private BigDecimal totalWeight;
        private List<String> cartonIds;
        private List<String> errors;
        private List<CartonDetail> cartons;
    }

    @Data
    @Builder
    class CartonDetail {
        private String cartonId;
        private String cartonType;
        private String orderNumber;
        private int itemCount;
        private BigDecimal weight;
        private String status;
        private List<CartonContent> contents;
    }

    @Data
    @Builder
    class CartonContent {
        private String sku;
        private BigDecimal quantity;
        private String allocationId;
    }

    @Data
    @Builder
    class CartonizationResult {
        private boolean success;
        private List<CartonPlan> cartonPlans;
        private List<String> errors;
    }

    @Data
    @Builder
    class CartonPlan {
        private String cartonType;
        private List<String> allocationIds;
        private BigDecimal estimatedWeight;
        private String packingInstructions;
    }
}
