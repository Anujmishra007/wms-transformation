package com.maersk.wms.printing.domain.label_generation.repository;

import com.maersk.wms.printing.domain.label_generation.model.Label;
import com.maersk.wms.printing.domain.label_generation.model.LabelTemplate;
import com.maersk.wms.printing.shared.kernel.identifiers.*;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for LabelTemplate persistence operations.
 */
public interface LabelTemplateRepository {

    LabelTemplate save(LabelTemplate template);

    Optional<LabelTemplate> findByKey(TemplateKey templateKey);

    Optional<LabelTemplate> findByCode(String templateCode, WarehouseKey warehouseKey);

    Optional<LabelTemplate> findByCodeAndStorer(String templateCode, StorerKey storerKey);

    List<LabelTemplate> findByWarehouseKey(WarehouseKey warehouseKey);

    List<LabelTemplate> findByStorerKey(StorerKey storerKey);

    List<LabelTemplate> findByLabelType(Label.LabelType labelType, WarehouseKey warehouseKey);

    List<LabelTemplate> findByLabelTypeAndSubType(Label.LabelType labelType, String subType, WarehouseKey warehouseKey);

    List<LabelTemplate> findByStatus(LabelTemplate.TemplateStatus status, WarehouseKey warehouseKey);

    List<LabelTemplate> findActiveTemplates(WarehouseKey warehouseKey);

    List<LabelTemplate> findByFormat(String format, WarehouseKey warehouseKey);

    List<LabelTemplate> findCompatibleWithPrinterType(String printerType, WarehouseKey warehouseKey);

    List<LabelTemplate> findSystemTemplates();

    void delete(TemplateKey templateKey);

    boolean existsByKey(TemplateKey templateKey);

    boolean existsByCode(String templateCode, WarehouseKey warehouseKey);

    long countByStatus(LabelTemplate.TemplateStatus status, WarehouseKey warehouseKey);

    long countByLabelType(Label.LabelType labelType, WarehouseKey warehouseKey);
}
