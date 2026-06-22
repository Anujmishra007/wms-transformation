package com.maersk.wms.masterdata.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Temporal activity interface for item operations.
 */
@ActivityInterface
public interface ItemActivities {

    @ActivityMethod
    ItemResult createItem(Map<String, Object> itemData, String clientCode, String facilityCode);

    @ActivityMethod
    ItemResult updateItem(String sku, Map<String, Object> itemData, String clientCode, String facilityCode);

    @ActivityMethod
    ItemResult validateItem(Map<String, Object> itemData, String clientCode, String facilityCode);

    @ActivityMethod
    List<ItemResult> importItemBatch(List<Map<String, Object>> items, String clientCode, String facilityCode, boolean updateExisting);

    @Data
    @Builder
    class ItemResult {
        private boolean success;
        private String sku;
        private String errorMessage;
        private String errorCode;
        private Map<String, Object> itemData;
    }
}
