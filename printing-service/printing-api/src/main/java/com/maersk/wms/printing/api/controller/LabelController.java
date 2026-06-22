package com.maersk.wms.printing.api.controller;

import com.maersk.wms.printing.domain.label_generation.model.Label;
import com.maersk.wms.printing.domain.label_generation.service.LabelGenerationService;
import com.maersk.wms.printing.shared.kernel.identifiers.*;
import com.maersk.wms.printing.shared.kernel.valueobjects.LabelData;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for label operations.
 */
@RestController
@RequestMapping("/api/v1/labels")
public class LabelController {

    private final LabelGenerationService labelService;

    public LabelController(LabelGenerationService labelService) {
        this.labelService = labelService;
    }

    @PostMapping
    public ResponseEntity<LabelResponse> createLabel(@RequestBody CreateLabelRequest request) {
        LabelData labelData = new LabelData(
                request.fields(),
                request.barcodes(),
                Map.of()
        );

        Label label = labelService.createLabel(
                new TemplateKey(request.templateKey()),
                labelData,
                new WarehouseKey(request.warehouseKey()),
                new UserKey(request.createdBy())
        );

        return ResponseEntity.ok(mapToResponse(label));
    }

    @PostMapping("/shipping")
    public ResponseEntity<LabelResponse> createShippingLabel(@RequestBody CreateShippingLabelRequest request) {
        LabelData labelData = new LabelData(
                request.fields(),
                request.barcodes(),
                Map.of()
        );

        Label label = labelService.createShippingLabel(
                request.sourceKey(),
                labelData,
                new WarehouseKey(request.warehouseKey()),
                new UserKey(request.createdBy())
        );

        return ResponseEntity.ok(mapToResponse(label));
    }

    @PostMapping("/lpn")
    public ResponseEntity<LabelResponse> createLpnLabel(@RequestBody CreateLpnLabelRequest request) {
        LabelData labelData = new LabelData(
                request.fields(),
                request.barcodes(),
                Map.of()
        );

        Label label = labelService.createLpnLabel(
                request.lpnNumber(),
                labelData,
                new WarehouseKey(request.warehouseKey()),
                new UserKey(request.createdBy())
        );

        return ResponseEntity.ok(mapToResponse(label));
    }

    @GetMapping("/{labelKey}")
    public ResponseEntity<LabelResponse> getLabel(@PathVariable String labelKey) {
        return labelService.findByKey(new LabelKey(labelKey))
                .map(this::mapToResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{labelKey}/render")
    public ResponseEntity<byte[]> renderLabel(
            @PathVariable String labelKey,
            @RequestParam(defaultValue = "ZPL") String format) {

        byte[] rendered = labelService.renderLabel(new LabelKey(labelKey), format);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(getMediaType(format));
        headers.setContentDispositionFormData("attachment", "label." + format.toLowerCase());

        return ResponseEntity.ok()
                .headers(headers)
                .body(rendered);
    }

    @PostMapping("/{labelKey}/void")
    public ResponseEntity<Void> voidLabel(
            @PathVariable String labelKey,
            @RequestBody VoidLabelRequest request) {

        labelService.voidLabel(
                new LabelKey(labelKey),
                request.reason(),
                new UserKey(request.voidedBy())
        );

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{labelKey}/reprint")
    public ResponseEntity<Void> reprintLabel(
            @PathVariable String labelKey,
            @RequestParam(defaultValue = "1") int copies) {

        labelService.reprintLabel(new LabelKey(labelKey), copies);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/warehouse/{warehouseKey}")
    public ResponseEntity<List<LabelResponse>> getLabelsByWarehouse(@PathVariable String warehouseKey) {
        List<Label> labels = labelService.findByWarehouse(new WarehouseKey(warehouseKey));
        return ResponseEntity.ok(labels.stream().map(this::mapToResponse).toList());
    }

    @GetMapping("/pending/{warehouseKey}")
    public ResponseEntity<List<LabelResponse>> getPendingLabels(@PathVariable String warehouseKey) {
        List<Label> labels = labelService.findPendingLabels(new WarehouseKey(warehouseKey));
        return ResponseEntity.ok(labels.stream().map(this::mapToResponse).toList());
    }

    // DTOs
    public record CreateLabelRequest(
            String templateKey,
            Map<String, String> fields,
            Map<String, String> barcodes,
            String warehouseKey,
            String createdBy
    ) {}

    public record CreateShippingLabelRequest(
            String sourceKey,
            Map<String, String> fields,
            Map<String, String> barcodes,
            String warehouseKey,
            String createdBy
    ) {}

    public record CreateLpnLabelRequest(
            String lpnNumber,
            Map<String, String> fields,
            Map<String, String> barcodes,
            String warehouseKey,
            String createdBy
    ) {}

    public record VoidLabelRequest(
            String reason,
            String voidedBy
    ) {}

    public record LabelResponse(
            String labelKey,
            String templateKey,
            String labelType,
            String sourceType,
            String sourceKey,
            String barcodeValue,
            String status,
            String warehouseKey,
            String createdAt,
            String createdBy
    ) {}

    // Helper methods
    private LabelResponse mapToResponse(Label label) {
        return new LabelResponse(
                label.getLabelKey().value(),
                label.getTemplateKey().value(),
                label.getLabelType().name(),
                label.getSourceType(),
                label.getSourceKey(),
                label.getBarcodeValue(),
                label.getStatus().name(),
                label.getWarehouseKey().value(),
                label.getCreatedAt().toString(),
                label.getCreatedBy()
        );
    }

    private MediaType getMediaType(String format) {
        return switch (format.toUpperCase()) {
            case "PDF" -> MediaType.APPLICATION_PDF;
            case "PNG" -> MediaType.IMAGE_PNG;
            default -> MediaType.TEXT_PLAIN;
        };
    }
}
