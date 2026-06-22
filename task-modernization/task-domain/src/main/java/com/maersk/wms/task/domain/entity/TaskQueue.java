package com.maersk.wms.task.domain.entity;

import com.maersk.wms.task.domain.enums.QueueStatus;
import com.maersk.wms.task.domain.enums.TaskType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Task Queue entity representing a queue of tasks for processing.
 * Queues are used to organize and prioritize work distribution.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskQueue {

    private Long queueKey;

    @NotBlank(message = "Queue code is required")
    private String queueCode;

    private String queueName;
    private String description;

    @NotNull(message = "Queue status is required")
    private QueueStatus status;

    // Queue configuration
    private TaskType taskType;
    private String workZone;
    private String workGroup;

    // Queue metrics
    private Integer pendingTasks;
    private Integer inProgressTasks;
    private Integer completedTasks;
    private Integer totalCapacity;
    private Double avgProcessingTime;

    // Priority settings
    private Integer basePriority;
    private Boolean priorityEscalationEnabled;
    private Integer escalationThresholdMinutes;
    private Integer escalationIncrement;

    // Processing rules
    private String processingOrder; // FIFO, LIFO, PRIORITY, ZONE_OPTIMIZED
    private Boolean batchingEnabled;
    private Integer batchSize;
    private Integer maxConcurrentTasks;

    // Work groups that can process this queue
    private List<String> allowedWorkGroups;

    // Schedule
    private Boolean scheduleEnabled;
    private String scheduleExpression;
    private LocalDateTime nextScheduledRun;

    // Audit
    private String clientCode;
    private String facilityCode;
    private String createdBy;
    private String modifiedBy;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Integer version;
}
