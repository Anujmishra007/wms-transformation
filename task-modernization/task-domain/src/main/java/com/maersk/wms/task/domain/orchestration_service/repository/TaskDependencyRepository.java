package com.maersk.wms.task.domain.orchestration_service.repository;

import com.maersk.wms.task.domain.orchestration_service.model.TaskDependency;
import com.maersk.wms.task.shared.kernel.identifiers.*;

import java.util.List;
import java.util.Optional;

/**
 * Repository for TaskDependency entity.
 */
public interface TaskDependencyRepository {

    TaskDependency save(TaskDependency dependency);
    Optional<TaskDependency> findById(DependencyKey dependencyKey);
    void delete(DependencyKey dependencyKey);

    List<TaskDependency> findByPredecessor(TaskKey predecessorTask);
    List<TaskDependency> findBySuccessor(TaskKey successorTask);
    List<TaskDependency> findByTask(TaskKey taskKey);
    List<TaskDependency> findBlockingDependencies(TaskKey taskKey);
    List<TaskDependency> findByStatus(TaskDependency.DependencyStatus status);

    boolean hasUnresolvedDependencies(TaskKey taskKey);
}
