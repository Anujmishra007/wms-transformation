package com.maersk.wms.task.service.orchestration;

import com.maersk.wms.task.domain.orchestration_service.model.*;
import com.maersk.wms.task.domain.orchestration_service.repository.*;
import com.maersk.wms.task.domain.orchestration_service.service.TaskOrchestrationService;
import com.maersk.wms.task.domain.orchestration_service.event.OrchestrationEvents;
import com.maersk.wms.task.shared.kernel.identifiers.*;
import com.maersk.wms.task.shared.kernel.exceptions.DependencyNotFoundException;
import com.maersk.wms.task.shared.kernel.exceptions.WorkflowNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of Task Orchestration Service.
 * Manages task dependencies and multi-step workflows.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TaskOrchestrationServiceImpl implements TaskOrchestrationService {

    private final TaskDependencyRepository dependencyRepository;
    private final TaskWorkflowRepository workflowRepository;
    private final ApplicationEventPublisher eventPublisher;

    // ==================== Dependency Management ====================

    @Override
    public TaskDependency createDependency(TaskKey predecessorTask, TaskKey successorTask,
                                            TaskDependency.DependencyType type) {
        return createDependencyWithLag(predecessorTask, successorTask, type, 0);
    }

    @Override
    public TaskDependency createDependencyWithLag(TaskKey predecessorTask, TaskKey successorTask,
                                                   TaskDependency.DependencyType type, int lagMinutes) {
        log.info("Creating dependency: {} -> {} (type: {}, lag: {} min)",
                predecessorTask.value(), successorTask.value(), type, lagMinutes);

        DependencyKey dependencyKey = new DependencyKey(UUID.randomUUID().toString());

        TaskDependency dependency = TaskDependency.builder()
                .dependencyKey(dependencyKey)
                .predecessorTask(predecessorTask)
                .successorTask(successorTask)
                .type(type)
                .lagMinutes(lagMinutes)
                .status(TaskDependency.DependencyStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        TaskDependency saved = dependencyRepository.save(dependency);

        eventPublisher.publishEvent(new OrchestrationEvents.DependencyCreated(
                dependencyKey, predecessorTask, successorTask, type.name(), Instant.now(), "SYSTEM"
        ));

        log.info("Created dependency {}", dependencyKey.value());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public TaskDependency getDependency(DependencyKey dependencyKey) {
        return dependencyRepository.findById(dependencyKey)
                .orElseThrow(() -> new DependencyNotFoundException(dependencyKey.value()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDependency> getDependenciesForTask(TaskKey taskKey) {
        List<TaskDependency> asPredecessor = dependencyRepository.findByPredecessor(taskKey);
        List<TaskDependency> asSuccessor = dependencyRepository.findBySuccessor(taskKey);

        List<TaskDependency> all = new ArrayList<>();
        all.addAll(asPredecessor);
        all.addAll(asSuccessor);
        return all;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDependency> getPredecessors(TaskKey taskKey) {
        return dependencyRepository.findBySuccessor(taskKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDependency> getSuccessors(TaskKey taskKey) {
        return dependencyRepository.findByPredecessor(taskKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDependency> getBlockingDependencies(TaskKey taskKey) {
        return getPredecessors(taskKey).stream()
                .filter(d -> d.getStatus() == TaskDependency.DependencyStatus.PENDING ||
                             d.getStatus() == TaskDependency.DependencyStatus.BLOCKED)
                .collect(Collectors.toList());
    }

    @Override
    public void resolveDependency(DependencyKey dependencyKey) {
        log.info("Resolving dependency {}", dependencyKey.value());

        TaskDependency dependency = getDependency(dependencyKey);
        dependency.resolve();
        dependencyRepository.save(dependency);

        eventPublisher.publishEvent(new OrchestrationEvents.DependencySatisfied(
                dependencyKey, dependency.getPredecessorTask(),
                dependency.getSuccessorTask(), Instant.now()
        ));

        // Check if successor is now unblocked
        List<TaskDependency> remaining = getBlockingDependencies(dependency.getSuccessorTask());
        if (remaining.isEmpty()) {
            eventPublisher.publishEvent(new OrchestrationEvents.TaskUnblocked(
                    dependency.getSuccessorTask(),
                    List.of(dependencyKey),
                    Instant.now()
            ));
        }

        log.info("Resolved dependency {}", dependencyKey.value());
    }

    @Override
    public void blockDependency(DependencyKey dependencyKey) {
        log.info("Blocking dependency {}", dependencyKey.value());

        TaskDependency dependency = getDependency(dependencyKey);
        dependency.block();
        dependencyRepository.save(dependency);

        log.info("Blocked dependency {}", dependencyKey.value());
    }

    @Override
    public void overrideDependency(DependencyKey dependencyKey, String overriddenBy) {
        log.info("Overriding dependency {} by {}", dependencyKey.value(), overriddenBy);

        TaskDependency dependency = getDependency(dependencyKey);
        dependency.override(overriddenBy);
        dependencyRepository.save(dependency);

        log.info("Overridden dependency {}", dependencyKey.value());
    }

    @Override
    public void deleteDependency(DependencyKey dependencyKey) {
        log.info("Deleting dependency {}", dependencyKey.value());

        getDependency(dependencyKey); // Verify exists
        dependencyRepository.delete(dependencyKey);

        eventPublisher.publishEvent(new OrchestrationEvents.DependencyRemoved(
                dependencyKey, "Deleted", Instant.now(), "SYSTEM"
        ));

        log.info("Deleted dependency {}", dependencyKey.value());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canTaskStart(TaskKey taskKey) {
        return getBlockingDependencies(taskKey).isEmpty();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskKey> getReadyTasks(List<TaskKey> taskKeys) {
        return taskKeys.stream()
                .filter(this::canTaskStart)
                .collect(Collectors.toList());
    }

    // ==================== Workflow Management ====================

    @Override
    public TaskWorkflow createWorkflow(String name, String sourceType, String sourceKey,
                                        List<TaskWorkflow.WorkflowStep> steps) {
        log.info("Creating workflow '{}' for source {}/{}", name, sourceType, sourceKey);

        String workflowKey = UUID.randomUUID().toString();

        TaskWorkflow workflow = TaskWorkflow.builder()
                .workflowKey(workflowKey)
                .name(name)
                .sourceType(sourceType)
                .sourceKey(sourceKey)
                .steps(new ArrayList<>(steps))
                .currentStep(0)
                .totalSteps(steps.size())
                .status(TaskWorkflow.WorkflowStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();

        TaskWorkflow saved = workflowRepository.save(workflow);

        log.info("Created workflow {}", workflowKey);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public TaskWorkflow getWorkflow(String workflowKey) {
        return workflowRepository.findById(workflowKey)
                .orElseThrow(() -> new WorkflowNotFoundException(workflowKey));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskWorkflow> getWorkflowsByStatus(TaskWorkflow.WorkflowStatus status) {
        return workflowRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskWorkflow> getWorkflowsBySource(String sourceType, String sourceKey) {
        return workflowRepository.findBySourceTypeAndKey(sourceType, sourceKey);
    }

    @Override
    public void startWorkflow(String workflowKey) {
        log.info("Starting workflow {}", workflowKey);

        TaskWorkflow workflow = getWorkflow(workflowKey);
        workflow.start();
        workflowRepository.save(workflow);

        List<TaskKey> initialTasks = workflow.getSteps().stream()
                .filter(s -> s.getStepNumber() == 1)
                .map(TaskWorkflow.WorkflowStep::getTaskKey)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        eventPublisher.publishEvent(new OrchestrationEvents.WorkflowStarted(
                workflowKey, workflow.getName(),
                workflow.getSourceType(), workflow.getSourceKey(),
                initialTasks, Instant.now(), "SYSTEM"
        ));

        log.info("Started workflow {}", workflowKey);
    }

    @Override
    public void completeCurrentStep(String workflowKey) {
        log.info("Completing current step for workflow {}", workflowKey);

        TaskWorkflow workflow = getWorkflow(workflowKey);
        TaskWorkflow.WorkflowStep currentStep = workflow.getCurrentStepDetails();

        workflow.completeCurrentStep();
        workflowRepository.save(workflow);

        eventPublisher.publishEvent(new OrchestrationEvents.WorkflowStepCompleted(
                workflowKey, currentStep.getStepNumber(), currentStep.getStepName(),
                currentStep.getTaskKey(), Map.of(), Instant.now()
        ));

        if (workflow.getStatus() == TaskWorkflow.WorkflowStatus.COMPLETED) {
            eventPublisher.publishEvent(new OrchestrationEvents.WorkflowCompleted(
                    workflowKey, workflow.getSteps().size(),
                    workflow.getCurrentStep(), Map.of(), Instant.now()
            ));
        }

        log.info("Completed step {} for workflow {}", currentStep.getStepNumber(), workflowKey);
    }

    @Override
    public void failCurrentStep(String workflowKey, String error) {
        log.info("Failing current step for workflow {} - error: {}", workflowKey, error);

        TaskWorkflow workflow = getWorkflow(workflowKey);
        TaskWorkflow.WorkflowStep currentStep = workflow.getCurrentStepDetails();

        workflow.failCurrentStep(error);
        workflowRepository.save(workflow);

        eventPublisher.publishEvent(new OrchestrationEvents.WorkflowStepFailed(
                workflowKey, currentStep.getStepNumber(), currentStep.getStepName(),
                currentStep.getTaskKey(), "STEP_FAILED", error, Instant.now()
        ));

        log.info("Failed step {} for workflow {}", currentStep.getStepNumber(), workflowKey);
    }

    @Override
    public void retryCurrentStep(String workflowKey) {
        log.info("Retrying current step for workflow {}", workflowKey);

        TaskWorkflow workflow = getWorkflow(workflowKey);
        workflow.retry();
        workflowRepository.save(workflow);

        log.info("Retried step for workflow {}", workflowKey);
    }

    @Override
    public void cancelWorkflow(String workflowKey) {
        log.info("Cancelling workflow {}", workflowKey);

        TaskWorkflow workflow = getWorkflow(workflowKey);
        int completedSteps = workflow.getCompletedSteps();
        int remainingSteps = workflow.getSteps().size() - completedSteps;

        workflow.cancel();
        workflowRepository.save(workflow);

        eventPublisher.publishEvent(new OrchestrationEvents.WorkflowCancelled(
                workflowKey, "User request", completedSteps, remainingSteps, Instant.now(), "SYSTEM"
        ));

        log.info("Cancelled workflow {}", workflowKey);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TaskWorkflow.WorkflowStep> getCurrentStep(String workflowKey) {
        TaskWorkflow workflow = getWorkflow(workflowKey);
        return Optional.ofNullable(workflow.getCurrentStepDetails());
    }

    @Override
    @Transactional(readOnly = true)
    public double getWorkflowProgress(String workflowKey) {
        TaskWorkflow workflow = getWorkflow(workflowKey);
        return workflow.getProgressPercent();
    }

    // ==================== Cross-Service Coordination ====================

    @Override
    public void notifyTaskCreated(TaskKey taskKey, String sourceService) {
        log.info("Task created notification: {} from {}", taskKey.value(), sourceService);

        // Auto-resolve dependencies where this task is the predecessor
        List<TaskDependency> asSuccessor = dependencyRepository.findByPredecessor(taskKey);
        // These will be resolved when task completes, not on creation
    }

    @Override
    public void notifyTaskCompleted(TaskKey taskKey, String sourceService) {
        log.info("Task completed notification: {} from {}", taskKey.value(), sourceService);

        // Resolve all dependencies where this task is the predecessor
        List<TaskDependency> dependencies = dependencyRepository.findByPredecessor(taskKey);
        dependencies.forEach(d -> {
            if (d.getStatus() == TaskDependency.DependencyStatus.PENDING) {
                resolveDependency(d.getDependencyKey());
            }
        });

        // Update any workflows containing this task
        // This would typically query workflows that include this task
    }

    @Override
    public void notifyTaskFailed(TaskKey taskKey, String sourceService, String error) {
        log.info("Task failed notification: {} from {} - error: {}",
                taskKey.value(), sourceService, error);

        // Block all dependencies where this task is the predecessor
        List<TaskDependency> dependencies = dependencyRepository.findByPredecessor(taskKey);
        dependencies.forEach(d -> {
            if (d.getStatus() == TaskDependency.DependencyStatus.PENDING) {
                d.block();
                dependencyRepository.save(d);

                eventPublisher.publishEvent(new OrchestrationEvents.DependencyViolated(
                        d.getDependencyKey(), taskKey, "PREDECESSOR_FAILED", Instant.now()
                ));
            }
        });
    }

    // ==================== Event Processing ====================

    @Override
    public void processInboundEvent(String eventType, String eventKey, String payload) {
        log.info("Processing inbound event: {} - {}", eventType, eventKey);
        // Process events from inbound service (receiving, putaway)
        processExternalEvent(eventType, eventKey, payload, "INBOUND");
    }

    @Override
    public void processOutboundEvent(String eventType, String eventKey, String payload) {
        log.info("Processing outbound event: {} - {}", eventType, eventKey);
        // Process events from outbound service (picking, shipping)
        processExternalEvent(eventType, eventKey, payload, "OUTBOUND");
    }

    @Override
    public void processInventoryEvent(String eventType, String eventKey, String payload) {
        log.info("Processing inventory event: {} - {}", eventType, eventKey);
        // Process events from inventory service (moves, adjustments)
        processExternalEvent(eventType, eventKey, payload, "INVENTORY");
    }

    private void processExternalEvent(String eventType, String eventKey, String payload, String source) {
        // Route to appropriate workflow or dependency handler based on event type
        switch (eventType) {
            case "TASK_COMPLETED" -> {
                TaskKey taskKey = new TaskKey(eventKey);
                notifyTaskCompleted(taskKey, source);
            }
            case "TASK_FAILED" -> {
                TaskKey taskKey = new TaskKey(eventKey);
                notifyTaskFailed(taskKey, source, payload);
            }
            case "TASK_CREATED" -> {
                TaskKey taskKey = new TaskKey(eventKey);
                notifyTaskCreated(taskKey, source);
            }
            default -> log.warn("Unknown event type: {}", eventType);
        }
    }
}
