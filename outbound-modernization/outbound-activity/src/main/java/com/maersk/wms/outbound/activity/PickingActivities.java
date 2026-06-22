package com.maersk.wms.outbound.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Temporal activity interface for picking operations.
 * Part of Picking Operations Service bounded context.
 */
@ActivityInterface
public interface PickingActivities {

    @ActivityMethod
    PickResult createPickTasks(String orderNumber, List<String> allocationIds,
                               String clientCode, String facilityCode);

    @ActivityMethod
    PickResult releasePickTasks(List<String> pickTaskIds, String clientCode, String facilityCode);

    @ActivityMethod
    void cancelPickTasks(List<String> pickTaskIds);

    @ActivityMethod
    PickResult confirmPick(String pickTaskId, BigDecimal quantity,
                           String clientCode, String facilityCode);

    @ActivityMethod
    PickResult shortPick(String pickTaskId, BigDecimal pickedQty, BigDecimal shortQty,
                         String clientCode, String facilityCode);

    // === Additional methods for workflow integration ===

    /**
     * Gets the next pick instruction for a user.
     */
    @ActivityMethod
    PickInstructionResult getNextPick(String userId, String pickListId);

    /**
     * Confirms a pick with full details.
     */
    @ActivityMethod
    ConfirmPickResult confirmPickWithDetails(String pickDetailKey, BigDecimal qtyPicked,
                                              String toLpn, String userId);

    /**
     * Records a short pick with reason.
     */
    @ActivityMethod
    void recordShortPick(String pickDetailKey, BigDecimal expectedQty, BigDecimal actualQty,
                         String reasonCode, String userId);

    /**
     * Skips a pick.
     */
    @ActivityMethod
    void skipPick(String pickDetailKey, String reason, String userId);

    /**
     * Completes a pick list.
     */
    @ActivityMethod
    void completePickList(String pickListId, String userId);

    /**
     * Updates inventory after pick.
     */
    @ActivityMethod
    void updateInventory(String pickDetailKey, BigDecimal qtyPicked, String fromLpn, String toLpn);

    /**
     * Loads picks for a pick list.
     */
    @ActivityMethod
    List<PickTaskDetail> loadPicksForList(String pickListId);

    @Data
    @Builder
    class PickResult {
        private boolean success;
        private int pickedLines;
        private int shortLines;
        private BigDecimal totalPickedQty;
        private List<String> pickTaskIds;
        private List<String> errors;
        private List<PickTaskDetail> pickTasks;
    }

    @Data
    @Builder
    class PickInstructionResult {
        private String pickDetailKey;
        private String sku;
        private String skuDescription;
        private String fromLocation;
        private String fromLpn;
        private BigDecimal qtyToPick;
        private String uom;
        private String toLocation;
        private boolean hasMorePicks;
    }

    @Data
    @Builder
    class ConfirmPickResult {
        private boolean success;
        private String pickDetailKey;
        private BigDecimal qtyConfirmed;
        private String message;
    }

    @Data
    @Builder
    class PickTaskDetail {
        private String pickTaskId;
        private String allocationId;
        private String orderNumber;
        private String sku;
        private String fromLocation;
        private String lpn;
        private BigDecimal quantity;
        private String status;
        private int sequence;
    }
}
