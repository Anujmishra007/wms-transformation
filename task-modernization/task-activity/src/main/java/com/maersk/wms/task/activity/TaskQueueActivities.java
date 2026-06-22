package com.maersk.wms.task.activity;

import com.maersk.wms.task.domain.entity.Task;
import com.maersk.wms.task.domain.entity.TaskQueue;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

import java.util.List;

/**
 * Temporal activity interface for task queue operations.
 */
@ActivityInterface
public interface TaskQueueActivities {

    @ActivityMethod
    void enqueueTask(String queueCode, Task task);

    @ActivityMethod
    Task dequeueTask(String queueCode);

    @ActivityMethod
    List<Task> dequeueTasks(String queueCode, int count);

    @ActivityMethod
    Task peekNextTask(String queueCode);

    @ActivityMethod
    int getQueueDepth(String queueCode);

    @ActivityMethod
    void updateQueueMetrics(String queueCode);

    @ActivityMethod
    TaskQueue getQueueStatus(String queueCode);

    @ActivityMethod
    void pauseQueue(String queueCode, String reason);

    @ActivityMethod
    void resumeQueue(String queueCode);

    @ActivityMethod
    void drainQueue(String queueCode);

    @ActivityMethod
    List<Task> getQueuedTasks(String queueCode, int limit);
}
