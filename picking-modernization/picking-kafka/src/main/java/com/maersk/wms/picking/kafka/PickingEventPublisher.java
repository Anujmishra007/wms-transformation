package com.maersk.wms.picking.kafka;

import com.maersk.wms.picking.domain.PickTask;
import com.maersk.wms.picking.domain.PickConfirmation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Publishes picking domain events to Kafka.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PickingEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC_TASK_ASSIGNED = "wms.picking.task-assigned";
    private static final String TOPIC_PICK_COMPLETED = "wms.picking.pick-completed";
    private static final String TOPIC_SHORT_PICK = "wms.picking.short-pick";

    /**
     * Publish task assigned event.
     */
    public void publishTaskAssigned(PickTask task) {
        TaskAssignedEvent event = TaskAssignedEvent.builder()
                .taskId(task.getTaskId())
                .userId(task.getAssignedUser())
                .warehouse(task.getWarehouseCode())
                .zone(task.getZone())
                .build();

        kafkaTemplate.send(TOPIC_TASK_ASSIGNED, task.getTaskId(), event);
        log.info("Published task assigned event: {}", task.getTaskId());
    }

    /**
     * Publish pick completed event.
     */
    public void publishPickCompleted(PickTask task, PickConfirmation confirmation) {
        PickCompletedEvent event = PickCompletedEvent.builder()
                .taskId(task.getTaskId())
                .orderId(task.getOrderId())
                .sku(task.getSku())
                .pickedQty(confirmation.getPickedQty())
                .fromLocation(task.getFromLocation())
                .fromLpn(confirmation.getFromLpn())
                .toLpn(confirmation.getToLpn())
                .userId(confirmation.getUserId())
                .build();

        kafkaTemplate.send(TOPIC_PICK_COMPLETED, task.getTaskId(), event);
        log.info("Published pick completed event: {}", task.getTaskId());
    }

    /**
     * Publish short pick event.
     */
    public void publishShortPick(PickTask task, PickConfirmation confirmation) {
        ShortPickEvent event = ShortPickEvent.builder()
                .taskId(task.getTaskId())
                .orderId(task.getOrderId())
                .sku(task.getSku())
                .requestedQty(task.getRequestedQty())
                .pickedQty(confirmation.getPickedQty())
                .shortQty(confirmation.getShortQty())
                .shortReason(confirmation.getShortReason())
                .location(task.getFromLocation())
                .build();

        kafkaTemplate.send(TOPIC_SHORT_PICK, task.getTaskId(), event);
        log.info("Published short pick event: {}", task.getTaskId());
    }
}
