package com.maersk.wms.printing.service.label;

import com.maersk.wms.printing.domain.label_generation.model.Label;
import com.maersk.wms.printing.domain.label_generation.model.LabelTemplate;
import com.maersk.wms.printing.domain.label_generation.repository.LabelRepository;
import com.maersk.wms.printing.domain.label_generation.repository.LabelTemplateRepository;
import com.maersk.wms.printing.domain.label_generation.service.LabelGenerationService;
import com.maersk.wms.printing.domain.label_generation.event.LabelEvents.*;
import com.maersk.wms.printing.shared.kernel.identifiers.*;
import com.maersk.wms.printing.shared.kernel.valueobjects.LabelData;
import com.maersk.wms.printing.shared.kernel.exceptions.*;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of LabelGenerationService.
 * Handles label creation, rendering, and template management.
 */
@Service
@Transactional
public class LabelGenerationServiceImpl implements LabelGenerationService {

    private final LabelRepository labelRepository;
    private final LabelTemplateRepository templateRepository;
    private final ApplicationEventPublisher eventPublisher;
    // TODO: Inject label renderer components

    public LabelGenerationServiceImpl(
            LabelRepository labelRepository,
            LabelTemplateRepository templateRepository,
            ApplicationEventPublisher eventPublisher) {
        this.labelRepository = labelRepository;
        this.templateRepository = templateRepository;
        this.eventPublisher = eventPublisher;
    }

    // Label Creation
    @Override
    public Label createLabel(TemplateKey templateKey, LabelData data, WarehouseKey warehouseKey, UserKey createdBy) {
        LabelTemplate template = templateRepository.findByKey(templateKey)
                .orElseThrow(() -> new TemplateNotFoundException("Template not found: " + templateKey.value()));

        if (!template.isActive()) {
            throw new PrintingException("Template is not active: " + templateKey.value());
        }

        Label label = Label.builder()
                .labelKey(new LabelKey(UUID.randomUUID().toString()))
                .templateKey(templateKey)
                .warehouseKey(warehouseKey)
                .labelType(template.getLabelType())
                .labelData(data)
                .barcodeValue(generateBarcodeValue(data))
                .status(Label.LabelStatus.CREATED)
                .createdAt(Instant.now())
                .createdBy(createdBy.value())
                .build();

        Label saved = labelRepository.save(label);

        eventPublisher.publishEvent(new LabelCreated(
                saved.getLabelKey(),
                templateKey,
                saved.getLabelType().name(),
                saved.getSourceType(),
                saved.getSourceKey(),
                warehouseKey,
                createdBy,
                Instant.now()
        ));

        return saved;
    }

    @Override
    public Label createShippingLabel(String sourceKey, LabelData data, WarehouseKey warehouseKey, UserKey createdBy) {
        TemplateKey templateKey = findDefaultTemplateForType(Label.LabelType.SHIPPING, warehouseKey);
        Label label = createLabel(templateKey, data, warehouseKey, createdBy);
        label.setSourceType("SHIPPING");
        label.setSourceKey(sourceKey);
        return labelRepository.save(label);
    }

    @Override
    public Label createLpnLabel(String lpnNumber, LabelData data, WarehouseKey warehouseKey, UserKey createdBy) {
        TemplateKey templateKey = findDefaultTemplateForType(Label.LabelType.LPN, warehouseKey);
        Label label = createLabel(templateKey, data, warehouseKey, createdBy);
        label.setSourceType("LPN");
        label.setSourceKey(lpnNumber);
        return labelRepository.save(label);
    }

    @Override
    public Label createLocationLabel(String locationCode, LabelData data, WarehouseKey warehouseKey, UserKey createdBy) {
        TemplateKey templateKey = findDefaultTemplateForType(Label.LabelType.LOCATION, warehouseKey);
        Label label = createLabel(templateKey, data, warehouseKey, createdBy);
        label.setSourceType("LOCATION");
        label.setSourceKey(locationCode);
        return labelRepository.save(label);
    }

