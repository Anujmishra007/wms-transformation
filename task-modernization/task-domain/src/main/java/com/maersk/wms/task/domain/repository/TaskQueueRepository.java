package com.maersk.wms.task.domain.repository;

import com.maersk.wms.task.domain.entity.TaskQueue;
import com.maersk.wms.task.domain.enums.QueueStatus;
import com.maersk.wms.task.domain.enums.TaskType;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for TaskQueue entity operations.
 */
public interface TaskQueueRepository {

    TaskQueue save(TaskQueue queue);

    Optional<TaskQueue> findByQueueKey(Long queueKey);

    Optional<TaskQueue> findByQueueCode(String queueCode);

    List<TaskQueue> findByStatus(QueueStatus status);

    List<TaskQueue> findActiveQueues();

    List<TaskQueue> findByTaskType(TaskType taskType);

    List<TaskQueue> findByWorkZone(String workZone);

    List<TaskQueue> findByWorkGroup(String workGroup);

    List<TaskQueue> findQueuesWithCapacity();

    List<TaskQueue> findScheduledQueues();

    int countByStatus(QueueStatus status);

    void updateStatus(Long queueKey, QueueStatus status, String modifiedBy);

    void updateQueueMetrics(Long queueKey, int pendingTasks, int inProgressTasks, int completedTasks);

    void incrementPendingTasks(Long queueKey);

    void decrementPendingTasks(Long queueKey);

    void deleteByQueueKey(Long queueKey);

    boolean existsByQueueCode(String queueCode);
}
