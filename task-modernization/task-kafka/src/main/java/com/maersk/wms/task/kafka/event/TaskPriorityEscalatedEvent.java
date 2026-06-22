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
public class TaskPriorityEscalatedEvent {
    private String eventId;
    private LocalDateTime eventTime;
    private Long taskKey;
    private String taskId;
    private String previousPriority;
    private String newPriority;
    private String clientCode;
    private String facilityCode;
}
