package com.maersk.wms.task.activity;

import com.maersk.wms.task.domain.entity.Task;
import com.maersk.wms.task.domain.enums.TaskPriority;
import com.maersk.wms.task.domain.enums.TaskStatus;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * Temporal activity interface for task operations.
 */
@ActivityInterface
public interface TaskActivities {

    @ActivityMethod
    Task createTask(Task task);

    @ActivityMethod
    Task getTask(Long taskKey);

    @ActivityMethod
    Task getTaskById(String taskId);

    @ActivityMethod
    void updateTaskStatus(Long taskKey, TaskStatus status, String updatedBy);

    @ActivityMethod
    void updateTaskPriority(Long taskKey, TaskPriority priority, String updatedBy);

    @ActivityMethod
    void assignTask(Long taskKey, String userId, String assignedBy);

    @ActivityMethod
    void unassignTask(Long taskKey, String reason, String unassignedBy);

    @ActivityMethod
    void startTask(Long taskKey, String startedBy);

    @ActivityMethod
    void completeTask(Long taskKey, Double completedQuantity, String completedBy);

    @ActivityMethod
    void shortTask(Long taskKey, Double shortQuantity, String reason, String shortedBy);

    @ActivityMethod
    void cancelTask(Long taskKey, String reason, String cancelledBy);

    @ActivityMethod
    void holdTask(Long taskKey, String reason, String heldBy);

    @ActivityMethod
    void releaseTaskFromHold(Long taskKey, String releasedBy);
}
