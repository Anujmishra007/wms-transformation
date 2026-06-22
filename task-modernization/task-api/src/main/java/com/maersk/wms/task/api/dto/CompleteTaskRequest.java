package com.maersk.wms.task.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompleteTaskRequest {

    @NotNull(message = "Completed quantity is required")
    @PositiveOrZero(message = "Completed quantity must be zero or positive")
    private Double completedQuantity;

    private Double shortQuantity;
    private String shortReason;
    private String completionNotes;
}
