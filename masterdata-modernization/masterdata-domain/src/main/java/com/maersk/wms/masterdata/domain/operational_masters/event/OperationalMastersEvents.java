package com.maersk.wms.masterdata.domain.operational_masters.event;

import com.maersk.wms.masterdata.shared.kernel.events.MasterDataDomainEvent;
import com.maersk.wms.masterdata.shared.kernel.identifiers.*;
import com.maersk.wms.masterdata.shared.kernel.valueobjects.*;
import com.maersk.wms.masterdata.domain.operational_masters.model.*;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * Domain events for Operational Masters bounded context.
 */
public final class OperationalMastersEvents {

    private OperationalMastersEvents() {}

    // ==================== Storer Events ====================

    public record StorerCreated(
            StorerKey storerKey,
            String storerCode,
            String storerName,
            Storer.StorerType storerType,
            Instant createdAt,
            String createdBy
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return storerKey.value(); }
        @Override public String eventType() { return "STORER_CREATED"; }
        @Override public Instant occurredAt() { return createdAt; }
    }

    public record StorerActivated(
            StorerKey storerKey,
            Instant activatedAt
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return storerKey.value(); }
        @Override public String eventType() { return "STORER_ACTIVATED"; }
        @Override public Instant occurredAt() { return activatedAt; }
    }

    public record StorerDeactivated(
            StorerKey storerKey,
            String reason,
            Instant deactivatedAt
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return storerKey.value(); }
        @Override public String eventType() { return "STORER_DEACTIVATED"; }
        @Override public Instant occurredAt() { return deactivatedAt; }
    }

    public record StorerSuspended(
            StorerKey storerKey,
            String reason,
            Instant suspendedAt
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return storerKey.value(); }
        @Override public String eventType() { return "STORER_SUSPENDED"; }
        @Override public Instant occurredAt() { return suspendedAt; }
    }

    // ==================== Dock Events ====================

    public record DockCreated(
            DockKey dockKey,
            WarehouseKey warehouseKey,
            String dockCode,
            Dock.DockType dockType,
            Instant createdAt
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return dockKey.value(); }
        @Override public String eventType() { return "DOCK_CREATED"; }
        @Override public Instant occurredAt() { return createdAt; }
    }

    public record DockReserved(
            DockKey dockKey,
            String appointmentId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Instant reservedAt
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return dockKey.value(); }
        @Override public String eventType() { return "DOCK_RESERVED"; }
        @Override public Instant occurredAt() { return reservedAt; }
    }

    public record DockOccupied(
            DockKey dockKey,
            String trailerId,
            String appointmentId,
            Instant occupiedAt
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return dockKey.value(); }
        @Override public String eventType() { return "DOCK_OCCUPIED"; }
        @Override public Instant occurredAt() { return occupiedAt; }
    }

    public record DockReleased(
            DockKey dockKey,
            Instant releasedAt
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return dockKey.value(); }
        @Override public String eventType() { return "DOCK_RELEASED"; }
        @Override public Instant occurredAt() { return releasedAt; }
    }

    public record DockBlocked(
            DockKey dockKey,
            String reason,
            Instant blockedAt
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return dockKey.value(); }
        @Override public String eventType() { return "DOCK_BLOCKED"; }
        @Override public Instant occurredAt() { return blockedAt; }
    }

    // ==================== Lane Events ====================

    public record LaneCreated(
            LaneKey laneKey,
            WarehouseKey warehouseKey,
            ZoneKey zoneKey,
            String laneCode,
            Lane.LaneType laneType,
            Instant createdAt
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return laneKey.value(); }
        @Override public String eventType() { return "LANE_CREATED"; }
        @Override public Instant occurredAt() { return createdAt; }
    }

    public record LaneAssignedToDock(
            LaneKey laneKey,
            DockKey dockKey,
            Instant assignedAt
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return laneKey.value(); }
        @Override public String eventType() { return "LANE_ASSIGNED_TO_DOCK"; }
        @Override public Instant occurredAt() { return assignedAt; }
    }

    public record LaneUnassignedFromDock(
            LaneKey laneKey,
            DockKey dockKey,
            Instant unassignedAt
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return laneKey.value(); }
        @Override public String eventType() { return "LANE_UNASSIGNED_FROM_DOCK"; }
        @Override public Instant occurredAt() { return unassignedAt; }
    }

    // ==================== Equipment Events ====================

    public record EquipmentCreated(
            EquipmentKey equipmentKey,
            WarehouseKey warehouseKey,
            String equipmentCode,
            Equipment.EquipmentType equipmentType,
            Instant createdAt
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return equipmentKey.value(); }
        @Override public String eventType() { return "EQUIPMENT_CREATED"; }
        @Override public Instant occurredAt() { return createdAt; }
    }

    public record EquipmentAssignedToUser(
            EquipmentKey equipmentKey,
            UserKey userKey,
            Instant assignedAt
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return equipmentKey.value(); }
        @Override public String eventType() { return "EQUIPMENT_ASSIGNED_TO_USER"; }
        @Override public Instant occurredAt() { return assignedAt; }
    }

    public record EquipmentReturned(
            EquipmentKey equipmentKey,
            UserKey userKey,
            Instant returnedAt
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return equipmentKey.value(); }
        @Override public String eventType() { return "EQUIPMENT_RETURNED"; }
        @Override public Instant occurredAt() { return returnedAt; }
    }

    public record EquipmentSentToMaintenance(
            EquipmentKey equipmentKey,
            String reason,
            Instant sentAt
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return equipmentKey.value(); }
        @Override public String eventType() { return "EQUIPMENT_SENT_TO_MAINTENANCE"; }
        @Override public Instant occurredAt() { return sentAt; }
    }

    public record EquipmentMaintenanceCompleted(
            EquipmentKey equipmentKey,
            Instant completedAt
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return equipmentKey.value(); }
        @Override public String eventType() { return "EQUIPMENT_MAINTENANCE_COMPLETED"; }
        @Override public Instant occurredAt() { return completedAt; }
    }

    public record EquipmentRetired(
            EquipmentKey equipmentKey,
            String reason,
            Instant retiredAt
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return equipmentKey.value(); }
        @Override public String eventType() { return "EQUIPMENT_RETIRED"; }
        @Override public Instant occurredAt() { return retiredAt; }
    }

    // ==================== User Events ====================

    public record UserCreated(
            UserKey userKey,
            WarehouseKey warehouseKey,
            String userId,
            String userName,
            Instant createdAt,
            String createdBy
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return userKey.value(); }
        @Override public String eventType() { return "USER_CREATED"; }
        @Override public Instant occurredAt() { return createdAt; }
    }

    public record UserActivated(
            UserKey userKey,
            Instant activatedAt
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return userKey.value(); }
        @Override public String eventType() { return "USER_ACTIVATED"; }
        @Override public Instant occurredAt() { return activatedAt; }
    }

    public record UserDeactivated(
            UserKey userKey,
            String reason,
            Instant deactivatedAt
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return userKey.value(); }
        @Override public String eventType() { return "USER_DEACTIVATED"; }
        @Override public Instant occurredAt() { return deactivatedAt; }
    }

    public record UserLoggedIn(
            UserKey userKey,
            Instant loggedInAt
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return userKey.value(); }
        @Override public String eventType() { return "USER_LOGGED_IN"; }
        @Override public Instant occurredAt() { return loggedInAt; }
    }

    public record UserLoggedOff(
            UserKey userKey,
            Instant loggedOffAt
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return userKey.value(); }
        @Override public String eventType() { return "USER_LOGGED_OFF"; }
        @Override public Instant occurredAt() { return loggedOffAt; }
    }

    public record UserAssignedToZone(
            UserKey userKey,
            ZoneKey zoneKey,
            Instant assignedAt
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return userKey.value(); }
        @Override public String eventType() { return "USER_ASSIGNED_TO_ZONE"; }
        @Override public Instant occurredAt() { return assignedAt; }
    }

    public record UserRemovedFromZone(
            UserKey userKey,
            ZoneKey zoneKey,
            Instant removedAt
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return userKey.value(); }
        @Override public String eventType() { return "USER_REMOVED_FROM_ZONE"; }
        @Override public Instant occurredAt() { return removedAt; }
    }

    public record UserSkillLevelUpdated(
            UserKey userKey,
            String skill,
            int oldLevel,
            int newLevel,
            Instant updatedAt
    ) implements MasterDataDomainEvent {
        @Override public String aggregateId() { return userKey.value(); }
        @Override public String eventType() { return "USER_SKILL_LEVEL_UPDATED"; }
        @Override public Instant occurredAt() { return updatedAt; }
    }
}
