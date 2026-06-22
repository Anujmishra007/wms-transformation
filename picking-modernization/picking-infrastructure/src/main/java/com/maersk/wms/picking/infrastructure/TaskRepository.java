package com.maersk.wms.picking.infrastructure;

import com.maersk.wms.picking.domain.PickTask;
import com.maersk.wms.picking.domain.TaskStatus;
import com.maersk.wms.picking.plugin.GetTaskCriteria;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for pick task persistence.
 */
public interface TaskRepository {

    /**
     * Find task by ID.
     */
    Optional<PickTask> findById(String taskId);

    /**
     * Find available tasks matching criteria.
     */
    List<PickTask> findAvailableTasks(GetTaskCriteria criteria);

    /**
     * Find tasks assigned to user.
     */
    List<PickTask> findByAssignedUser(String userId);

    /**
     * Save task.
     */
    PickTask save(PickTask task);

    /**
     * Update task status.
     */
    void updateStatus(String taskId, TaskStatus status);

    /**
     * Assign task to user.
     */
    void assignTask(String taskId, String userId);
}
