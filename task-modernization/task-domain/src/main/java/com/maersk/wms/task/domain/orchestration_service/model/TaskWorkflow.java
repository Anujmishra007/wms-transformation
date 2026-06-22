package com.maersk.wms.task.domain.orchestration_service.model;

import com.maersk.wms.task.shared.kernel.identifiers.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * TaskWorkflow entity - defines multi-step task workflows.
 * Part of Task Orchestration bounded context.
 */
@Data
@Builder
public class TaskWorkflow {

    private String workflowKey;
    private String name;
    private String description;
    private WorkflowStatus status;

    // Workflow definition
    @Builder.Default
    private List<WorkflowStep> steps = new ArrayList<>();
    private int currentStep;
    private int totalSteps;

    // Source context
    private String sourceType;
    private String sourceKey;

    // Progress
    private int completedSteps;
    private double progressPercent;

    // Timing
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime estimatedEndTime;

    // Error handling
    private String lastError;
    private int retryCount;
    private int maxRetries;

    // Audit
    private LocalDateTime createdAt;

    public enum WorkflowStatus {
        CREATED, RUNNING, PAUSED, COMPLETED, FAILED, CANCELLED
    }

    @Data
    @Builder
    public static class WorkflowStep {
        private int stepNumber;
        private String stepName;
        private TaskKey taskKey;
        private StepStatus status;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String errorMessage;

        public enum StepStatus {
            PENDING, IN_PROGRESS, COMPLETED, FAILED, SKIPPED
        }
    }

    public void start() {
        this.status = WorkflowStatus.RUNNING;
        this.startTime = LocalDateTime.now();
        this.currentStep = 1;
    }

    public void completeCurrentStep() {
        if (currentStep <= steps.size()) {
            WorkflowStep step = steps.get(currentStep - 1);
            step.setStatus(WorkflowStep.StepStatus.COMPLETED);
            step.setEndTime(LocalDateTime.now());
            completedSteps++;
            currentStep++;
            updateProgress();

            if (currentStep > totalSteps) {
                complete();
            }
        }
    }

    public void failCurrentStep(String error) {
        if (currentStep <= steps.size()) {
            WorkflowStep step = steps.get(currentStep - 1);
            step.setStatus(WorkflowStep.StepStatus.FAILED);
            step.setErrorMessage(error);
            this.lastError = error;

            if (retryCount >= maxRetries) {
                this.status = WorkflowStatus.FAILED;
            }
        }
    }

    public void retry() {
        if (retryCount < maxRetries) {
            retryCount++;
            WorkflowStep step = steps.get(currentStep - 1);
            step.setStatus(WorkflowStep.StepStatus.PENDING);
            step.setErrorMessage(null);
        }
    }

    public void complete() {
        this.status = WorkflowStatus.COMPLETED;
        this.endTime = LocalDateTime.now();
        this.progressPercent = 100.0;
    }

    public void cancel() {
        this.status = WorkflowStatus.CANCELLED;
        this.endTime = LocalDateTime.now();
    }

    private void updateProgress() {
        if (totalSteps > 0) {
            progressPercent = ((double) completedSteps / totalSteps) * 100;
        }
    }

    public WorkflowStep getCurrentStepDetails() {
        if (currentStep > 0 && currentStep <= steps.size()) {
            return steps.get(currentStep - 1);
        }
        return null;
    }
}
