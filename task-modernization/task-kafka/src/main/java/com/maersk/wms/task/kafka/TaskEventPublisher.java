package com.maersk.wms.task.kafka;

import com.maersk.wms.task.domain.entity.Task;
import com.maersk.wms.task.kafka.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Publishes task domain events to Kafka.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskEventPublisher {

    private static final String TASK_EVENTS_TOPIC = "wms.task.events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishTaskCreated(Task task) {
        TaskCreatedEvent event = TaskCreatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventTime(LocalDateTime.now())
                .taskKey(task.getTaskKey())
                .taskId(task.getTaskId())
                .taskType(task.getTaskType().name())
                .priority(task.getPriority().name())
                .sourceLocation(task.getSourceLocation())
                .destinationLocation(task.getDestinationLocation())
                .quantity(task.getQuantity())
                .clientCode(task.getClientCode())
                .facilityCode(task.getFacilityCode())
                .build();

        publish(task.getTaskId(), event);
        log.info("Published TaskCreatedEvent for task: {}", task.getTaskId());
    }

    public void publishTaskAssigned(Task task) {
        TaskAssignedEvent event = TaskAssignedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventTime(LocalDateTime.now())
                .taskKey(task.getTaskKey())
                .taskId(task.getTaskId())
                .assignedUserId(task.getAssignedUserId())
                .assignedUserName(task.getAssignedUserName())
                .workGroup(task.getWorkGroup())
                .clientCode(task.getClientCode())
                .facilityCode(task.getFacilityCode())
                .build();

        publish(task.getTaskId(), event);
        log.info("Published TaskAssignedEvent for task: {} to user: {}", task.getTaskId(), task.getAssignedUserId());
    }

    public void publishTaskStarted(Task task) {
        TaskStartedEvent event = TaskStartedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventTime(LocalDateTime.now())
                .taskKey(task.getTaskKey())
                .taskId(task.getTaskId())
                .userId(task.getAssignedUserId())
                .startedAt(task.getStartedAt())
                .clientCode(task.getClientCode())
                .facilityCode(task.getFacilityCode())
                .build();

        publish(task.getTaskId(), event);
        log.info("Published TaskStartedEvent for task: {}", task.getTaskId());
    }

    public void publishTaskCompleted(Task task) {
        TaskCompletedEvent event = TaskCompletedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventTime(LocalDateTime.now())
                .taskKey(task.getTaskKey())
                .taskId(task.getTaskId())
                .userId(task.getAssignedUserId())
                .completedQuantity(task.getPickedQuantity())
                .shortQuantity(task.getShortQuantity())
                .completedAt(task.getCompletedAt())
                .actualMinutes(task.getActualMinutes())
                .clientCode(task.getClientCode())
                .facilityCode(task.getFacilityCode())
                .build();

        publish(task.getTaskId(), event);
        log.info("Published TaskCompletedEvent for task: {}", task.getTaskId());
    }

    public void publishTaskCancelled(Task task, String reason) {
        TaskCancelledEvent event = TaskCancelledEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventTime(LocalDateTime.now())
                .taskKey(task.getTaskKey())
                .taskId(task.getTaskId())
                .reason(reason)
                .cancelledBy(task.getModifiedBy())
                .clientCode(task.getClientCode())
                .facilityCode(task.getFacilityCode())
                .build();

        publish(task.getTaskId(), event);
        log.info("Published TaskCancelledEvent for task: {}", task.getTaskId());
    }

    public void publishTaskPriorityEscalated(Task task, String previousPriority) {
        TaskPriorityEscalatedEvent event = TaskPriorityEscalatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventTime(LocalDateTime.now())
                .taskKey(task.getTaskKey())
                .taskId(task.getTaskId())
                .previousPriority(previousPriority)
                .newPriority(task.getPriority().name())
                .clientCode(task.getClientCode())
                .facilityCode(task.getFacilityCode())
                .build();

        publish(task.getTaskId(), event);
        log.info("Published TaskPriorityEscalatedEvent for task: {} from {} to {}",
                task.getTaskId(), previousPriority, task.getPriority().name());
    }

    private void publish(String key, Object event) {
        kafkaTemplate.send(TASK_EVENTS_TOPIC, key, event);
    }
}
