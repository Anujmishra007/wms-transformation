package com.maersk.wms.task.domain.entity;

import com.maersk.wms.task.domain.enums.WorkGroupStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Work Group entity representing a team or group of workers.
 * Used for task assignment and workload balancing.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkGroup {

    private Long workGroupKey;

    @NotBlank(message = "Work group code is required")
    private String workGroupCode;

    private String description;

    @NotNull(message = "Work group status is required")
    private WorkGroupStatus status;

    // Capacity
    private Integer maxUsers;
    private Integer currentUsers;
    private Integer maxTasks;
    private Integer currentTasks;

    // Work zones assigned to this group
    private List<String> workZones;

    // Task types this group can handle
    private List<String> allowedTaskTypes;

    // Priority settings
    private Integer defaultPriority;
    private Boolean autoAssignEnabled;
    private String assignmentStrategy; // ROUND_ROBIN, LEAST_LOADED, ZONE_BASED

    // Shift information
    private String shiftCode;
    private LocalDateTime shiftStartTime;
    private LocalDateTime shiftEndTime;

    // Supervisor
    private String supervisorUserId;
    private String supervisorName;

    // Audit
    private String clientCode;
    private String facilityCode;
    private String createdBy;
    private String modifiedBy;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Integer version;
}
