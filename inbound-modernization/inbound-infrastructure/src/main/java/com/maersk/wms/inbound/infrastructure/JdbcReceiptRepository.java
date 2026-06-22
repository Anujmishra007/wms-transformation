package com.maersk.wms.inbound.infrastructure;

import com.maersk.wms.inbound.domain.Receipt;
import com.maersk.wms.inbound.domain.ReceiptStatus;
import com.maersk.wms.inbound.domain.repository.ReceiptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
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
                   LOTTABLE01, LOTTABLE02, LOTTABLE03, LOTTABLE04, LOTTABLE05,
                   LOTTABLE06, LOTTABLE07, LOTTABLE08, LOTTABLE09, LOTTABLE10,
                   ADDWHO, ADDDATE, EDITWHO, EDITDATE
            FROM RECEIPT
            """;

    @Override
    public Optional<Receipt> findByKey(String receiptKey) {
        String sql = SELECT_BASE + " WHERE RECEIPTKEY = ?";
        List<Receipt> results = jdbcTemplate.query(sql, new ReceiptRowMapper(), receiptKey);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public Optional<Receipt> findByExternalKey(String externalReceiptKey, String storerKey) {
        String sql = SELECT_BASE + " WHERE EXTERNRECEIPTKEY = ? AND STORERKEY = ?";
        List<Receipt> results = jdbcTemplate.query(sql, new ReceiptRowMapper(), externalReceiptKey, storerKey);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<Receipt> findByStatus(ReceiptStatus status) {
        String sql = SELECT_BASE + " WHERE STATUS = ?";
        return jdbcTemplate.query(sql, new ReceiptRowMapper(), status.getCode());
    }

    @Override
    public List<Receipt> findByStorerKey(String storerKey) {
        String sql = SELECT_BASE + " WHERE STORERKEY = ? ORDER BY ADDDATE DESC";
        return jdbcTemplate.query(sql, new ReceiptRowMapper(), storerKey);
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
    public List<Receipt> findByDateRange(LocalDateTime fromDate, LocalDateTime toDate) {
        String sql = SELECT_BASE + " WHERE RECEIPTDATE BETWEEN ? AND ? ORDER BY RECEIPTDATE DESC";
        return jdbcTemplate.query(sql, new ReceiptRowMapper(), fromDate, toDate);
    }

    @Override
    public Receipt save(Receipt receipt) {
        // Simplified - would use proper upsert logic
        log.debug("Saving receipt: {}", receipt.getReceiptKey());
        return receipt;
    }

    @Override
    public void delete(String receiptKey) {
        String sql = "DELETE FROM RECEIPT WHERE RECEIPTKEY = ?";
        jdbcTemplate.update(sql, receiptKey);
    }

    @Override
    public String generateReceiptKey() {
        String sql = "EXEC nspg_GetKey 'RECEIPT', 1";
        return jdbcTemplate.queryForObject(sql, String.class);
    }

    private static class ReceiptRowMapper implements RowMapper<Receipt> {
        @Override
        public Receipt mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Receipt.builder()
                    .receiptKey(rs.getString("RECEIPTKEY"))
                    .receiptType(rs.getString("RECEIPTTYPE"))
                    .externalReceiptKey(rs.getString("EXTERNRECEIPTKEY"))
                    .storerKey(rs.getString("STORERKEY"))
                    .poKey(rs.getString("POKEY"))
                    .asnKey(rs.getString("ASNKEY"))
                    .carrierKey(rs.getString("CARRIERKEY"))
                    .carrierName(rs.getString("CARRIERNAME"))
                    .trailerNumber(rs.getString("TRAILERNUMBER"))
                    .sealNumber(rs.getString("SEALNUMBER"))
                    .door(rs.getString("DOOR"))
                    .status(ReceiptStatus.fromCode(rs.getString("STATUS")))
                    .totalExpectedQty(rs.getBigDecimal("TOTALEXPECTEDQTY"))
                    .totalReceivedQty(rs.getBigDecimal("TOTALRECEIVEDQTY"))
                    .totalDamagedQty(rs.getBigDecimal("TOTALDAMAGEDQTY"))
                    .warehouseReference(rs.getString("WAREHOUSEREFERENCE"))
                    .notes(rs.getString("NOTES"))
                    .lottable01(rs.getString("LOTTABLE01"))
                    .lottable02(rs.getString("LOTTABLE02"))
                    .lottable03(rs.getString("LOTTABLE03"))
                    .lottable04(rs.getString("LOTTABLE04"))
                    .lottable05(rs.getString("LOTTABLE05"))
                    .addWho(rs.getString("ADDWHO"))
                    .editWho(rs.getString("EDITWHO"))
                    .build();
        }
    }
}
