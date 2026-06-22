package com.maersk.wms.printing.domain.label_generation.service;

import com.maersk.wms.printing.domain.label_generation.model.Label;
import com.maersk.wms.printing.domain.label_generation.model.LabelTemplate;
import com.maersk.wms.printing.shared.kernel.identifiers.*;
import com.maersk.wms.printing.shared.kernel.valueobjects.LabelData;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for label generation operations.
 * Handles label creation, rendering, and template management.
 */
public interface LabelGenerationService {

    // Label Creation
    Label createLabel(TemplateKey templateKey, LabelData data, WarehouseKey warehouseKey, UserKey createdBy);

    Label createShippingLabel(String sourceKey, LabelData data, WarehouseKey warehouseKey, UserKey createdBy);

    Label createLpnLabel(String lpnNumber, LabelData data, WarehouseKey warehouseKey, UserKey createdBy);

    Label createLocationLabel(String locationCode, LabelData data, WarehouseKey warehouseKey, UserKey createdBy);

    List<Label> createBatchLabels(TemplateKey templateKey, List<LabelData> dataList, WarehouseKey warehouseKey, UserKey createdBy);

    // Label Rendering
    byte[] renderLabel(LabelKey labelKey, String format);

    byte[] renderLabelPreview(TemplateKey templateKey, LabelData data);

    List<byte[]> renderBatchLabels(List<LabelKey> labelKeys, String format);

    // Label Retrieval
    Optional<Label> findByKey(LabelKey labelKey);

    Optional<Label> findByBarcodeValue(String barcodeValue);

    List<Label> findBySourceKey(String sourceKey);

    List<Label> findByWarehouse(WarehouseKey warehouseKey);

    List<Label> findPendingLabels(WarehouseKey warehouseKey);

    // Label Operations
    void markAsRendered(LabelKey labelKey);

    void markAsPrinted(LabelKey labelKey);

    void voidLabel(LabelKey labelKey, String reason, UserKey voidedBy);

    void reprintLabel(LabelKey labelKey, int copies);

    // Template Management
    LabelTemplate createTemplate(LabelTemplate template, UserKey createdBy);

    LabelTemplate updateTemplate(TemplateKey templateKey, LabelTemplate template, UserKey updatedBy);

    void activateTemplate(TemplateKey templateKey);

    void deprecateTemplate(TemplateKey templateKey);

    void archiveTemplate(TemplateKey templateKey);

    Optional<LabelTemplate> findTemplateByKey(TemplateKey templateKey);

    Optional<LabelTemplate> findTemplateByCode(String templateCode, WarehouseKey warehouseKey);

    List<LabelTemplate> findTemplatesByType(Label.LabelType labelType, WarehouseKey warehouseKey);

    List<LabelTemplate> findActiveTemplates(WarehouseKey warehouseKey);

    List<LabelTemplate> findTemplatesForStorer(StorerKey storerKey);

    // Template Compatibility
    boolean isTemplateCompatibleWithPrinter(TemplateKey templateKey, PrinterKey printerKey);

    List<LabelTemplate> findCompatibleTemplates(PrinterKey printerKey);
}
