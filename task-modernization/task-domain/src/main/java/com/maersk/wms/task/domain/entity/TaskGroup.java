package com.maersk.wms.task.domain.entity;

import com.maersk.wms.task.domain.enums.TaskGroupStatus;
import com.maersk.wms.task.domain.enums.TaskGroupType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Task Group entity for grouping related tasks together.
 * Represents a logical grouping like a pick wave, replenishment batch, or cycle count batch.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskGroup {

    private Long taskGroupKey;

    @NotBlank(message = "Task group ID is required")
    private String taskGroupId;

    @NotNull(message = "Task group type is required")
    private TaskGroupType groupType;

    @NotNull(message = "Task group status is required")
    private TaskGroupStatus status;

    private String description;

    // Assignment
    private String assignedUserId;
    private String assignedUserName;
    private String workGroup;

    // Task statistics
    private Integer totalTasks;
    private Integer completedTasks;
    private Integer pendingTasks;
    private Integer inProgressTasks;
    private Integer failedTasks;

    // Quantity statistics
    private Double totalQuantity;
    private Double completedQuantity;

    // Timing
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime dueDate;
    private Integer estimatedMinutes;
    private Integer actualMinutes;

    // Reference
    private String waveId;
    private String routeId;
    private String batchId;

    // Related tasks
    private List<Task> tasks;

    // Audit
    private String clientCode;
    private String facilityCode;
    private String createdBy;
    private String modifiedBy;
    private LocalDateTime modifiedAt;
    private Integer version;
}
