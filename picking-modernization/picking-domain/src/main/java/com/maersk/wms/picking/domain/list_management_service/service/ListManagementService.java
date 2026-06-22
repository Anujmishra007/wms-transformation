package com.maersk.wms.picking.domain.list_management_service.service;

import com.maersk.wms.picking.domain.list_management_service.model.PickList;
import com.maersk.wms.picking.domain.list_management_service.model.PickListStatus;
import com.maersk.wms.picking.domain.list_management_service.model.PickListType;
import com.maersk.wms.picking.shared.kernel.identifiers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * List Management Service - handles pick list creation, assignment, and lifecycle.
 * Manages grouping of pick tasks into lists for efficient warehouse execution.
 */
public interface ListManagementService {

    // List Creation
    PickList createList(PickListType type, String zone, String description);
    PickList createListFromWave(WaveKey waveKey, PickListType type);
    PickList createListFromTasks(List<PickTaskKey> taskKeys, PickListType type, String description);
    PickList createClusterList(List<OrderKey> orderKeys, int clusterSize);
    PickList createZoneList(String zone, int maxTasks);
    PickList createBatchList(List<SkuKey> skuKeys, String zone);

    // Task Management
    void addTaskToList(PickListKey listKey, PickTaskKey taskKey);
    void removeTaskFromList(PickListKey listKey, PickTaskKey taskKey);
    void moveTaskToList(PickTaskKey taskKey, PickListKey fromList, PickListKey toList);
    void resequenceTasks(PickListKey listKey, List<PickTaskKey> orderedTasks);
    List<PickTaskKey> getTasksInList(PickListKey listKey);

    // List Assignment
    void assignList(PickListKey listKey, UserKey userId, DeviceKey deviceId);
    void unassignList(PickListKey listKey, String reason);
    void reassignList(PickListKey listKey, UserKey newUserId, String reason);
    List<PickList> getUnassignedLists(String zone);
    List<PickList> getListsForUser(UserKey userId);

    // List Lifecycle
    void releaseList(PickListKey listKey);
    void startList(PickListKey listKey);
    void suspendList(PickListKey listKey, String reason);
    void resumeList(PickListKey listKey);
    void completeList(PickListKey listKey);
    void cancelList(PickListKey listKey, String reason);

    // Query
    Optional<PickList> getList(PickListKey listKey);
    List<PickList> getListsByWave(WaveKey waveKey);
    List<PickList> getListsByZone(String zone);
    List<PickList> getListsByStatus(PickListStatus status);
    List<PickList> getListsByType(PickListType type);

    // Progress Tracking
    void updateListProgress(PickListKey listKey);
    double getListCompletionPercentage(PickListKey listKey);
    int getCompletedTaskCount(PickListKey listKey);
    int getRemainingTaskCount(PickListKey listKey);

    // Routing
    void optimizeRoute(PickListKey listKey);
    void calculateEstimatedTime(PickListKey listKey);
    String generatePickPath(PickListKey listKey);

    // Merging/Splitting
    PickList mergeLists(List<PickListKey> listKeys, String description);
    List<PickList> splitList(PickListKey listKey, int splitCount);
    List<PickList> splitListByZone(PickListKey listKey);
}
