package com.maersk.wms.task.api.dto;

import com.maersk.wms.task.domain.enums.TaskType;
import com.maersk.wms.task.domain.enums.TaskStatus;
import com.maersk.wms.task.domain.enums.TaskPriority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

    private Long taskKey;
    private String taskId;
    private TaskType taskType;
    private TaskStatus status;
    private TaskPriority priority;

    private String sourceLocation;
    private String sourceZone;
    private String destinationLocation;
    private String destinationZone;

    private String sku;
    private Double quantity;
    private Double pickedQuantity;
    private Double shortQuantity;

    private String assignedUserId;
    private String assignedUserName;
    private String workGroup;
    private String workZone;

    private LocalDateTime createdAt;
    private LocalDateTime assignedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}