    @Override
    public List<Label> createBatchLabels(TemplateKey templateKey, List<LabelData> dataList, WarehouseKey warehouseKey, UserKey createdBy) {
        List<Label> labels = dataList.stream()
                .map(data -> createLabel(templateKey, data, warehouseKey, createdBy))
                .collect(Collectors.toList());

        eventPublisher.publishEvent(new BatchLabelsCreated(
                labels.stream().map(Label::getLabelKey).collect(Collectors.toList()),
                templateKey,
                labels.isEmpty() ? "" : labels.get(0).getLabelType().name(),
                labels.size(),
                warehouseKey,
                createdBy,
                Instant.now()
        ));

        return labels;
    }

    // Label Rendering
    @Override
    public byte[] renderLabel(LabelKey labelKey, String format) {
        Label label = findByKeyOrThrow(labelKey);
        LabelTemplate template = templateRepository.findByKey(label.getTemplateKey())
                .orElseThrow(() -> new TemplateNotFoundException("Template not found"));

        // TODO: Implement actual label rendering using ZPL/EPL/PDF renderer
        byte[] rendered = renderLabelContent(label, template, format);

        label.markAsRendered();
        labelRepository.save(label);

        eventPublisher.publishEvent(new LabelRendered(
                labelKey,
                format,
                rendered.length,
                Instant.now()
        ));

        return rendered;
    }

    @Override
    public byte[] renderLabelPreview(TemplateKey templateKey, LabelData data) {
        LabelTemplate template = templateRepository.findByKey(templateKey)
                .orElseThrow(() -> new TemplateNotFoundException("Template not found"));

        // TODO: Implement preview rendering
        return new byte[0];
    }

    @Override
    public List<byte[]> renderBatchLabels(List<LabelKey> labelKeys, String format) {
        return labelKeys.stream()
                .map(key -> renderLabel(key, format))
                .collect(Collectors.toList());
    }

