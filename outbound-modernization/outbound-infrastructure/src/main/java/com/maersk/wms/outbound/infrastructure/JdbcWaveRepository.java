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

    @Override
    public Wave save(Wave wave) {
        if (wave.getId() == null) {
            return insert(wave);
        }
        return update(wave);
    }

    @Override
    public Optional<Wave> findById(Long id) {
        String sql = "SELECT * FROM WAVEHEADER WHERE WAVEHEADER_ID = ?";
        List<Wave> results = jdbcTemplate.query(sql, new WaveRowMapper(), id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public Optional<Wave> findByWaveNumber(String waveNumber) {
        String sql = "SELECT * FROM WAVEHEADER WHERE WAVEKEY = ?";
        List<Wave> results = jdbcTemplate.query(sql, new WaveRowMapper(), waveNumber);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<Wave> findByStatus(WaveStatus status) {
        String sql = "SELECT * FROM WAVEHEADER WHERE STATUS = ?";
        return jdbcTemplate.query(sql, new WaveRowMapper(), status.getCode());
    }

    @Override
    public void delete(Wave wave) {
        String sql = "DELETE FROM WAVEHEADER WHERE WAVEHEADER_ID = ?";
        jdbcTemplate.update(sql, wave.getId());
    }

    private Wave insert(Wave wave) {
        // Get next key
        String keyQuery = "EXEC nspg_GetKey 'WAVEHEADER', 1";
        Long newId = jdbcTemplate.queryForObject(keyQuery, Long.class);
        wave.setId(newId);

        // Generate wave number
        String waveNumberQuery = "EXEC nspg_GetKey 'WAVE', 1";
        Long waveNum = jdbcTemplate.queryForObject(waveNumberQuery, Long.class);
        wave.setWaveNumber("W" + String.format("%010d", waveNum));

        String sql = """
            INSERT INTO WAVEHEADER (WAVEHEADER_ID, WAVEKEY, WAVETYPE, STATUS,
                ORDERCOUNT, LINECOUNT, TOTALUNITS, ADDWHO, ADDDATE)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, GETDATE())
            """;

        jdbcTemplate.update(sql,
                wave.getId(),
                wave.getWaveNumber(),
                wave.getWaveType(),
                wave.getStatus().getCode(),
                wave.getOrderCount(),
                wave.getLineCount(),
                wave.getTotalUnits(),
                wave.getCreatedBy()
        );

        return wave;
    }

    private Wave update(Wave wave) {
        String sql = """
            UPDATE WAVEHEADER SET
                STATUS = ?,
                ORDERCOUNT = ?,
                LINECOUNT = ?,
                TOTALUNITS = ?,
                RELEASEDDATE = ?,
                RELEASEDBY = ?,
                EDITWHO = ?,
                EDITDATE = GETDATE()
            WHERE WAVEHEADER_ID = ?
            """;

        jdbcTemplate.update(sql,
                wave.getStatus().getCode(),
                wave.getOrderCount(),
                wave.getLineCount(),
                wave.getTotalUnits(),
                wave.getReleasedAt(),
                wave.getReleasedBy(),
                wave.getUpdatedBy(),
                wave.getId()
        );

        return wave;
    }

    private static class WaveRowMapper implements RowMapper<Wave> {
        @Override
        public Wave mapRow(ResultSet rs, int rowNum) throws SQLException {
            Wave wave = new Wave();
            wave.setId(rs.getLong("WAVEHEADER_ID"));
            wave.setWaveNumber(rs.getString("WAVEKEY"));
            wave.setWaveType(rs.getString("WAVETYPE"));
            wave.setStatus(WaveStatus.fromCode(rs.getString("STATUS")));
            wave.setOrderCount(rs.getInt("ORDERCOUNT"));
            wave.setLineCount(rs.getInt("LINECOUNT"));
            wave.setTotalUnits(rs.getInt("TOTALUNITS"));
            wave.setCreatedBy(rs.getString("ADDWHO"));
            if (rs.getTimestamp("ADDDATE") != null) {
                wave.setCreatedAt(rs.getTimestamp("ADDDATE").toLocalDateTime());
            }
            if (rs.getTimestamp("RELEASEDDATE") != null) {
                wave.setReleasedAt(rs.getTimestamp("RELEASEDDATE").toLocalDateTime());
            }
            wave.setReleasedBy(rs.getString("RELEASEDBY"));
            return wave;
        }
    }
}
