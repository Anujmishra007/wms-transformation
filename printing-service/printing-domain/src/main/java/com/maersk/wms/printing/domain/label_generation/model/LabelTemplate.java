package com.maersk.wms.printing.domain.label_generation.model;

import com.maersk.wms.printing.shared.kernel.identifiers.*;
import com.maersk.wms.printing.shared.kernel.valueobjects.*;

import lombok.*;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Label Template entity.
 * Defines the format and layout for generating labels.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabelTemplate {

    private TemplateKey templateKey;
    private StorerKey storerKey; // null = system-wide template
    private WarehouseKey warehouseKey; // null = all warehouses

    // Template Identity
    private String templateName;
    private String templateCode;
    private String description;

    // Label Type
    private Label.LabelType labelType;
    private String labelSubType;

    // Format
    private String format; // ZPL, EPL, PDF, HTML
    private String templateContent; // The actual template code/markup
    private int version;

    // Dimensions
    private LabelDimensions dimensions;

    // Field Definitions
    @Builder.Default
    private List<TemplateField> fields = List.of();
    @Builder.Default
    private List<TemplateBarcodeField> barcodeFields = List.of();

    // Printer Compatibility
    @Builder.Default
    private List<String> compatiblePrinterTypes = List.of();

    // Defaults
    @Builder.Default
    private Map<String, String> defaultValues = Map.of();

    // Status
    private TemplateStatus status;

    // Audit
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;

    public enum TemplateStatus {
        DRAFT, ACTIVE, DEPRECATED, ARCHIVED
    }

    // Business Methods
    public void activate() {
        this.status = TemplateStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void deprecate() {
        this.status = TemplateStatus.DEPRECATED;
        this.updatedAt = Instant.now();
    }

    public void archive() {
        this.status = TemplateStatus.ARCHIVED;
        this.updatedAt = Instant.now();
    }

    public void incrementVersion() {
        this.version++;
        this.updatedAt = Instant.now();
    }

    public boolean isActive() {
        return status == TemplateStatus.ACTIVE;
    }

    public boolean isCompatibleWith(String printerType) {
        return compatiblePrinterTypes.isEmpty() || compatiblePrinterTypes.contains(printerType);
    }

    public TemplateField getField(String fieldName) {
        return fields.stream()
                .filter(f -> f.fieldName().equals(fieldName))
                .findFirst()
                .orElse(null);
    }

    public String getDefaultValue(String fieldName) {
        return defaultValues.get(fieldName);
    }

    // Nested Records
    public record TemplateField(
            String fieldName,
            String label,
            String dataType,
            boolean required,
            String validationRule,
            int maxLength,
            int xPosition,
            int yPosition,
            String fontName,
            int fontSize
    ) {}

    public record TemplateBarcodeField(
            String fieldName,
            String barcodeType,
            boolean required,
            int xPosition,
            int yPosition,
            int width,
            int height,
            boolean includeText
    ) {}
}
