package com.maersk.wms.outbound.infrastructure;

import com.maersk.wms.outbound.domain.Wave;
import com.maersk.wms.outbound.domain.WaveStatus;
import com.maersk.wms.outbound.domain.repository.WaveRepository;
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
 * JDBC implementation of WaveRepository.
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class JdbcWaveRepository implements WaveRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_BASE = """
            SELECT WAVEKEY, STORERKEY, WAVETYPE, WAVEDESCRIPTION, STATUS,
                   PLANNEDSTARTTIME, ACTUALSTARTTIME, COMPLETEDTIME,
                   TOTALORDERS, TOTALLINES, TOTALQTY, TOTALWEIGHT, TOTALVOLUME,
                   ORDERSALLOCATED, ORDERSPICKED, ORDERSPACKED, ORDERSSHIPPED,
                   CARRIERCODE, ROUTEKEY, DOOR,
                   CREATEDBY, RELEASEDBY,
                   ADDWHO, ADDDATE, EDITWHO, EDITDATE
            FROM WAVE
            """;

    @Override
    public Optional<Wave> findByKey(String waveKey) {
        String sql = SELECT_BASE + " WHERE WAVEKEY = ?";
        List<Wave> results = jdbcTemplate.query(sql, new WaveRowMapper(), waveKey);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public Optional<Wave> findByWaveKey(String waveKey) {
        return findByKey(waveKey);
    }

    @Override
    public List<Wave> findByStatus(WaveStatus status) {
        String sql = SELECT_BASE + " WHERE STATUS = ?";
        return jdbcTemplate.query(sql, new WaveRowMapper(), status.getCode());
    }

    @Override
    public List<Wave> findByStorerKey(String storerKey) {
        String sql = SELECT_BASE + " WHERE STORERKEY = ?";
        return jdbcTemplate.query(sql, new WaveRowMapper(), storerKey);
    }

    @Override
    public List<Wave> findByDateRange(LocalDateTime fromDate, LocalDateTime toDate) {
        String sql = SELECT_BASE + " WHERE ADDDATE BETWEEN ? AND ?";
        return jdbcTemplate.query(sql, new WaveRowMapper(), fromDate, toDate);
    }

    @Override
    public List<Wave> findActiveWaves(String storerKey) {
        String sql = SELECT_BASE + " WHERE STORERKEY = ? AND STATUS IN (?, ?, ?)";
        return jdbcTemplate.query(sql, new WaveRowMapper(),
                storerKey,
                WaveStatus.RELEASED.getCode(),
                WaveStatus.IN_PROGRESS.getCode(),
                WaveStatus.PICKING.getCode());
    }

    @Override
    public Wave save(Wave wave) {
        if (exists(wave.getWaveKey())) {
            return update(wave);
        }
        return insert(wave);
    }

    @Override
    public void delete(String waveKey) {
        String sql = "DELETE FROM WAVE WHERE WAVEKEY = ?";
        jdbcTemplate.update(sql, waveKey);
    }

    @Override
    public String generateWaveKey() {
        String keyQuery = "EXEC nspg_GetKey 'WAVE', 1";
        String key = jdbcTemplate.queryForObject(keyQuery, String.class);
        return "W" + key;
    }

    private boolean exists(String waveKey) {
        if (waveKey == null) {
            return false;
        }
        String sql = "SELECT COUNT(*) FROM WAVE WHERE WAVEKEY = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, waveKey);
        return count != null && count > 0;
    }

    private Wave insert(Wave wave) {
        String waveKey = wave.getWaveKey();
        if (waveKey == null) {
            waveKey = generateWaveKey();
            wave.setWaveKey(waveKey);
        }

        String sql = """
            INSERT INTO WAVE (WAVEKEY, STORERKEY, WAVETYPE, WAVEDESCRIPTION, STATUS,
                PLANNEDSTARTTIME, TOTALORDERS, TOTALLINES, TOTALQTY, TOTALWEIGHT, TOTALVOLUME,
                CARRIERCODE, ROUTEKEY, DOOR, CREATEDBY, ADDWHO, ADDDATE)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE())
            """;

        jdbcTemplate.update(sql,
                waveKey,
                wave.getStorerKey(),
                wave.getWaveType(),
                wave.getWaveDescription(),
                wave.getStatus() != null ? wave.getStatus().getCode() : WaveStatus.PLANNED.getCode(),
                wave.getPlannedStartTime(),
                wave.getTotalOrders(),
                wave.getTotalLines(),
                wave.getTotalQty(),
                wave.getTotalWeight(),
                wave.getTotalVolume(),
                wave.getCarrierCode(),
                wave.getRouteKey(),
                wave.getDoor(),
                wave.getCreatedBy(),
                wave.getAddWho()
        );

        return wave;
    }

    private Wave update(Wave wave) {
        String sql = """
            UPDATE WAVE SET
                STATUS = ?,
                ACTUALSTARTTIME = ?,
                COMPLETEDTIME = ?,
                TOTALORDERS = ?,
                TOTALLINES = ?,
                TOTALQTY = ?,
                ORDERSALLOCATED = ?,
                ORDERSPICKED = ?,
                ORDERSPACKED = ?,
                ORDERSSHIPPED = ?,
                RELEASEDBY = ?,
                EDITWHO = ?,
                EDITDATE = GETDATE()
            WHERE WAVEKEY = ?
            """;

        jdbcTemplate.update(sql,
                wave.getStatus() != null ? wave.getStatus().getCode() : null,
                wave.getActualStartTime(),
                wave.getCompletedTime(),
                wave.getTotalOrders(),
                wave.getTotalLines(),
                wave.getTotalQty(),
                wave.getOrdersAllocated(),
                wave.getOrdersPicked(),
                wave.getOrdersPacked(),
                wave.getOrdersShipped(),
                wave.getReleasedBy(),
                wave.getEditWho(),
                wave.getWaveKey()
        );

        return wave;
    }

    private static class WaveRowMapper implements RowMapper<Wave> {
        @Override
        public Wave mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Wave.builder()
                    .waveKey(rs.getString("WAVEKEY"))
                    .storerKey(rs.getString("STORERKEY"))
                    .waveType(rs.getString("WAVETYPE"))
                    .waveDescription(rs.getString("WAVEDESCRIPTION"))
                    .status(WaveStatus.fromCode(rs.getString("STATUS")))
                    .plannedStartTime(rs.getTimestamp("PLANNEDSTARTTIME") != null ? rs.getTimestamp("PLANNEDSTARTTIME").toLocalDateTime() : null)
                    .actualStartTime(rs.getTimestamp("ACTUALSTARTTIME") != null ? rs.getTimestamp("ACTUALSTARTTIME").toLocalDateTime() : null)
                    .completedTime(rs.getTimestamp("COMPLETEDTIME") != null ? rs.getTimestamp("COMPLETEDTIME").toLocalDateTime() : null)
                    .totalOrders(rs.getInt("TOTALORDERS"))
                    .totalLines(rs.getInt("TOTALLINES"))
                    .totalQty(rs.getBigDecimal("TOTALQTY"))
                    .totalWeight(rs.getBigDecimal("TOTALWEIGHT"))
                    .totalVolume(rs.getBigDecimal("TOTALVOLUME"))
                    .ordersAllocated(rs.getInt("ORDERSALLOCATED"))
                    .ordersPicked(rs.getInt("ORDERSPICKED"))
                    .ordersPacked(rs.getInt("ORDERSPACKED"))
                    .ordersShipped(rs.getInt("ORDERSSHIPPED"))
                    .carrierCode(rs.getString("CARRIERCODE"))
                    .routeKey(rs.getString("ROUTEKEY"))
                    .door(rs.getString("DOOR"))
                    .createdBy(rs.getString("CREATEDBY"))
                    .releasedBy(rs.getString("RELEASEDBY"))
                    .addWho(rs.getString("ADDWHO"))
                    .addDate(rs.getTimestamp("ADDDATE") != null ? rs.getTimestamp("ADDDATE").toLocalDateTime() : null)
                    .editWho(rs.getString("EDITWHO"))
                    .editDate(rs.getTimestamp("EDITDATE") != null ? rs.getTimestamp("EDITDATE").toLocalDateTime() : null)
                    .build();
        }
    }
}
