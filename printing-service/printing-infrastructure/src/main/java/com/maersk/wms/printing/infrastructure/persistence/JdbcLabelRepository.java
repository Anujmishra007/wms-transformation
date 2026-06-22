package com.maersk.wms.printing.infrastructure.persistence;

import com.maersk.wms.printing.domain.label_generation.model.Label;
import com.maersk.wms.printing.domain.label_generation.repository.LabelRepository;
import com.maersk.wms.printing.shared.kernel.identifiers.*;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * JDBC-based implementation of LabelRepository.
 */
@Repository
public class JdbcLabelRepository implements LabelRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcLabelRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Label> labelRowMapper = (rs, rowNum) -> Label.builder()
            .labelKey(new LabelKey(rs.getString("label_key")))
            .templateKey(new TemplateKey(rs.getString("template_key")))
            .warehouseKey(new WarehouseKey(rs.getString("warehouse_key")))
            .labelType(Label.LabelType.valueOf(rs.getString("label_type")))
            .sourceType(rs.getString("source_type"))
            .sourceKey(rs.getString("source_key"))
            .barcodeValue(rs.getString("barcode_value"))
            .status(Label.LabelStatus.valueOf(rs.getString("status")))
            .createdAt(rs.getTimestamp("created_at").toInstant())
            .createdBy(rs.getString("created_by"))
            .build();

    @Override
    public Label save(Label label) {
        // TODO: Implement upsert logic
        String sql = """
            MERGE INTO Labels AS target
            USING (SELECT ? AS label_key) AS source
            ON target.label_key = source.label_key
            WHEN MATCHED THEN
                UPDATE SET template_key = ?, warehouse_key = ?, label_type = ?,
                    source_type = ?, source_key = ?, barcode_value = ?, status = ?,
                    updated_at = GETUTCDATE()
            WHEN NOT MATCHED THEN
                INSERT (label_key, template_key, warehouse_key, label_type, source_type,
                    source_key, barcode_value, status, created_at, created_by)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, GETUTCDATE(), ?);
            """;
        // Execute and return label
        return label;
    }

    @Override
    public List<Label> saveAll(List<Label> labels) {
        labels.forEach(this::save);
        return labels;
    }

    @Override
    public Optional<Label> findByKey(LabelKey labelKey) {
        String sql = "SELECT * FROM Labels WHERE label_key = ?";
        List<Label> results = jdbcTemplate.query(sql, labelRowMapper, labelKey.value());
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public Optional<Label> findByBarcodeValue(String barcodeValue) {
        String sql = "SELECT * FROM Labels WHERE barcode_value = ?";
        List<Label> results = jdbcTemplate.query(sql, labelRowMapper, barcodeValue);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<Label> findBySourceKey(String sourceKey) {
        String sql = "SELECT * FROM Labels WHERE source_key = ?";
        return jdbcTemplate.query(sql, labelRowMapper, sourceKey);
    }

    @Override
    public List<Label> findBySourceTypeAndKey(String sourceType, String sourceKey) {
        String sql = "SELECT * FROM Labels WHERE source_type = ? AND source_key = ?";
        return jdbcTemplate.query(sql, labelRowMapper, sourceType, sourceKey);
    }

    @Override
    public List<Label> findByTemplateKey(TemplateKey templateKey) {
        String sql = "SELECT * FROM Labels WHERE template_key = ?";
        return jdbcTemplate.query(sql, labelRowMapper, templateKey.value());
    }

    @Override
    public List<Label> findByWarehouseKey(WarehouseKey warehouseKey) {
        String sql = "SELECT * FROM Labels WHERE warehouse_key = ?";
        return jdbcTemplate.query(sql, labelRowMapper, warehouseKey.value());
    }

    @Override
    public List<Label> findByStatus(Label.LabelStatus status, WarehouseKey warehouseKey) {
        String sql = "SELECT * FROM Labels WHERE status = ? AND warehouse_key = ?";
        return jdbcTemplate.query(sql, labelRowMapper, status.name(), warehouseKey.value());
    }

    @Override
    public List<Label> findByType(Label.LabelType type, WarehouseKey warehouseKey) {
        String sql = "SELECT * FROM Labels WHERE label_type = ? AND warehouse_key = ?";
        return jdbcTemplate.query(sql, labelRowMapper, type.name(), warehouseKey.value());
    }

    @Override
    public List<Label> findByTypeAndStatus(Label.LabelType type, Label.LabelStatus status, WarehouseKey warehouseKey) {
        String sql = "SELECT * FROM Labels WHERE label_type = ? AND status = ? AND warehouse_key = ?";
        return jdbcTemplate.query(sql, labelRowMapper, type.name(), status.name(), warehouseKey.value());
    }

    @Override
    public List<Label> findByCreatedAtBetween(Instant from, Instant to, WarehouseKey warehouseKey) {
        String sql = "SELECT * FROM Labels WHERE created_at BETWEEN ? AND ? AND warehouse_key = ?";
        return jdbcTemplate.query(sql, labelRowMapper, from, to, warehouseKey.value());
    }

    @Override
    public List<Label> findByCreatedBy(UserKey userKey) {
        String sql = "SELECT * FROM Labels WHERE created_by = ?";
        return jdbcTemplate.query(sql, labelRowMapper, userKey.value());
    }

    @Override
    public List<Label> findPendingLabels(WarehouseKey warehouseKey) {
        String sql = "SELECT * FROM Labels WHERE status IN ('CREATED', 'RENDERED') AND warehouse_key = ?";
        return jdbcTemplate.query(sql, labelRowMapper, warehouseKey.value());
    }

    @Override
    public List<Label> findVoidedLabels(WarehouseKey warehouseKey) {
        String sql = "SELECT * FROM Labels WHERE status = 'VOIDED' AND warehouse_key = ?";
        return jdbcTemplate.query(sql, labelRowMapper, warehouseKey.value());
    }

    @Override
    public void delete(LabelKey labelKey) {
        String sql = "DELETE FROM Labels WHERE label_key = ?";
        jdbcTemplate.update(sql, labelKey.value());
    }

    @Override
    public void deleteBySourceKey(String sourceKey) {
        String sql = "DELETE FROM Labels WHERE source_key = ?";
        jdbcTemplate.update(sql, sourceKey);
    }

    @Override
    public boolean existsByKey(LabelKey labelKey) {
        String sql = "SELECT COUNT(*) FROM Labels WHERE label_key = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, labelKey.value());
        return count != null && count > 0;
    }

    @Override
    public boolean existsByBarcodeValue(String barcodeValue) {
        String sql = "SELECT COUNT(*) FROM Labels WHERE barcode_value = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, barcodeValue);
        return count != null && count > 0;
    }

    @Override
    public long countByStatus(Label.LabelStatus status, WarehouseKey warehouseKey) {
        String sql = "SELECT COUNT(*) FROM Labels WHERE status = ? AND warehouse_key = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, status.name(), warehouseKey.value());
        return count != null ? count : 0L;
    }

    @Override
    public long countByType(Label.LabelType type, WarehouseKey warehouseKey) {
        String sql = "SELECT COUNT(*) FROM Labels WHERE label_type = ? AND warehouse_key = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, type.name(), warehouseKey.value());
        return count != null ? count : 0L;
    }

    @Override
    public long countByCreatedAtBetween(Instant from, Instant to, WarehouseKey warehouseKey) {
        String sql = "SELECT COUNT(*) FROM Labels WHERE created_at BETWEEN ? AND ? AND warehouse_key = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, from, to, warehouseKey.value());
        return count != null ? count : 0L;
    }
}
