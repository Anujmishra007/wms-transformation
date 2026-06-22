package com.maersk.wms.task.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * User Workload entity tracking current workload for a user.
 * Used for load balancing and task assignment optimization.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserWorkload {

    private Long workloadKey;

    private String userId;
    private String userName;

    // Current shift
    private String workGroup;
    private String workZone;
    private String shiftCode;
    private LocalDateTime shiftStartTime;
    private LocalDateTime shiftEndTime;

    // Equipment
    private String currentEquipmentId;
    private String equipmentType;

    // Current location
    private String currentLocation;
    private String currentZone;
    private String currentAisle;

    // Workload metrics
    private Integer assignedTasks;
    private Integer completedTasks;
    private Integer pendingTasks;
    private Integer inProgressTasks;

    // Quantity metrics
    private Double totalAssignedQuantity;
    private Double totalCompletedQuantity;
    private Integer totalLines;
    private Integer completedLines;

    // Time metrics
    private Integer totalEstimatedMinutes;
    private Integer totalActualMinutes;
    private Double productivityRate; // Tasks per hour
    private Double accuracy; // Percentage

    // Current task
    private Long currentTaskKey;
    private String currentTaskId;
    private LocalDateTime currentTaskStartedAt;

    // Capacity
    private Integer maxConcurrentTasks;
    private Double utilizationPercent;

    // Status
    private Boolean isAvailable;
    private Boolean isOnBreak;
    private LocalDateTime lastActivityAt;

    // Allowed task types for this user
    private List<String> allowedTaskTypes;

    // Audit
    private String clientCode;
    private String facilityCode;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Integer version;
}
