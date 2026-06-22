package com.maersk.wms.task.api.dto;

import com.maersk.wms.task.domain.enums.TaskType;
import com.maersk.wms.task.domain.enums.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskRequest {

    @NotBlank(message = "Task ID is required")
    private String taskId;

    @NotNull(message = "Task type is required")
    private TaskType taskType;

    private TaskPriority priority;

    private String sourceLocation;
    private String sourceZone;
    private String sourceLpn;

    private String destinationLocation;
    private String destinationZone;
    private String destinationLpn;

    private String sku;

    @Positive(message = "Quantity must be positive")
    private Double quantity;

    private String workGroup;
    private String workZone;

    private Long orderKey;
    private String waveId;
    private LocalDateTime dueDate;
}
