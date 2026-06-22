package com.maersk.wms.task.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCompletedEvent {
    private String eventId;
    private LocalDateTime eventTime;
    private Long taskKey;
    private String taskId;
    private String userId;
    private Double completedQuantity;
    private Double shortQuantity;
    private LocalDateTime completedAt;
    private Integer actualMinutes;
    private String clientCode;
    private String facilityCode;
}
