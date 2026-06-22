package com.maersk.wms.picking.service.impl;

import com.maersk.wms.picking.domain.list_management_service.model.PickList;
import com.maersk.wms.picking.domain.list_management_service.model.PickListStatus;
import com.maersk.wms.picking.domain.list_management_service.model.PickListType;
import com.maersk.wms.picking.domain.list_management_service.repository.PickListRepository;
import com.maersk.wms.picking.domain.list_management_service.service.ListManagementService;
import com.maersk.wms.picking.domain.task_execution_service.model.PickTask;
import com.maersk.wms.picking.domain.task_execution_service.repository.PickTaskRepository;
import com.maersk.wms.picking.shared.kernel.identifiers.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of List Management Service.
 * Handles pick list creation, assignment, and lifecycle.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ListManagementServiceImpl implements ListManagementService {

    private final PickListRepository listRepository;
    private final PickTaskRepository taskRepository;

    // List Creation

    @Override
    @Transactional
    public PickList createList(PickListType type, String zone, String description) {
        log.info("Creating {} pick list for zone {}", type, zone);

        PickList list = PickList.builder()
                .pickListKey(new PickListKey(UUID.randomUUID().toString()))
                .type(type)
                .zone(zone)
                .description(description)
                .status(PickListStatus.CREATED)
                .createdTime(LocalDateTime.now())
                .totalTasks(0)
                .completedTasks(0)
                .shortedTasks(0)
                .skippedTasks(0)
                .cancelledTasks(0)
                .pickTasks(new ArrayList<>())
                .build();

        return listRepository.save(list);
    }

    @Override
    @Transactional
    public PickList createListFromWave(WaveKey waveKey, PickListType type) {
        log.info("Creating pick list from wave {}", waveKey);

        List<PickTask> waveTasks = taskRepository.findByWave(waveKey);
        if (waveTasks.isEmpty()) {
            throw new IllegalArgumentException("No tasks found for wave: " + waveKey);
        }

        String zone = waveTasks.get(0).getZone();
        PickList list = createList(type, zone, "Wave: " + waveKey);
        list.setWaveKey(waveKey);

        for (PickTask task : waveTasks) {
            list.addTask(task.getPickTaskKey());
        }

        return listRepository.save(list);
    }

    @Override
    @Transactional
    public PickList createListFromTasks(List<PickTaskKey> taskKeys, PickListType type, String description) {
        log.info("Creating pick list from {} tasks", taskKeys.size());

        if (taskKeys.isEmpty()) {
            throw new IllegalArgumentException("Task list cannot be empty");
        }

        // Get first task to determine zone
        PickTask firstTask = taskRepository.findById(taskKeys.get(0))
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskKeys.get(0)));

        PickList list = createList(type, firstTask.getZone(), description);

        for (PickTaskKey taskKey : taskKeys) {
            list.addTask(taskKey);
        }

        return listRepository.save(list);
    }

    @Override
    @Transactional
    public PickList createClusterList(List<OrderKey> orderKeys, int clusterSize) {
        log.info("Creating cluster list for {} orders with cluster size {}", orderKeys.size(), clusterSize);

        List<PickTaskKey> allTasks = new ArrayList<>();
        for (OrderKey orderKey : orderKeys) {
            List<PickTask> orderTasks = taskRepository.findByOrder(orderKey);
            allTasks.addAll(orderTasks.stream().map(PickTask::getPickTaskKey).collect(Collectors.toList()));
        }

        return createListFromTasks(allTasks, PickListType.CLUSTER,
                "Cluster: " + orderKeys.size() + " orders");
    }

    @Override
    @Transactional
    public PickList createZoneList(String zone, int maxTasks) {
        log.info("Creating zone list for {} with max {} tasks", zone, maxTasks);

        List<PickTask> zoneTasks = taskRepository.findByZoneAndStatusOrderByPriority(
                zone, com.maersk.wms.picking.domain.task_execution_service.model.PickTaskStatus.RELEASED);

        List<PickTaskKey> taskKeys = zoneTasks.stream()
                .limit(maxTasks)
                .map(PickTask::getPickTaskKey)
                .collect(Collectors.toList());

        return createListFromTasks(taskKeys, PickListType.ZONE, "Zone: " + zone);
    }

    @Override
    @Transactional
    public PickList createBatchList(List<SkuKey> skuKeys, String zone) {
        log.info("Creating batch list for {} SKUs in zone {}", skuKeys.size(), zone);

        List<PickTaskKey> allTasks = new ArrayList<>();
        for (SkuKey skuKey : skuKeys) {
            List<PickTask> skuTasks = taskRepository.findByZoneAndStatus(
                    zone, com.maersk.wms.picking.domain.task_execution_service.model.PickTaskStatus.RELEASED);
            List<PickTaskKey> skuTaskKeys = skuTasks.stream()
                    .filter(t -> t.getSku() != null && t.getSku().equals(skuKey))
                    .map(PickTask::getPickTaskKey)
                    .collect(Collectors.toList());
            allTasks.addAll(skuTaskKeys);
        }

        return createListFromTasks(allTasks, PickListType.BATCH, "Batch: " + skuKeys.size() + " SKUs");
    }

    // Task Management

    @Override
    @Transactional
    public void addTaskToList(PickListKey listKey, PickTaskKey taskKey) {
        log.info("Adding task {} to list {}", taskKey, listKey);

        PickList list = getList(listKey)
                .orElseThrow(() -> new IllegalArgumentException("List not found: " + listKey));

        list.addTask(taskKey);
        listRepository.save(list);
    }

    @Override
    @Transactional
    public void removeTaskFromList(PickListKey listKey, PickTaskKey taskKey) {
        log.info("Removing task {} from list {}", taskKey, listKey);

        PickList list = getList(listKey)
                .orElseThrow(() -> new IllegalArgumentException("List not found: " + listKey));

        list.removeTask(taskKey);
        listRepository.save(list);
    }

    @Override
    @Transactional
    public void moveTaskToList(PickTaskKey taskKey, PickListKey fromList, PickListKey toList) {
        log.info("Moving task {} from list {} to list {}", taskKey, fromList, toList);

        removeTaskFromList(fromList, taskKey);
        addTaskToList(toList, taskKey);
    }

    @Override
    @Transactional
    public void resequenceTasks(PickListKey listKey, List<PickTaskKey> orderedTasks) {
        log.info("Resequencing tasks for list {}", listKey);

        PickList list = getList(listKey)
                .orElseThrow(() -> new IllegalArgumentException("List not found: " + listKey));

        list.setPickTasks(orderedTasks);
        listRepository.save(list);
    }

    @Override
    public List<PickTaskKey> getTasksInList(PickListKey listKey) {
        return getList(listKey)
                .map(PickList::getPickTasks)
                .orElse(new ArrayList<>());
    }

    // List Assignment

    @Override
    @Transactional
    public void assignList(PickListKey listKey, UserKey userId, DeviceKey deviceId) {
        log.info("Assigning list {} to user {} on device {}", listKey, userId, deviceId);

        PickList list = getList(listKey)
                .orElseThrow(() -> new IllegalArgumentException("List not found: " + listKey));

        if (!list.canAssign()) {
            throw new IllegalStateException("List cannot be assigned in current state: " + list.getStatus());
        }

        list.assign(userId, deviceId);
        listRepository.save(list);
    }

    @Override
    @Transactional
    public void unassignList(PickListKey listKey, String reason) {
        log.info("Unassigning list {} - reason: {}", listKey, reason);

        PickList list = getList(listKey)
                .orElseThrow(() -> new IllegalArgumentException("List not found: " + listKey));

        list.setAssignedUser(null);
        list.setAssignedDevice(null);
        list.setAssignedTime(null);
        list.setStatus(PickListStatus.RELEASED);
        listRepository.save(list);
    }

    @Override
    @Transactional
    public void reassignList(PickListKey listKey, UserKey newUserId, String reason) {
        log.info("Reassigning list {} to user {} - reason: {}", listKey, newUserId, reason);

        PickList list = getList(listKey)
                .orElseThrow(() -> new IllegalArgumentException("List not found: " + listKey));

        list.setAssignedUser(newUserId);
        list.setAssignedTime(LocalDateTime.now());
        listRepository.save(list);
    }

    @Override
    public List<PickList> getUnassignedLists(String zone) {
        return listRepository.findUnassignedByZone(zone);
    }

    @Override
    public List<PickList> getListsForUser(UserKey userId) {
        return listRepository.findByUser(userId);
    }

    // List Lifecycle

    @Override
    @Transactional
    public void releaseList(PickListKey listKey) {
        log.info("Releasing list {}", listKey);

        PickList list = getList(listKey)
                .orElseThrow(() -> new IllegalArgumentException("List not found: " + listKey));

        list.setStatus(PickListStatus.RELEASED);
        list.setReleasedTime(LocalDateTime.now());
        listRepository.save(list);
    }

    @Override
    @Transactional
    public void startList(PickListKey listKey) {
        log.info("Starting list {}", listKey);

        PickList list = getList(listKey)
                .orElseThrow(() -> new IllegalArgumentException("List not found: " + listKey));

        if (!list.canStart()) {
            throw new IllegalStateException("List cannot be started in current state: " + list.getStatus());
        }

        list.start();
        listRepository.save(list);
    }

    @Override
    @Transactional
    public void suspendList(PickListKey listKey, String reason) {
        log.info("Suspending list {} - reason: {}", listKey, reason);

        PickList list = getList(listKey)
                .orElseThrow(() -> new IllegalArgumentException("List not found: " + listKey));

        list.setStatus(PickListStatus.SUSPENDED);
        listRepository.save(list);
    }

    @Override
    @Transactional
    public void resumeList(PickListKey listKey) {
        log.info("Resuming list {}", listKey);

        PickList list = getList(listKey)
                .orElseThrow(() -> new IllegalArgumentException("List not found: " + listKey));

        list.setStatus(PickListStatus.IN_PROGRESS);
        listRepository.save(list);
    }

    @Override
    @Transactional
    public void completeList(PickListKey listKey) {
        log.info("Completing list {}", listKey);

        PickList list = getList(listKey)
                .orElseThrow(() -> new IllegalArgumentException("List not found: " + listKey));

        list.complete();
        listRepository.save(list);
    }

    @Override
    @Transactional
    public void cancelList(PickListKey listKey, String reason) {
        log.info("Cancelling list {} - reason: {}", listKey, reason);

        PickList list = getList(listKey)
                .orElseThrow(() -> new IllegalArgumentException("List not found: " + listKey));

        list.setStatus(PickListStatus.CANCELLED);
        listRepository.save(list);
    }

    // Query

    @Override
    public Optional<PickList> getList(PickListKey listKey) {
        return listRepository.findById(listKey);
    }

    @Override
    public List<PickList> getListsByWave(WaveKey waveKey) {
        return listRepository.findByWave(waveKey);
    }

    @Override
    public List<PickList> getListsByZone(String zone) {
        return listRepository.findByZone(zone);
    }

    @Override
    public List<PickList> getListsByStatus(PickListStatus status) {
        return listRepository.findByStatus(status);
    }

    @Override
    public List<PickList> getListsByType(PickListType type) {
        return listRepository.findByType(type);
    }

    // Progress Tracking

    @Override
    @Transactional
    public void updateListProgress(PickListKey listKey) {
        log.debug("Updating progress for list {}", listKey);

        PickList list = getList(listKey)
                .orElseThrow(() -> new IllegalArgumentException("List not found: " + listKey));

        // Count completed tasks
        int completed = 0;
        int shorted = 0;
        int skipped = 0;
        int cancelled = 0;

        for (PickTaskKey taskKey : list.getPickTasks()) {
            PickTask task = taskRepository.findById(taskKey).orElse(null);
            if (task != null) {
                switch (task.getStatus()) {
                    case COMPLETED -> completed++;
                    case SHORTED -> shorted++;
                    case SKIPPED -> skipped++;
                    case CANCELLED -> cancelled++;
                }
            }
        }

        list.setCompletedTasks(completed);
        list.setShortedTasks(shorted);
        list.setSkippedTasks(skipped);
        list.setCancelledTasks(cancelled);

        // Check if list is complete
        if (list.isComplete()) {
            list.complete();
        }

        listRepository.save(list);
    }

    @Override
    public double getListCompletionPercentage(PickListKey listKey) {
        return getList(listKey)
                .map(PickList::getCompletionPercentage)
                .orElse(0.0);
    }

    @Override
    public int getCompletedTaskCount(PickListKey listKey) {
        return getList(listKey)
                .map(PickList::getCompletedTasks)
                .orElse(0);
    }

    @Override
    public int getRemainingTaskCount(PickListKey listKey) {
        return getList(listKey)
                .map(PickList::getRemainingTasks)
                .orElse(0);
    }

    // Routing

    @Override
    @Transactional
    public void optimizeRoute(PickListKey listKey) {
        log.info("Optimizing route for list {}", listKey);

        PickList list = getList(listKey)
                .orElseThrow(() -> new IllegalArgumentException("List not found: " + listKey));

        // Sort tasks by location for optimal pick path
        List<PickTask> tasks = list.getPickTasks().stream()
                .map(taskRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .sorted((t1, t2) -> {
                    if (t1.getAisle() != null && t2.getAisle() != null) {
                        int aisleCompare = t1.getAisle().compareTo(t2.getAisle());
                        if (aisleCompare != 0) return aisleCompare;
                    }
                    if (t1.getFromLocation() != null && t2.getFromLocation() != null) {
                        return t1.getFromLocation().toString().compareTo(t2.getFromLocation().toString());
                    }
                    return 0;
                })
                .collect(Collectors.toList());

        list.setPickTasks(tasks.stream().map(PickTask::getPickTaskKey).collect(Collectors.toList()));
        list.setRoute(generatePickPath(listKey));
        listRepository.save(list);
    }

    @Override
    @Transactional
    public void calculateEstimatedTime(PickListKey listKey) {
        log.info("Calculating estimated time for list {}", listKey);

        PickList list = getList(listKey)
                .orElseThrow(() -> new IllegalArgumentException("List not found: " + listKey));

        // Simple estimation: 30 seconds per task + 10 seconds travel between locations
        int taskCount = list.getTotalTasks();
        int estimatedMinutes = (taskCount * 30 + (taskCount - 1) * 10) / 60;

        list.setEstimatedMinutes(estimatedMinutes);
        listRepository.save(list);
    }

    @Override
    public String generatePickPath(PickListKey listKey) {
        PickList list = getList(listKey).orElse(null);
        if (list == null || list.getPickTasks().isEmpty()) {
            return "";
        }

        StringBuilder path = new StringBuilder();
        for (PickTaskKey taskKey : list.getPickTasks()) {
            PickTask task = taskRepository.findById(taskKey).orElse(null);
            if (task != null && task.getFromLocation() != null) {
                if (path.length() > 0) path.append(" -> ");
                path.append(task.getFromLocation());
            }
        }
        return path.toString();
    }

    // Merging/Splitting

    @Override
    @Transactional
    public PickList mergeLists(List<PickListKey> listKeys, String description) {
        log.info("Merging {} lists", listKeys.size());

        List<PickTaskKey> allTasks = new ArrayList<>();
        String zone = null;

        for (PickListKey listKey : listKeys) {
            PickList list = getList(listKey)
                    .orElseThrow(() -> new IllegalArgumentException("List not found: " + listKey));
            allTasks.addAll(list.getPickTasks());
            if (zone == null) zone = list.getZone();

            // Cancel the original list
            list.setStatus(PickListStatus.CANCELLED);
            listRepository.save(list);
        }

        return createListFromTasks(allTasks, PickListType.STANDARD, description);
    }

    @Override
    @Transactional
    public List<PickList> splitList(PickListKey listKey, int splitCount) {
        log.info("Splitting list {} into {} parts", listKey, splitCount);

        PickList originalList = getList(listKey)
                .orElseThrow(() -> new IllegalArgumentException("List not found: " + listKey));

        List<PickTaskKey> allTasks = originalList.getPickTasks();
        int tasksPerList = (int) Math.ceil((double) allTasks.size() / splitCount);

        List<PickList> newLists = new ArrayList<>();
        for (int i = 0; i < splitCount && i * tasksPerList < allTasks.size(); i++) {
            int start = i * tasksPerList;
            int end = Math.min(start + tasksPerList, allTasks.size());
            List<PickTaskKey> taskSubset = allTasks.subList(start, end);

            PickList newList = createListFromTasks(taskSubset, originalList.getType(),
                    originalList.getDescription() + " - Part " + (i + 1));
            newLists.add(newList);
        }

        // Cancel the original list
        originalList.setStatus(PickListStatus.CANCELLED);
        listRepository.save(originalList);

        return newLists;
    }

    @Override
    @Transactional
    public List<PickList> splitListByZone(PickListKey listKey) {
        log.info("Splitting list {} by zone", listKey);

        PickList originalList = getList(listKey)
                .orElseThrow(() -> new IllegalArgumentException("List not found: " + listKey));

        // Group tasks by zone
        java.util.Map<String, List<PickTaskKey>> tasksByZone = new java.util.HashMap<>();
        for (PickTaskKey taskKey : originalList.getPickTasks()) {
            PickTask task = taskRepository.findById(taskKey).orElse(null);
            if (task != null) {
                String taskZone = task.getZone() != null ? task.getZone() : "UNKNOWN";
                tasksByZone.computeIfAbsent(taskZone, k -> new ArrayList<>()).add(taskKey);
            }
        }

        List<PickList> newLists = new ArrayList<>();
        for (java.util.Map.Entry<String, List<PickTaskKey>> entry : tasksByZone.entrySet()) {
            PickList newList = createListFromTasks(entry.getValue(), PickListType.ZONE,
                    originalList.getDescription() + " - Zone " + entry.getKey());
            newList.setZone(entry.getKey());
            listRepository.save(newList);
            newLists.add(newList);
        }

        // Cancel the original list
        originalList.setStatus(PickListStatus.CANCELLED);
        listRepository.save(originalList);

        return newLists;
    }
}
