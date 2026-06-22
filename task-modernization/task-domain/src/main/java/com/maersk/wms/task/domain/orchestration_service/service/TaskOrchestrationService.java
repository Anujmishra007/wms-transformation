package com.maersk.wms.task.domain.orchestration_service.service;

import com.maersk.wms.task.domain.orchestration_service.model.*;
import com.maersk.wms.task.shared.kernel.identifiers.*;

import java.util.List;
import java.util.Optional;

/**
 * Task Orchestration Service - manages task dependencies and workflows.
 */
public interface TaskOrchestrationService {

    // Dependency Management
    TaskDependency createDependency(TaskKey predecessorTask, TaskKey successorTask,
                                     TaskDependency.DependencyType type);
    TaskDependency createDependencyWithLag(TaskKey predecessorTask, TaskKey successorTask,
                                            TaskDependency.DependencyType type, int lagMinutes);

    TaskDependency getDependency(DependencyKey dependencyKey);
    List<TaskDependency> getDependenciesForTask(TaskKey taskKey);
    List<TaskDependency> getPredecessors(TaskKey taskKey);
    List<TaskDependency> getSuccessors(TaskKey taskKey);
    List<TaskDependency> getBlockingDependencies(TaskKey taskKey);

    void resolveDependency(DependencyKey dependencyKey);
    void blockDependency(DependencyKey dependencyKey);
    void overrideDependency(DependencyKey dependencyKey, String overriddenBy);
    void deleteDependency(DependencyKey dependencyKey);

    boolean canTaskStart(TaskKey taskKey);
    List<TaskKey> getReadyTasks(List<TaskKey> taskKeys);

    // Workflow Management
    TaskWorkflow createWorkflow(String name, String sourceType, String sourceKey,
                                 List<TaskWorkflow.WorkflowStep> steps);
    TaskWorkflow getWorkflow(String workflowKey);
    List<TaskWorkflow> getWorkflowsByStatus(TaskWorkflow.WorkflowStatus status);
    List<TaskWorkflow> getWorkflowsBySource(String sourceType, String sourceKey);

    void startWorkflow(String workflowKey);
    void completeCurrentStep(String workflowKey);
    void failCurrentStep(String workflowKey, String error);
    void retryCurrentStep(String workflowKey);
    void cancelWorkflow(String workflowKey);

    Optional<TaskWorkflow.WorkflowStep> getCurrentStep(String workflowKey);
    double getWorkflowProgress(String workflowKey);

    // Cross-Service Coordination
    void notifyTaskCreated(TaskKey taskKey, String sourceService);
    void notifyTaskCompleted(TaskKey taskKey, String sourceService);
    void notifyTaskFailed(TaskKey taskKey, String sourceService, String error);

    // Event Processing
    void processInboundEvent(String eventType, String eventKey, String payload);
    void processOutboundEvent(String eventType, String eventKey, String payload);
    void processInventoryEvent(String eventType, String eventKey, String payload);
}
