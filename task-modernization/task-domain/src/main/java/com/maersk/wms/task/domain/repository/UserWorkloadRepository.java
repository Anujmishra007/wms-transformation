package com.maersk.wms.task.domain.repository;

import com.maersk.wms.task.domain.entity.UserWorkload;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for UserWorkload entity operations.
 */
public interface UserWorkloadRepository {

    UserWorkload save(UserWorkload workload);

    Optional<UserWorkload> findByWorkloadKey(Long workloadKey);

    Optional<UserWorkload> findByUserId(String userId);

    List<UserWorkload> findByWorkGroup(String workGroup);

    List<UserWorkload> findByWorkZone(String workZone);

    List<UserWorkload> findByShiftCode(String shiftCode);

    List<UserWorkload> findAvailableUsers();

    List<UserWorkload> findAvailableUsersByWorkGroup(String workGroup);

    List<UserWorkload> findAvailableUsersByWorkZone(String workZone);

    List<UserWorkload> findUsersWithCapacity(int maxAssignedTasks);

    List<UserWorkload> findByCurrentLocation(String location);

    List<UserWorkload> findIdleUsers(LocalDateTime idleSince);

    List<UserWorkload> findUsersByAllowedTaskType(String taskType);

    UserWorkload findLeastLoadedUser(String workGroup);

    void updateCurrentTask(String userId, Long taskKey, String taskId);

    void clearCurrentTask(String userId);

    void updateTaskCounts(String userId, int assignedTasks, int completedTasks, int pendingTasks, int inProgressTasks);

    void updateLocation(String userId, String location, String zone, String aisle);

    void updateAvailability(String userId, boolean isAvailable);

    void updateProductivityMetrics(String userId, Double productivityRate, Double accuracy);

    void recordActivity(String userId, LocalDateTime activityTime);

    void deleteByUserId(String userId);

    boolean existsByUserId(String userId);
}
