package com.maersk.wms.task.domain.context_service.event;

import com.maersk.wms.task.shared.kernel.events.TaskDomainEvent;
import com.maersk.wms.task.shared.kernel.identifiers.*;

import java.time.Instant;
import java.util.Map;

/**
 * Domain events for Task Context bounded context.
 * Covers order, inventory, shipment, and equipment context handling.
 */
public final class ContextEvents {

    private ContextEvents() {}

    // ==================== Context Attachment Events ====================

    public record OrderContextAttached(
            TaskKey taskKey,
            OrderKey orderKey,
            String orderType,
            String customerCode,
            Map<String, Object> orderDetails,
            Instant attachedAt
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return taskKey.value();
        }

        @Override
        public String eventType() {
            return "ORDER_CONTEXT_ATTACHED";
        }

        @Override
        public Instant occurredAt() {
            return attachedAt;
        }
    }

    public record InventoryContextAttached(
            TaskKey taskKey,
            LpnKey lpnKey,
            SkuKey skuKey,
            LocationKey locationKey,
            int quantity,
            String lotNumber,
            Instant attachedAt
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return taskKey.value();
        }

        @Override
        public String eventType() {
            return "INVENTORY_CONTEXT_ATTACHED";
        }

        @Override
        public Instant occurredAt() {
            return attachedAt;
        }
    }

    public record ShipmentContextAttached(
            TaskKey taskKey,
            String shipmentKey,
            String carrierCode,
            String serviceLevel,
            Instant shipByDate,
            Map<String, Object> shipmentDetails,
            Instant attachedAt
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return taskKey.value();
        }

        @Override
        public String eventType() {
            return "SHIPMENT_CONTEXT_ATTACHED";
        }

        @Override
        public Instant occurredAt() {
            return attachedAt;
        }
    }

    public record EquipmentContextAttached(
            TaskKey taskKey,
            DeviceKey deviceKey,
            String equipmentType,
            String equipmentStatus,
            Instant attachedAt
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return taskKey.value();
        }

        @Override
        public String eventType() {
            return "EQUIPMENT_CONTEXT_ATTACHED";
        }

        @Override
        public Instant occurredAt() {
            return attachedAt;
        }
    }

    public record LocationContextAttached(
            TaskKey taskKey,
            LocationKey sourceLocation,
            LocationKey destinationLocation,
            ZoneKey zoneKey,
            String locationType,
            Instant attachedAt
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return taskKey.value();
        }

        @Override
        public String eventType() {
            return "LOCATION_CONTEXT_ATTACHED";
        }

        @Override
        public Instant occurredAt() {
            return attachedAt;
        }
    }

    // ==================== Context Update Events ====================

    public record TaskContextUpdated(
            TaskKey taskKey,
            String contextType,
            Map<String, Object> previousValues,
            Map<String, Object> newValues,
            Instant updatedAt,
            String updatedBy
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return taskKey.value();
        }

        @Override
        public String eventType() {
            return "TASK_CONTEXT_UPDATED";
        }

        @Override
        public Instant occurredAt() {
            return updatedAt;
        }
    }

    public record TaskContextDetached(
            TaskKey taskKey,
            String contextType,
            String detachReason,
            Instant detachedAt,
            String detachedBy
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return taskKey.value();
        }

        @Override
        public String eventType() {
            return "TASK_CONTEXT_DETACHED";
        }

        @Override
        public Instant occurredAt() {
            return detachedAt;
        }
    }

    // ==================== Context Validation Events ====================

    public record ContextValidationPassed(
            TaskKey taskKey,
            String validationType,
            Map<String, Object> validatedContext,
            Instant validatedAt
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return taskKey.value();
        }

        @Override
        public String eventType() {
            return "CONTEXT_VALIDATION_PASSED";
        }

        @Override
        public Instant occurredAt() {
            return validatedAt;
        }
    }

    public record ContextValidationFailed(
            TaskKey taskKey,
            String validationType,
            String failureReason,
            Map<String, Object> failedContext,
            Instant failedAt
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return taskKey.value();
        }

        @Override
        public String eventType() {
            return "CONTEXT_VALIDATION_FAILED";
        }

        @Override
        public Instant occurredAt() {
            return failedAt;
        }
    }

    // ==================== Context Enrichment Events ====================

    public record ContextEnriched(
            TaskKey taskKey,
            String enrichmentSource,
            Map<String, Object> enrichedData,
            Instant enrichedAt
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return taskKey.value();
        }

        @Override
        public String eventType() {
            return "CONTEXT_ENRICHED";
        }

        @Override
        public Instant occurredAt() {
            return enrichedAt;
        }
    }

    public record ContextSnapshotTaken(
            TaskKey taskKey,
            String snapshotId,
            Map<String, Object> contextSnapshot,
            Instant snapshotAt
    ) implements TaskDomainEvent {
        @Override
        public String aggregateId() {
            return taskKey.value();
        }

        @Override
        public String eventType() {
            return "CONTEXT_SNAPSHOT_TAKEN";
        }

        @Override
        public Instant occurredAt() {
            return snapshotAt;
        }
    }
}