    // Label Retrieval
    @Override
    @Transactional(readOnly = true)
    public Optional<Label> findByKey(LabelKey labelKey) {
        return labelRepository.findByKey(labelKey);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Label> findByBarcodeValue(String barcodeValue) {
        return labelRepository.findByBarcodeValue(barcodeValue);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Label> findBySourceKey(String sourceKey) {
        return labelRepository.findBySourceKey(sourceKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Label> findByWarehouse(WarehouseKey warehouseKey) {
        return labelRepository.findByWarehouseKey(warehouseKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Label> findPendingLabels(WarehouseKey warehouseKey) {
        return labelRepository.findPendingLabels(warehouseKey);
    }

    // Label Operations
    @Override
    public void markAsRendered(LabelKey labelKey) {
        Label label = findByKeyOrThrow(labelKey);
        label.markAsRendered();
        labelRepository.save(label);
    }

    @Override
    public void markAsPrinted(LabelKey labelKey) {
        Label label = findByKeyOrThrow(labelKey);
        label.markAsPrinted();
        labelRepository.save(label);

        eventPublisher.publishEvent(new LabelPrinted(
                labelKey,
                null, // Printer key to be set by caller
                null, // Job key to be set by caller
                Instant.now()
        ));
    }

    @Override
    public void voidLabel(LabelKey labelKey, String reason, UserKey voidedBy) {
        Label label = findByKeyOrThrow(labelKey);
        label.voidLabel(reason);
        labelRepository.save(label);

        eventPublisher.publishEvent(new LabelVoided(
                labelKey,
                reason,
                voidedBy,
                Instant.now()
        ));
    }

    @Override
    public void reprintLabel(LabelKey labelKey, int copies) {
        Label label = findByKeyOrThrow(labelKey);
        label.requestReprint(copies);
        labelRepository.save(label);

        eventPublisher.publishEvent(new LabelReprinted(
                labelKey,
                copies,
                null,
                Instant.now()
        ));
    }

    // Template Management
    @Override
    public LabelTemplate createTemplate(LabelTemplate template, UserKey createdBy) {
        if (templateRepository.existsByCode(template.getTemplateCode(), template.getWarehouseKey())) {
            throw new PrintingException("Template code already exists: " + template.getTemplateCode());
        }

        template.setCreatedAt(Instant.now());
        template.setCreatedBy(createdBy.value());
        LabelTemplate saved = templateRepository.save(template);

        eventPublisher.publishEvent(new TemplateCreated(
                saved.getTemplateKey(),
                saved.getTemplateCode(),
                saved.getTemplateName(),
                saved.getLabelType().name(),
                saved.getFormat(),
                saved.getWarehouseKey(),
                createdBy,
                Instant.now()
        ));

        return saved;
    }

    @Override
    public LabelTemplate updateTemplate(TemplateKey templateKey, LabelTemplate template, UserKey updatedBy) {
        LabelTemplate existing = templateRepository.findByKey(templateKey)
                .orElseThrow(() -> new TemplateNotFoundException("Template not found"));

        existing.setTemplateName(template.getTemplateName());
        existing.setDescription(template.getDescription());
        existing.setTemplateContent(template.getTemplateContent());
        existing.setDimensions(template.getDimensions());
        existing.setFields(template.getFields());
        existing.setBarcodeFields(template.getBarcodeFields());
        existing.setCompatiblePrinterTypes(template.getCompatiblePrinterTypes());
        existing.setDefaultValues(template.getDefaultValues());
        existing.setUpdatedBy(updatedBy.value());
        existing.incrementVersion();

        return templateRepository.save(existing);
    }

    @Override
    public void activateTemplate(TemplateKey templateKey) {
        LabelTemplate template = findTemplateOrThrow(templateKey);
        template.activate();
        templateRepository.save(template);

        eventPublisher.publishEvent(new TemplateActivated(templateKey, Instant.now()));
    }

    @Override
    public void deprecateTemplate(TemplateKey templateKey) {
        LabelTemplate template = findTemplateOrThrow(templateKey);
        template.deprecate();
        templateRepository.save(template);

        eventPublisher.publishEvent(new TemplateDeprecated(templateKey, Instant.now()));
    }

    @Override
    public void archiveTemplate(TemplateKey templateKey) {
        LabelTemplate template = findTemplateOrThrow(templateKey);
        template.archive();
        templateRepository.save(template);

        eventPublisher.publishEvent(new TemplateArchived(templateKey, Instant.now()));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LabelTemplate> findTemplateByKey(TemplateKey templateKey) {
        return templateRepository.findByKey(templateKey);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LabelTemplate> findTemplateByCode(String templateCode, WarehouseKey warehouseKey) {
        return templateRepository.findByCode(templateCode, warehouseKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LabelTemplate> findTemplatesByType(Label.LabelType labelType, WarehouseKey warehouseKey) {
        return templateRepository.findByLabelType(labelType, warehouseKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LabelTemplate> findActiveTemplates(WarehouseKey warehouseKey) {
        return templateRepository.findActiveTemplates(warehouseKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LabelTemplate> findTemplatesForStorer(StorerKey storerKey) {
        return templateRepository.findByStorerKey(storerKey);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTemplateCompatibleWithPrinter(TemplateKey templateKey, PrinterKey printerKey) {
        // TODO: Implement printer compatibility check
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LabelTemplate> findCompatibleTemplates(PrinterKey printerKey) {
        // TODO: Implement based on printer type
        return List.of();
    }

    // Helper Methods
    private Label findByKeyOrThrow(LabelKey labelKey) {
        return labelRepository.findByKey(labelKey)
                .orElseThrow(() -> new LabelNotFoundException("Label not found: " + labelKey.value()));
    }

    private LabelTemplate findTemplateOrThrow(TemplateKey templateKey) {
        return templateRepository.findByKey(templateKey)
                .orElseThrow(() -> new TemplateNotFoundException("Template not found: " + templateKey.value()));
    }

    private TemplateKey findDefaultTemplateForType(Label.LabelType labelType, WarehouseKey warehouseKey) {
        return templateRepository.findByLabelType(labelType, warehouseKey)
                .stream()
                .filter(LabelTemplate::isActive)
                .findFirst()
                .map(LabelTemplate::getTemplateKey)
                .orElseThrow(() -> new TemplateNotFoundException("No active template found for type: " + labelType));
    }

    private String generateBarcodeValue(LabelData data) {
        // TODO: Implement barcode value generation based on label data
        if (data.barcodes() != null && !data.barcodes().isEmpty()) {
            return data.barcodes().values().iterator().next();
        }
        return UUID.randomUUID().toString();
    }

    private byte[] renderLabelContent(Label label, LabelTemplate template, String format) {
        // TODO: Implement actual rendering logic
        // This would use ZPL, EPL, or PDF generation based on format
        return new byte[0];
    }
}
