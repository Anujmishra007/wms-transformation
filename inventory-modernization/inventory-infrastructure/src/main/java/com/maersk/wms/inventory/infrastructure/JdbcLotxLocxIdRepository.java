package com.maersk.wms.inventory.infrastructure;

import com.maersk.wms.inventory.domain.LotxLocxId;
import com.maersk.wms.inventory.domain.InventoryStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of LotxLocxIdRepository.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class JdbcLotxLocxIdRepository implements LotxLocxIdRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<LotxLocxId> rowMapper = new LotxLocxIdRowMapper();

    @Override
    public Optional<LotxLocxId> findById(String lotxLocxIdKey) {
        String sql = "SELECT * FROM LOTxLOCxID WHERE LOTXLOCXIDKEY = ?";
        List<LotxLocxId> results = jdbcTemplate.query(sql, rowMapper, lotxLocxIdKey);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<LotxLocxId> findBySku(String sku, String warehouse) {
        String sql = "SELECT * FROM LOTxLOCxID WHERE SKU = ? AND STORERKEY = ?";
        return jdbcTemplate.query(sql, rowMapper, sku, warehouse);
    }

    @Override
    public List<LotxLocxId> findAvailableBySku(String sku, String warehouse) {
        String sql = """
            SELECT * FROM LOTxLOCxID
            WHERE SKU = ? AND STORERKEY = ?
            AND STATUS = 0 AND HOLDCODE IS NULL
            AND (QTY - QTYALLOCATED - QTYPICKED) > 0
            ORDER BY RECEIPTDATE ASC
            """;
        return jdbcTemplate.query(sql, rowMapper, sku, warehouse);
    }

    @Override
    public List<LotxLocxId> findByLocation(String location, String warehouse) {
        String sql = "SELECT * FROM LOTxLOCxID WHERE LOC = ? AND STORERKEY = ?";
        return jdbcTemplate.query(sql, rowMapper, location, warehouse);
    }

    @Override
    public List<LotxLocxId> findByLpn(String lpn, String warehouse) {
        String sql = "SELECT * FROM LOTxLOCxID WHERE ID = ? AND STORERKEY = ?";
        return jdbcTemplate.query(sql, rowMapper, lpn, warehouse);
    }

    @Override
    public List<LotxLocxId> findByLot(String lot, String sku, String warehouse) {
        String sql = "SELECT * FROM LOTxLOCxID WHERE LOT = ? AND SKU = ? AND STORERKEY = ?";
        return jdbcTemplate.query(sql, rowMapper, lot, sku, warehouse);
    }

    @Override
    public LotxLocxId save(LotxLocxId inventory) {
        log.info("Saving inventory: {}", inventory.getLotxLocxIdKey());
        // Implementation
        return inventory;
    }

    @Override
    public void updateQty(String lotxLocxIdKey, BigDecimal newQty) {
        String sql = "UPDATE LOTxLOCxID SET QTY = ?, EDITDATE = GETDATE() WHERE LOTXLOCXIDKEY = ?";
        jdbcTemplate.update(sql, newQty, lotxLocxIdKey);
    }

    @Override
    public void updateStatus(String lotxLocxIdKey, InventoryStatus status) {
        String sql = "UPDATE LOTxLOCxID SET STATUS = ?, EDITDATE = GETDATE() WHERE LOTXLOCXIDKEY = ?";
        jdbcTemplate.update(sql, status.getLegacyCode(), lotxLocxIdKey);
    }

    @Override
    public void updateAllocatedQty(String lotxLocxIdKey, BigDecimal allocatedQty) {
        String sql = "UPDATE LOTxLOCxID SET QTYALLOCATED = ?, EDITDATE = GETDATE() WHERE LOTXLOCXIDKEY = ?";
        jdbcTemplate.update(sql, allocatedQty, lotxLocxIdKey);
    }

    @Override
    public void delete(String lotxLocxIdKey) {
        String sql = "DELETE FROM LOTxLOCxID WHERE LOTXLOCXIDKEY = ?";
        jdbcTemplate.update(sql, lotxLocxIdKey);
    }

    @Override
    public BigDecimal getTotalAvailableQty(String sku, String warehouse) {
        String sql = """
            SELECT COALESCE(SUM(QTY - QTYALLOCATED - QTYPICKED), 0)
            FROM LOTxLOCxID
            WHERE SKU = ? AND STORERKEY = ?
            AND STATUS = 0 AND HOLDCODE IS NULL
            """;
        return jdbcTemplate.queryForObject(sql, BigDecimal.class, sku, warehouse);
    }

    private static class LotxLocxIdRowMapper implements RowMapper<LotxLocxId> {
        @Override
        public LotxLocxId mapRow(ResultSet rs, int rowNum) throws SQLException {
            return LotxLocxId.builder()
                    .lotxLocxIdKey(rs.getString("LOTXLOCXIDKEY"))
                    .sku(rs.getString("SKU"))
                    .lot(rs.getString("LOT"))
                    .location(rs.getString("LOC"))
                    .id(rs.getString("ID"))
                    .qty(rs.getBigDecimal("QTY"))
                    .qtyAllocated(rs.getBigDecimal("QTYALLOCATED"))
                    .qtyPicked(rs.getBigDecimal("QTYPICKED"))
                    .status(InventoryStatus.fromLegacyCode(rs.getInt("STATUS")))
                    .holdCode(rs.getString("HOLDCODE"))
                    .lottable01(rs.getString("LOTTABLE01"))
                    .lottable02(rs.getString("LOTTABLE02"))
                    .lottable03(rs.getString("LOTTABLE03"))
                    .lottable04(rs.getString("LOTTABLE04"))
                    .lottable05(rs.getString("LOTTABLE05"))
                    .build();
        }
    }
}
