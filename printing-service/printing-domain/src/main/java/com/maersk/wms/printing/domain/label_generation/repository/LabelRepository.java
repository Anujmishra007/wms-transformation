package com.maersk.wms.printing.domain.label_generation.repository;

import com.maersk.wms.printing.domain.label_generation.model.Label;
import com.maersk.wms.printing.shared.kernel.identifiers.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Label persistence operations.
 */
public interface LabelRepository {

    Label save(Label label);

    List<Label> saveAll(List<Label> labels);

    Optional<Label> findByKey(LabelKey labelKey);

    Optional<Label> findByBarcodeValue(String barcodeValue);

    List<Label> findBySourceKey(String sourceKey);

    List<Label> findBySourceTypeAndKey(String sourceType, String sourceKey);

    List<Label> findByTemplateKey(TemplateKey templateKey);

    List<Label> findByWarehouseKey(WarehouseKey warehouseKey);

    List<Label> findByStatus(Label.LabelStatus status, WarehouseKey warehouseKey);

    List<Label> findByType(Label.LabelType type, WarehouseKey warehouseKey);

    List<Label> findByTypeAndStatus(Label.LabelType type, Label.LabelStatus status, WarehouseKey warehouseKey);

    List<Label> findByCreatedAtBetween(Instant from, Instant to, WarehouseKey warehouseKey);

    List<Label> findByCreatedBy(UserKey userKey);

    List<Label> findPendingLabels(WarehouseKey warehouseKey);

    List<Label> findVoidedLabels(WarehouseKey warehouseKey);

    void delete(LabelKey labelKey);

    void deleteBySourceKey(String sourceKey);

    boolean existsByKey(LabelKey labelKey);

    boolean existsByBarcodeValue(String barcodeValue);

    long countByStatus(Label.LabelStatus status, WarehouseKey warehouseKey);

    long countByType(Label.LabelType type, WarehouseKey warehouseKey);

    long countByCreatedAtBetween(Instant from, Instant to, WarehouseKey warehouseKey);
}
