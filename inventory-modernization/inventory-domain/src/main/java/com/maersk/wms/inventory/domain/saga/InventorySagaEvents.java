package com.maersk.wms.inventory.domain.saga;

import com.maersk.wms.inventory.shared.kernel.events.InventoryDomainEvent;
import com.maersk.wms.inventory.shared.kernel.identifiers.*;
import com.maersk.wms.inventory.shared.kernel.valueobjects.*;

import java.time.Instant;
import java.util.List;

/**
 * Saga events for distributed transaction management and compensation.
 * Supports eventual consistency across inventory and related services.
 */
public final class InventorySagaEvents {

    private InventorySagaEvents() {}

    // ═══════════════════════════════════════════════════════════════
    // SAGA LIFECYCLE EVENTS
    // ═══════════════════════════════════════════════════════════════

    public record InventorySagaStarted(
            String sagaId,
            SagaType sagaType,
            String correlationId,
            List<String> participatingServices,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.saga_started";
        }
    }

    public record InventorySagaCompleted(
            String sagaId,
            SagaType sagaType,
            String correlationId,
            SagaOutcome outcome,
            List<String> completedSteps,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.saga_completed";
        }
    }

    public record InventorySagaFailed(
            String sagaId,
            SagaType sagaType,
            String correlationId,
            String failedStep,
            String failureReason,
            List<String> completedSteps,
            boolean compensationRequired,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.saga_failed";
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // COMPENSATION EVENTS
    // ═══════════════════════════════════════════════════════════════

    public record CompensationStarted(
            String sagaId,
            String compensationId,
            String failedStep,
            List<CompensationStep> stepsToCompensate,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.compensation_started";
        }
    }

    public record CompensationStepCompleted(
            String sagaId,
            String compensationId,
            String stepName,
            CompensationAction action,
            String details,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.compensation_step_completed";
        }
    }

    public record CompensationCompleted(
            String sagaId,
            String compensationId,
            int stepsCompensated,
            boolean fullyCompensated,
            List<String> failedCompensations,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.compensation_completed";
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // ALLOCATION SAGA EVENTS (specific saga type)
    // ═══════════════════════════════════════════════════════════════

    public record AllocationSagaInventoryReserved(
            String sagaId,
            AllocationKey allocationKey,
            OrderKey orderKey,
            List<InventoryReservation> reservations,
            Quantity totalReserved,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.allocation_saga.inventory_reserved";
        }
    }

    public record AllocationSagaCompensateReservation(
            String sagaId,
            AllocationKey allocationKey,
            OrderKey orderKey,
            List<InventoryReservation> reservationsToRelease,
            String compensationReason,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.allocation_saga.compensate_reservation";
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // TRANSFER SAGA EVENTS (specific saga type)
    // ═══════════════════════════════════════════════════════════════

    public record TransferSagaSourceDepleted(
            String sagaId,
            TransferKey transferKey,
            InventoryKey sourceInventoryKey,
            Quantity depletedQuantity,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.transfer_saga.source_depleted";
        }
    }

    public record TransferSagaTargetCreated(
            String sagaId,
            TransferKey transferKey,
            InventoryKey targetInventoryKey,
            Quantity createdQuantity,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.transfer_saga.target_created";
        }
    }

    public record TransferSagaRollback(
            String sagaId,
            TransferKey transferKey,
            RollbackAction action,
            String rollbackReason,
            WarehouseKey warehouseKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.transfer_saga.rollback";
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // SUPPORTING TYPES
    // ═══════════════════════════════════════════════════════════════

    public enum SagaType {
        ALLOCATION,             // Reserve inventory for order
        DEALLOCATION,           // Release inventory reservation
        TRANSFER,               // Move inventory between locations
        RECEIPT,                // Create inventory from receipt
        SHIPMENT,               // Deplete inventory for shipment
        ADJUSTMENT,             // Adjust inventory quantities
        COUNT_RECONCILIATION,   // Reconcile count variances
        HOLD_MANAGEMENT         // Apply/release holds
    }

    public enum SagaOutcome {
        SUCCESS,
        PARTIAL_SUCCESS,
        FAILED,
        COMPENSATED,
        COMPENSATION_FAILED
    }

    public enum CompensationAction {
        RESTORE_QUANTITY,       // Restore depleted quantity
        RELEASE_ALLOCATION,     // Release held allocation
        REVERSE_TRANSFER,       // Move inventory back
        DELETE_CREATED,         // Delete created inventory
        RESTORE_STATUS,         // Restore previous status
        RELEASE_HOLD            // Release applied hold
    }

    public enum RollbackAction {
        RESTORE_SOURCE,         // Restore source inventory
        DELETE_TARGET,          // Delete target inventory
        FULL_REVERSAL           // Complete reversal
    }

    public record CompensationStep(
            String stepName,
            CompensationAction action,
            String targetEntity,
            String targetKey
    ) {}

    // ═══════════════════════════════════════════════════════════════
    // SIMPLIFIED SAGA EVENTS (for saga orchestrators)
    // ═══════════════════════════════════════════════════════════════

    /**
     * Simple saga started event.
     */
    public record SagaStarted(
            String sagaId,
            String sagaType,
            String entityKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.saga.started";
        }
    }

    /**
     * Simple saga completed event.
     */
    public record SagaCompleted(
            String sagaId,
            String sagaType,
            boolean success,
            String errorMessage,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.saga.completed";
        }
    }

    /**
     * Allocation saga step event.
     */
    public record AllocationSagaStep(
            String sagaId,
            String stepName,
            AllocationKey allocationKey,
            Quantity quantity,
            SkuKey skuKey,
            OrderKey orderKey,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.allocation_saga.step";
        }
    }

    /**
     * Transfer saga step event.
     */
    public record TransferSagaStep(
            String sagaId,
            String stepName,
            InventoryKey inventoryKey,
            LocationKey fromLocation,
            LocationKey toLocation,
            Quantity quantity,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.transfer_saga.step";
        }
    }

    /**
     * Compensation step completion event (simplified).
     */
    public record CompensationStepEvent(
            String sagaId,
            String actionType,
            String entityKey,
            boolean success,
            String errorMessage,
            Instant occurredAt
    ) implements InventoryDomainEvent {
        @Override
        public String getEventType() {
            return "inventory.saga.compensation_step";
        }

        /**
         * Factory method with original name for compatibility.
         */
        public static CompensationStepEvent CompensationStep(
                String sagaId,
                String actionType,
                String entityKey,
                boolean success,
                String errorMessage,
                Instant occurredAt
        ) {
            return new CompensationStepEvent(sagaId, actionType, entityKey, success, errorMessage, occurredAt);
        }
    }

    public record InventoryReservation(
            InventoryKey inventoryKey,
            LocationKey locationKey,
            LpnKey lpnKey,
            Quantity reservedQuantity
    ) {}
}
