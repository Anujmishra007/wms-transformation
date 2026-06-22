package com.maersk.wms.task.domain.entity;

import com.maersk.wms.task.domain.enums.AssignmentStatus;
import com.maersk.wms.task.domain.enums.AssignmentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Task Assignment entity tracking the assignment of tasks to users or equipment.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskAssignment {

    private Long assignmentKey;

    @NotBlank(message = "Assignment ID is required")
    private String assignmentId;

    @NotNull(message = "Assignment type is required")
    private AssignmentType assignmentType;

    @NotNull(message = "Assignment status is required")
    private AssignmentStatus status;

    // Task reference
    private Long taskKey;
    private String taskId;
    private Long taskGroupKey;
    private String taskGroupId;

    // Assignee information
    @NotBlank(message = "User ID is required")
    private String userId;
    private String userName;
    private String equipmentId;
    private String equipmentType;

    // Work assignment
    private String workGroup;
    private String workZone;
    private String workArea;

    // Assignment timing
    private LocalDateTime assignedAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime releasedAt;

    // Priority
    private Integer priority;
    private Integer sequenceNumber;

    // Reason codes
    private String assignmentReason;
    private String releaseReason;
    private String completionReason;

    // Audit
    private String clientCode;
    private String facilityCode;
    private String assignedBy;
    private String createdBy;
    private String modifiedBy;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Integer version;
}
