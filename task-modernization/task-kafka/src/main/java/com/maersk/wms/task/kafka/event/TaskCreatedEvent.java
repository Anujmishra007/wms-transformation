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
public class TaskCreatedEvent {
    private String eventId;
    private LocalDateTime eventTime;
    private Long taskKey;
    private String taskId;
    private String taskType;
    private String priority;
    private String sourceLocation;
    private String destinationLocation;
    private Double quantity;
    private String clientCode;
    private String facilityCode;
}
