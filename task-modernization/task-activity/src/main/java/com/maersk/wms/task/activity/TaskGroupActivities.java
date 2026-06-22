package com.maersk.wms.task.activity;

import com.maersk.wms.task.domain.entity.Task;
import com.maersk.wms.task.domain.entity.TaskGroup;
import com.maersk.wms.task.domain.enums.TaskGroupStatus;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

import java.util.List;

/**
 * Temporal activity interface for task group operations.
 */
@ActivityInterface
public interface TaskGroupActivities {

    @ActivityMethod
    TaskGroup createTaskGroup(TaskGroup taskGroup);

    @ActivityMethod
    TaskGroup getTaskGroup(Long taskGroupKey);

    @ActivityMethod
    void updateTaskGroupStatus(Long taskGroupKey, TaskGroupStatus status, String updatedBy);

    @ActivityMethod
    void addTaskToGroup(Long taskGroupKey, Long taskKey);

    @ActivityMethod
    void removeTaskFromGroup(Long taskGroupKey, Long taskKey);

    @ActivityMethod
    List<Task> getTasksInGroup(Long taskGroupKey);

    @ActivityMethod
    void updateTaskGroupMetrics(Long taskGroupKey);

    @ActivityMethod
    void releaseTaskGroup(Long taskGroupKey, String releasedBy);

    @ActivityMethod
    void completeTaskGroup(Long taskGroupKey, String completedBy);

    @ActivityMethod
    void cancelTaskGroup(Long taskGroupKey, String reason, String cancelledBy);

    @ActivityMethod
    boolean areAllTasksComplete(Long taskGroupKey);
}
