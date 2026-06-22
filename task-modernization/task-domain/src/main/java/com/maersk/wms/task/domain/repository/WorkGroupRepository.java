package com.maersk.wms.task.domain.repository;

import com.maersk.wms.task.domain.entity.WorkGroup;
import com.maersk.wms.task.domain.enums.WorkGroupStatus;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for WorkGroup entity operations.
 */
public interface WorkGroupRepository {

    WorkGroup save(WorkGroup workGroup);

    Optional<WorkGroup> findByWorkGroupKey(Long workGroupKey);

    Optional<WorkGroup> findByWorkGroupCode(String workGroupCode);

    List<WorkGroup> findByStatus(WorkGroupStatus status);

    List<WorkGroup> findActiveWorkGroups();

    List<WorkGroup> findByWorkZone(String workZone);

    List<WorkGroup> findByShiftCode(String shiftCode);

    List<WorkGroup> findBySupervisorUserId(String supervisorUserId);

    List<WorkGroup> findWithCapacity();

    List<WorkGroup> findByAllowedTaskType(String taskType);

    int countByStatus(WorkGroupStatus status);

    void updateStatus(Long workGroupKey, WorkGroupStatus status, String modifiedBy);

    void updateUserCount(Long workGroupKey, int currentUsers);

    void updateTaskCount(Long workGroupKey, int currentTasks);

    void deleteByWorkGroupKey(Long workGroupKey);

    boolean existsByWorkGroupCode(String workGroupCode);
}
