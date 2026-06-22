package com.maersk.wms.printing.domain.label_generation.event;

import com.maersk.wms.printing.shared.kernel.events.PrintingDomainEvent;
import com.maersk.wms.printing.shared.kernel.identifiers.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Domain events for Label Generation bounded context.
 */
public final class LabelEvents {

    private LabelEvents() {}

    // Label Lifecycle Events
    public record LabelCreated(
            LabelKey labelKey,
            TemplateKey templateKey,
            String labelType,
            String sourceType,
            String sourceKey,
            WarehouseKey warehouseKey,
            UserKey createdBy,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "label.created";
        }
    }

    public record LabelRendered(
            LabelKey labelKey,
            String format,
            int sizeBytes,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "label.rendered";
        }
    }

    public record LabelPrinted(
            LabelKey labelKey,
            PrinterKey printerKey,
            PrintJobKey jobKey,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "label.printed";
        }
    }

    public record LabelVoided(
            LabelKey labelKey,
            String reason,
            UserKey voidedBy,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "label.voided";
        }
    }

    public record LabelReprinted(
            LabelKey labelKey,
            int copies,
            UserKey requestedBy,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "label.reprinted";
        }
    }

    public record BatchLabelsCreated(
            List<LabelKey> labelKeys,
            TemplateKey templateKey,
            String labelType,
            int count,
            WarehouseKey warehouseKey,
            UserKey createdBy,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "label.batch_created";
        }
    }

    // Template Lifecycle Events
    public record TemplateCreated(
            TemplateKey templateKey,
            String templateCode,
            String templateName,
            String labelType,
            String format,
            WarehouseKey warehouseKey,
            UserKey createdBy,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "template.created";
        }
    }

    public record TemplateUpdated(
            TemplateKey templateKey,
            int newVersion,
            Map<String, Object> changes,
            UserKey updatedBy,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "template.updated";
        }
    }

    public record TemplateActivated(
            TemplateKey templateKey,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "template.activated";
        }
    }

    public record TemplateDeprecated(
            TemplateKey templateKey,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "template.deprecated";
        }
    }

    public record TemplateArchived(
            TemplateKey templateKey,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "template.archived";
        }
    }

    public record TemplateVersionIncremented(
            TemplateKey templateKey,
            int previousVersion,
            int newVersion,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "template.version_incremented";
        }
    }

    // Label Data Events
    public record LabelDataPopulated(
            LabelKey labelKey,
            Map<String, String> fields,
            List<String> barcodes,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "label.data_populated";
        }
    }

    public record LabelBarcodeGenerated(
            LabelKey labelKey,
            String barcodeType,
            String barcodeValue,
            Instant occurredAt
    ) implements PrintingDomainEvent {
        @Override
        public String getEventType() {
            return "label.barcode_generated";
        }
    }
}
