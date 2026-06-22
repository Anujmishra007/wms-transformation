package com.maersk.wms.task.domain.entity;

import com.maersk.wms.task.domain.enums.TaskStatus;
import com.maersk.wms.task.domain.enums.TaskType;
import com.maersk.wms.task.domain.enums.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Core Task entity representing a unit of work in the warehouse.
 * Maps to TASKDETAIL table in WMS database.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    private Long taskKey;

    @NotBlank(message = "Task ID is required")
    private String taskId;

    @NotNull(message = "Task type is required")
    private TaskType taskType;

    @NotNull(message = "Task status is required")
    private TaskStatus status;

    @NotNull(message = "Task priority is required")
    private TaskPriority priority;

    // Source information
    private String sourceLocation;
    private String sourceZone;
    private String sourceLpn;

    // Destination information
    private String destinationLocation;
    private String destinationZone;
    private String destinationLpn;

    // Item information
    private String sku;
    private String itemDescription;
    private Double quantity;
    private String uom;

    // Assignment
    private String assignedUserId;
    private String assignedUserName;
    private String assignedEquipment;
    private String workGroup;
    private String workZone;

    // Reference keys
    private Long orderKey;
    private String orderNumber;
    private Long orderDetailKey;
    private Long pickDetailKey;
    private Long receiptKey;
    private Long receiptDetailKey;
    private Long moveKey;

    // Timing
    private LocalDateTime createdAt;
    private LocalDateTime assignedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime dueDate;
    private Integer estimatedMinutes;
    private Integer actualMinutes;

    // Sequence and routing
    private Integer sequenceNumber;
    private Integer pickPathSequence;
    private String routeId;
    private String waveId;

    // Quantity tracking
    private Double originalQuantity;
    private Double pickedQuantity;
    private Double shortQuantity;
    private Double damagedQuantity;

    // Audit
    private String clientCode;
    private String facilityCode;
    private String createdBy;
    private String modifiedBy;
    private LocalDateTime modifiedAt;
    private Integer version;
}
