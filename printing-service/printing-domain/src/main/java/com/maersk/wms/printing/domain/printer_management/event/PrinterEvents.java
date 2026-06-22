package com.maersk.wms.printing.domain.printer_management.event;

import com.maersk.wms.printing.shared.kernel.events.PrintingDomainEvent;
import com.maersk.wms.printing.shared.kernel.identifiers.*;

import java.time.Instant;
import java.util.List;

/**
 * Domain events for Printer Management bounded context.
 */
public final class PrinterEvents {

    private PrinterEvents() {}

    // Printer Lifecycle Events
    public record PrinterRegistered(
            PrinterKey printerKey,
            String printerCode,
            String printerName,
            String printerType,
            ZoneKey zoneKey,
            WarehouseKey warehouseKey,
            UserKey registeredBy,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "printer.registered";
        }
    }

    public record PrinterUpdated(
            PrinterKey printerKey,
            List<String> updatedFields,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "printer.updated";
        }
    }

    public record PrinterDecommissioned(
            PrinterKey printerKey,
            String reason,
            UserKey decommissionedBy,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "printer.decommissioned";
        }
    }

    // Printer Status Events
    public record PrinterWentOnline(
            PrinterKey printerKey,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "printer.went_online";
        }
    }

    public record PrinterWentOffline(
            PrinterKey printerKey,
            String reason,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "printer.went_offline";
        }
    }

    public record PrinterBecameBusy(
            PrinterKey printerKey,
            PrintJobKey currentJobKey,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "printer.became_busy";
        }
    }

    public record PrinterBecameIdle(
            PrinterKey printerKey,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "printer.became_idle";
        }
    }

    public record PrinterErrorOccurred(
            PrinterKey printerKey,
            String error,
            String previousStatus,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "printer.error_occurred";
        }
    }

    public record PrinterErrorCleared(
            PrinterKey printerKey,
            String previousError,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "printer.error_cleared";
        }
    }

    public record PrinterPaused(
            PrinterKey printerKey,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "printer.paused";
        }
    }

    public record PrinterResumed(
            PrinterKey printerKey,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "printer.resumed";
        }
    }

    public record PrinterDisabled(
            PrinterKey printerKey,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "printer.disabled";
        }
    }

    public record PrinterEnabled(
            PrinterKey printerKey,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "printer.enabled";
        }
    }

    // Printer Media Events
    public record PrinterOutOfMedia(
            PrinterKey printerKey,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "printer.out_of_media";
        }
    }

    public record PrinterOutOfRibbon(
            PrinterKey printerKey,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "printer.out_of_ribbon";
        }
    }

    public record PrinterMediaRefilled(
            PrinterKey printerKey,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "printer.media_refilled";
        }
    }

    // Printer Assignment Events
    public record PrinterAssignedToZone(
            PrinterKey printerKey,
            ZoneKey previousZoneKey,
            ZoneKey newZoneKey,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "printer.assigned_to_zone";
        }
    }

    public record PrinterAssignedToUser(
            PrinterKey printerKey,
            UserKey userKey,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "printer.assigned_to_user";
        }
    }

    public record PrinterUnassignedFromUser(
            PrinterKey printerKey,
            UserKey previousUserKey,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "printer.unassigned_from_user";
        }
    }

    public record PrinterLabelTypesUpdated(
            PrinterKey printerKey,
            List<String> previousLabelTypes,
            List<String> newLabelTypes,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "printer.label_types_updated";
        }
    }

    // Printer Maintenance Events
    public record PrinterMaintenanceScheduled(
            PrinterKey printerKey,
            Instant scheduledDate,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "printer.maintenance_scheduled";
        }
    }

    public record PrinterMaintenanceCompleted(
            PrinterKey printerKey,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "printer.maintenance_completed";
        }
    }

    public record PrinterMaintenanceRequired(
            PrinterKey printerKey,
            String reason,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "printer.maintenance_required";
        }
    }

    public record PrinterPrintHeadLifeUpdated(
            PrinterKey printerKey,
            int previousPercent,
            int newPercent,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "printer.print_head_life_updated";
        }
    }

    // Printer Metrics Events
    public record PrinterJobCompleted(
            PrinterKey printerKey,
            PrintJobKey jobKey,
            int labelsCount,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "printer.job_completed";
        }
    }

    public record PrinterDailyCountersReset(
            PrinterKey printerKey,
            int previousJobsToday,
            int previousLabelsToday,
            int previousErrorsToday,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "printer.daily_counters_reset";
        }
    }
}
