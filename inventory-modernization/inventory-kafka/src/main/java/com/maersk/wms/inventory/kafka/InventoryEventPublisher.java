package com.maersk.wms.inventory.kafka;

import com.maersk.wms.inventory.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Publishes inventory domain events to Kafka.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC_ADJUSTMENT = "wms.inventory.adjustment";
    private static final String TOPIC_TRANSFER = "wms.inventory.transfer";
    private static final String TOPIC_HOLD = "wms.inventory.hold";
    private static final String TOPIC_ALLOCATION = "wms.inventory.allocation";

    public void publishAdjustmentCompleted(InventoryAdjustment adjustment) {
        InventoryAdjustedEvent event = InventoryAdjustedEvent.builder()
                .adjustmentKey(adjustment.getAdjustmentKey())
                .sku(adjustment.getSku())
                .location(adjustment.getLocation())
                .adjustmentType(adjustment.getAdjustmentType().getCode())
                .systemQty(adjustment.getSystemQty())
                .adjustedQty(adjustment.getAdjustedQty())
                .variance(adjustment.getVariance())
                .reasonCode(adjustment.getReasonCode())
                .build();

        kafkaTemplate.send(TOPIC_ADJUSTMENT, adjustment.getAdjustmentKey(), event);
        log.info("Published adjustment event: {}", adjustment.getAdjustmentKey());
    }

    public void publishTransferCompleted(InventoryTransfer transfer) {
        InventoryTransferredEvent event = InventoryTransferredEvent.builder()
                .transferKey(transfer.getTransferKey())
                .sku(transfer.getSku())
                .fromLocation(transfer.getFromLocation())
                .toLocation(transfer.getToLocation())
                .transferQty(transfer.getTransferQty())
                .transferType(transfer.getTransferType().getCode())
                .build();

        kafkaTemplate.send(TOPIC_TRANSFER, transfer.getTransferKey(), event);
        log.info("Published transfer event: {}", transfer.getTransferKey());
    }

    public void publishHoldApplied(InventoryHold hold) {
        InventoryHoldEvent event = InventoryHoldEvent.builder()
                .holdKey(hold.getHoldKey())
                .holdCode(hold.getHoldCode())
                .scope(hold.getScope().name())
                .sku(hold.getSku())
                .lot(hold.getLot())
                .action("APPLIED")
                .build();

        kafkaTemplate.send(TOPIC_HOLD, hold.getHoldKey(), event);
        log.info("Published hold applied event: {}", hold.getHoldKey());
    }

    public void publishHoldReleased(InventoryHold hold) {
        InventoryHoldEvent event = InventoryHoldEvent.builder()
                .holdKey(hold.getHoldKey())
                .holdCode(hold.getHoldCode())
                .scope(hold.getScope().name())
                .action("RELEASED")
                .build();

        kafkaTemplate.send(TOPIC_HOLD, hold.getHoldKey(), event);
        log.info("Published hold released event: {}", hold.getHoldKey());
    }
}
