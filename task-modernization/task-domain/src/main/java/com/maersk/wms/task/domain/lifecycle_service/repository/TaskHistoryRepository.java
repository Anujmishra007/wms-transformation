package com.maersk.wms.task.domain.lifecycle_service.repository;

import com.maersk.wms.task.domain.lifecycle_service.model.TaskHistory;
import com.maersk.wms.task.shared.kernel.identifiers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for TaskHistory entity.
 */
public interface TaskHistoryRepository {

    TaskHistory save(TaskHistory history);
    Optional<TaskHistory> findById(HistoryKey historyKey);

    List<TaskHistory> findByTask(TaskKey taskKey);
    List<TaskHistory> findByUser(UserKey userId, LocalDateTime from, LocalDateTime to);
    List<TaskHistory> findByAction(TaskHistory.HistoryAction action, LocalDateTime from, LocalDateTime to);
    List<TaskHistory> findByDateRange(LocalDateTime from, LocalDateTime to);
}
