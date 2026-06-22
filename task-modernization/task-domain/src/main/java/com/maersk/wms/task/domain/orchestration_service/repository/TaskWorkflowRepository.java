package com.maersk.wms.task.domain.orchestration_service.repository;

import com.maersk.wms.task.domain.orchestration_service.model.TaskWorkflow;

import java.util.List;
import java.util.Optional;

/**
 * Repository for TaskWorkflow aggregate.
 */
public interface TaskWorkflowRepository {

    TaskWorkflow save(TaskWorkflow workflow);
    Optional<TaskWorkflow> findById(String workflowKey);
    void delete(String workflowKey);

    List<TaskWorkflow> findByStatus(TaskWorkflow.WorkflowStatus status);
    List<TaskWorkflow> findBySourceTypeAndKey(String sourceType, String sourceKey);
    List<TaskWorkflow> findActive();

    int countByStatus(TaskWorkflow.WorkflowStatus status);
}
