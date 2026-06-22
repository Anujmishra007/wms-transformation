package com.maersk.wms.outbound.workflow;

import com.maersk.wms.outbound.activity.AllocationActivities;
import com.maersk.wms.outbound.activity.PackingActivities;
import com.maersk.wms.outbound.activity.PickingActivities;
import com.maersk.wms.outbound.activity.ShippingActivities;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Saga;
import io.temporal.workflow.Workflow;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of order fulfillment workflow.
 * Uses Saga pattern for compensation on failures.
 */
public class OrderFulfillmentWorkflowImpl implements OrderFulfillmentWorkflow {

    private final AllocationActivities allocationActivities;
    private final PickingActivities pickingActivities;
    private final PackingActivities packingActivities;
    private final ShippingActivities shippingActivities;

    public OrderFulfillmentWorkflowImpl() {
        ActivityOptions options = ActivityOptions.newBuilder()
                .setStartToCloseTimeout(Duration.ofMinutes(5))
                .setRetryOptions(RetryOptions.newBuilder()
                        .setMaximumAttempts(3)
                        .setInitialInterval(Duration.ofSeconds(1))
                        .setBackoffCoefficient(2.0)
                        .build())
                .build();

        this.allocationActivities = Workflow.newActivityStub(AllocationActivities.class, options);
        this.pickingActivities = Workflow.newActivityStub(PickingActivities.class, options);
        this.packingActivities = Workflow.newActivityStub(PackingActivities.class, options);
        this.shippingActivities = Workflow.newActivityStub(ShippingActivities.class, options);
    }

