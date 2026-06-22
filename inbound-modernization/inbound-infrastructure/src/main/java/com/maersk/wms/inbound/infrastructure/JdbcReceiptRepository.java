package com.maersk.wms.inbound.infrastructure;

import com.maersk.wms.inbound.domain.operations_service.Receipt;
import com.maersk.wms.inbound.domain.operations_service.ReceiptStatus;
import com.maersk.wms.inbound.domain.operations_service.ReceiptType;
import com.maersk.wms.inbound.domain.operations_service.repository.ReceiptRepository;
import com.maersk.wms.inbound.shared.kernel.identifiers.ReceiptKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.StorerKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of ReceiptRepository.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class JdbcReceiptRepository implements ReceiptRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_BASE = """
            SELECT RECEIPTKEY, RECEIPTTYPE, EXTERNRECEIPTKEY, STORERKEY, POKEY, ASNKEY,
                   CARRIERKEY, CARRIERNAME, TRAILERNUMBER, SEALNUMBER, DOOR,
                   STATUS, EXPECTEDARRIVALDATE, ACTUALARRIVALDATE, RECEIPTDATE, CLOSEDDATE,
                   TOTALEXPECTEDQTY, TOTALRECEIVEDQTY, TOTALDAMAGEDQTY,
                   WAREHOUSEREFERENCE, NOTES,
                   ADDWHO, ADDDATE, EDITWHO, EDITDATE
            FROM RECEIPT
            """;

    @Override
    public Optional<Receipt> findByKey(ReceiptKey receiptKey) {
        String sql = SELECT_BASE + " WHERE RECEIPTKEY = ?";
        List<Receipt> results = jdbcTemplate.query(sql, new ReceiptRowMapper(), receiptKey.value());
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public Optional<Receipt> findByExternalKey(String externalReceiptKey) {
        String sql = SELECT_BASE + " WHERE EXTERNRECEIPTKEY = ?";
        List<Receipt> results = jdbcTemplate.query(sql, new ReceiptRowMapper(), externalReceiptKey);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<Receipt> findByStorerKey(StorerKey storerKey) {
        String sql = SELECT_BASE + " WHERE STORERKEY = ? ORDER BY ADDDATE DESC";
        return jdbcTemplate.query(sql, new ReceiptRowMapper(), storerKey.value());
    }

    @Override
    public List<Receipt> findByStatus(ReceiptStatus status) {
        String sql = SELECT_BASE + " WHERE STATUS = ?";
        return jdbcTemplate.query(sql, new ReceiptRowMapper(), status.getCode());
    }

    @Override
    public List<Receipt> findByStorerAndStatus(StorerKey storerKey, ReceiptStatus status) {
        String sql = SELECT_BASE + " WHERE STORERKEY = ? AND STATUS = ?";
        return jdbcTemplate.query(sql, new ReceiptRowMapper(), storerKey.value(), status.getCode());
    }

    @Override
    public List<Receipt> findByPoKey(String poKey) {
        String sql = SELECT_BASE + " WHERE POKEY = ?";
        return jdbcTemplate.query(sql, new ReceiptRowMapper(), poKey);
    }

    @Override
    public List<Receipt> findByAsnKey(String asnKey) {
        String sql = SELECT_BASE + " WHERE ASNKEY = ?";
        return jdbcTemplate.query(sql, new ReceiptRowMapper(), asnKey);
    }

    @Override
    public List<Receipt> findByDateRange(LocalDate from, LocalDate to) {
        String sql = SELECT_BASE + " WHERE CAST(RECEIPTDATE AS DATE) BETWEEN ? AND ? ORDER BY RECEIPTDATE DESC";
        return jdbcTemplate.query(sql, new ReceiptRowMapper(), from, to);
    }

    @Override
    public List<Receipt> findActiveByStorer(StorerKey storerKey) {
        String sql = SELECT_BASE + " WHERE STORERKEY = ? AND STATUS IN ('0', '5', '9') ORDER BY RECEIPTDATE DESC";
        return jdbcTemplate.query(sql, new ReceiptRowMapper(), storerKey.value());
    }

    @Override
    public List<Receipt> findPendingPutaway() {
        String sql = SELECT_BASE + " WHERE STATUS = '5' ORDER BY RECEIPTDATE ASC";
        return jdbcTemplate.query(sql, new ReceiptRowMapper());
    }

    @Override
    public Receipt save(Receipt receipt) {
        // Simplified - would use proper upsert logic
        log.debug("Saving receipt: {}", receipt.getReceiptKey());
        return receipt;
    }

    @Override
    public void delete(ReceiptKey receiptKey) {
        String sql = "DELETE FROM RECEIPT WHERE RECEIPTKEY = ?";
        jdbcTemplate.update(sql, receiptKey.value());
    }

    @Override
    public boolean exists(ReceiptKey receiptKey) {
        String sql = "SELECT COUNT(1) FROM RECEIPT WHERE RECEIPTKEY = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, receiptKey.value());
        return count != null && count > 0;
    }

    @Override
    public long countByStatus(ReceiptStatus status) {
        String sql = "SELECT COUNT(1) FROM RECEIPT WHERE STATUS = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, status.getCode());
        return count != null ? count : 0L;
    }

    @Override
    public long countByStorerAndStatus(StorerKey storerKey, ReceiptStatus status) {
        String sql = "SELECT COUNT(1) FROM RECEIPT WHERE STORERKEY = ? AND STATUS = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, storerKey.value(), status.getCode());
        return count != null ? count : 0L;
    }

    private static class ReceiptRowMapper implements RowMapper<Receipt> {
        @Override
        public Receipt mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Receipt.builder()
                    .receiptKey(new ReceiptKey(rs.getString("RECEIPTKEY")))
                    .externalReceiptKey(rs.getString("EXTERNRECEIPTKEY"))
                    .storerKey(new StorerKey(rs.getString("STORERKEY")))
                    .receiptType(ReceiptType.fromCode(rs.getString("RECEIPTTYPE")))
                    .status(ReceiptStatus.fromCode(rs.getString("STATUS")))
                    .poKey(rs.getString("POKEY"))
                    .asnKey(rs.getString("ASNKEY"))
                    .carrierKey(rs.getString("CARRIERKEY"))
                    .carrierName(rs.getString("CARRIERNAME"))
                    .trailerNumber(rs.getString("TRAILERNUMBER"))
                    .sealNumber(rs.getString("SEALNUMBER"))
                    .door(rs.getString("DOOR"))
                    .notes(rs.getString("NOTES"))
                    .addWho(rs.getString("ADDWHO"))
                    .editWho(rs.getString("EDITWHO"))
                    .build();
        }
    }
}