    @Override
    public OrderFulfillmentResult fulfill(OrderFulfillmentRequest request) {
        LocalDateTime startTime = LocalDateTime.now();
        List<String> errors = new ArrayList<>();
        Saga.Options sagaOptions = new Saga.Options.Builder().setParallelCompensation(false).build();
        Saga saga = new Saga(sagaOptions);

        try {
            // Step 1: Allocate inventory
            AllocationActivities.AllocationResult allocationResult =
                    allocationActivities.allocateOrder(
                            request.getOrderNumber(),
                            request.getClientCode(),
                            request.getFacilityCode()
                    );

            saga.addCompensation(() -> allocationActivities.deallocateOrder(
                    request.getOrderNumber(),
                    request.getClientCode(),
                    request.getFacilityCode()
            ));

            if (!allocationResult.isSuccess()) {
                return OrderFulfillmentResult.builder()
                        .orderNumber(request.getOrderNumber())
                        .status(allocationResult.isPartiallyAllocated() ?
                                OrderFulfillmentStatus.ALLOCATION_SHORT :
                                OrderFulfillmentStatus.FAILED)
                        .fullyAllocated(false)
                        .allocatedLines(allocationResult.getAllocatedLines())
                        .shortLines(allocationResult.getShortLines())
                        .allocationIds(allocationResult.getAllocationIds())
                        .errors(allocationResult.getErrors())
                        .startTime(startTime)
                        .endTime(LocalDateTime.now())
                        .build();
            }

            // Step 2: Create and release pick tasks
            PickingActivities.PickResult pickResult =
                    pickingActivities.createPickTasks(
                            request.getOrderNumber(),
                            allocationResult.getAllocationIds(),
                            request.getClientCode(),
                            request.getFacilityCode()
                    );

            saga.addCompensation(() -> pickingActivities.cancelPickTasks(
                    pickResult.getPickTaskIds()
            ));

            if (!pickResult.isSuccess()) {
                saga.compensate();
                return OrderFulfillmentResult.builder()
                        .orderNumber(request.getOrderNumber())
                        .status(OrderFulfillmentStatus.FAILED)
                        .message("Pick task creation failed")
                        .errors(pickResult.getErrors())
                        .startTime(startTime)
                        .endTime(LocalDateTime.now())
                        .build();
            }

            // If not auto-processing, return after pick task creation
            if (!request.isAutoPack()) {
                return OrderFulfillmentResult.builder()
                        .orderNumber(request.getOrderNumber())
                        .status(OrderFulfillmentStatus.PICKING)
                        .fullyAllocated(true)
                        .allocatedLines(allocationResult.getAllocatedLines())
                        .allocationIds(allocationResult.getAllocationIds())
                        .pickTaskIds(pickResult.getPickTaskIds())
                        .startTime(startTime)
                        .endTime(LocalDateTime.now())
                        .build();
            }

            // Step 3: Pack (cartonization)
            PackingActivities.PackResult packResult =
                    packingActivities.packOrder(
                            request.getOrderNumber(),
                            request.getClientCode(),
                            request.getFacilityCode()
                    );

            saga.addCompensation(() -> packingActivities.unpackOrder(
                    packResult.getCartonIds()
            ));

            if (!packResult.isSuccess()) {
                saga.compensate();
                return OrderFulfillmentResult.builder()
                        .orderNumber(request.getOrderNumber())
                        .status(OrderFulfillmentStatus.FAILED)
                        .message("Packing failed")
                        .errors(packResult.getErrors())
                        .startTime(startTime)
                        .endTime(LocalDateTime.now())
                        .build();
            }

            // If not auto-shipping, return after packing
            if (!request.isAutoShip()) {
                return OrderFulfillmentResult.builder()
                        .orderNumber(request.getOrderNumber())
                        .status(OrderFulfillmentStatus.PACKED)
                        .fullyAllocated(true)
                        .allocatedLines(allocationResult.getAllocatedLines())
                        .allocationIds(allocationResult.getAllocationIds())
                        .pickTaskIds(pickResult.getPickTaskIds())
                        .cartonCount(packResult.getCartonCount())
                        .cartonIds(packResult.getCartonIds())
                        .startTime(startTime)
                        .endTime(LocalDateTime.now())
                        .build();
            }

            // Step 4: Ship
            ShippingActivities.ShipResult shipResult =
                    shippingActivities.shipOrder(
                            request.getOrderNumber(),
                            packResult.getCartonIds(),
                            request.getClientCode(),
                            request.getFacilityCode()
                    );

            if (!shipResult.isSuccess()) {
                saga.compensate();
                return OrderFulfillmentResult.builder()
                        .orderNumber(request.getOrderNumber())
                        .status(OrderFulfillmentStatus.FAILED)
                        .message("Shipping failed")
                        .errors(shipResult.getErrors())
                        .startTime(startTime)
                        .endTime(LocalDateTime.now())
                        .build();
            }

            // Success - fully fulfilled
            LocalDateTime endTime = LocalDateTime.now();
            return OrderFulfillmentResult.builder()
                    .orderNumber(request.getOrderNumber())
                    .status(OrderFulfillmentStatus.SHIPPED)
                    .fullyAllocated(true)
                    .allocatedLines(allocationResult.getAllocatedLines())
                    .allocationIds(allocationResult.getAllocationIds())
                    .pickedLines(pickResult.getPickedLines())
                    .pickTaskIds(pickResult.getPickTaskIds())
                    .cartonCount(packResult.getCartonCount())
                    .cartonIds(packResult.getCartonIds())
                    .shipmentId(shipResult.getShipmentId())
                    .trackingNumber(shipResult.getTrackingNumber())
                    .carrier(shipResult.getCarrier())
                    .startTime(startTime)
                    .endTime(endTime)
                    .durationMs(Duration.between(startTime, endTime).toMillis())
                    .build();

        } catch (Exception e) {
            saga.compensate();
            errors.add(e.getMessage());
            return OrderFulfillmentResult.builder()
                    .orderNumber(request.getOrderNumber())
                    .status(OrderFulfillmentStatus.FAILED)
                    .message("Workflow failed: " + e.getMessage())
                    .errors(errors)
                    .startTime(startTime)
                    .endTime(LocalDateTime.now())
                    .build();
        }
    }
}
