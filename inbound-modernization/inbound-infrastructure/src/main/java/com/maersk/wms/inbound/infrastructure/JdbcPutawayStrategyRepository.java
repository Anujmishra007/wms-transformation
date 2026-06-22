package com.maersk.wms.inbound.infrastructure;

import com.maersk.wms.inbound.domain.putaway_service.PutawayStrategy;
import com.maersk.wms.inbound.domain.putaway_service.PutawayStrategyType;
import com.maersk.wms.inbound.domain.putaway_service.repository.PutawayStrategyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of PutawayStrategyRepository.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class JdbcPutawayStrategyRepository implements PutawayStrategyRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_BASE = """
            SELECT STRATEGYKEY, STRATEGYNAME, DESCRIPTION, STRATEGYTYPE,
                   ACTIVE, PRIORITY, ALLOWEDZONES, EXCLUDEDZONES, DEFAULTZONE,
                   PREFERREDLOCATIONTYPES, ALLOWMIXEDSKU, ALLOWMIXEDLOT,
                   CHECKCAPACITY, CONSOLIDATE, ENFORCEFIFO, CHECKEXPIRY,
                   MINDAYSTOEXPIRY, USEVELOCITY, VELOCITYZONEMAPPING,
                   FORRETURNS, RETURNZONE
            FROM PUTAWAYSTRATEGY
            """;

    @Override
    public Optional<PutawayStrategy> findByKey(String strategyKey) {
        String sql = SELECT_BASE + " WHERE STRATEGYKEY = ?";
        List<PutawayStrategy> results = jdbcTemplate.query(sql, new PutawayStrategyRowMapper(), strategyKey);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public Optional<PutawayStrategy> findByName(String strategyName) {
        String sql = SELECT_BASE + " WHERE STRATEGYNAME = ?";
        List<PutawayStrategy> results = jdbcTemplate.query(sql, new PutawayStrategyRowMapper(), strategyName);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<PutawayStrategy> findAll() {
        String sql = SELECT_BASE + " ORDER BY PRIORITY, STRATEGYNAME";
        return jdbcTemplate.query(sql, new PutawayStrategyRowMapper());
    }

    @Override
    public List<PutawayStrategy> findByType(PutawayStrategyType type) {
        String sql = SELECT_BASE + " WHERE STRATEGYTYPE = ? ORDER BY PRIORITY";
        return jdbcTemplate.query(sql, new PutawayStrategyRowMapper(), type.name());
    }

    @Override
    public List<PutawayStrategy> findActiveStrategies() {
        String sql = SELECT_BASE + " WHERE ACTIVE = 1 ORDER BY PRIORITY";
        return jdbcTemplate.query(sql, new PutawayStrategyRowMapper());
    }

    @Override
    public List<PutawayStrategy> findActive() {
        return findActiveStrategies();
    }

    @Override
    public Optional<PutawayStrategy> findDefaultStrategy() {
        String sql = SELECT_BASE + " WHERE ACTIVE = 1 ORDER BY PRIORITY FETCH FIRST 1 ROWS ONLY";
        List<PutawayStrategy> results = jdbcTemplate.query(sql, new PutawayStrategyRowMapper());
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public Optional<PutawayStrategy> findDefault() {
        return findDefaultStrategy();
    }

    @Override
    public List<PutawayStrategy> findForReturns() {
        String sql = SELECT_BASE + " WHERE FORRETURNS = 1 AND ACTIVE = 1 ORDER BY PRIORITY";
        return jdbcTemplate.query(sql, new PutawayStrategyRowMapper());
    }

    @Override
    public List<PutawayStrategy> findByPriority() {
        String sql = SELECT_BASE + " WHERE ACTIVE = 1 ORDER BY PRIORITY ASC";
        return jdbcTemplate.query(sql, new PutawayStrategyRowMapper());
    }

    @Override
    public boolean existsByName(String strategyName) {
        String sql = "SELECT COUNT(1) FROM PUTAWAYSTRATEGY WHERE STRATEGYNAME = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, strategyName);
        return count != null && count > 0;
    }

    @Override
    public boolean exists(String strategyKey) {
        String sql = "SELECT COUNT(1) FROM PUTAWAYSTRATEGY WHERE STRATEGYKEY = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, strategyKey);
        return count != null && count > 0;
    }

    @Override
    public PutawayStrategy save(PutawayStrategy strategy) {
        log.debug("Saving putaway strategy: {}", strategy.getStrategyName());
        // Simplified - would use proper upsert logic
        return strategy;
    }

    @Override
    public void delete(String strategyKey) {
        String sql = "DELETE FROM PUTAWAYSTRATEGY WHERE STRATEGYKEY = ?";
        jdbcTemplate.update(sql, strategyKey);
    }

    private static class PutawayStrategyRowMapper implements RowMapper<PutawayStrategy> {
        @Override
        public PutawayStrategy mapRow(ResultSet rs, int rowNum) throws SQLException {
            return PutawayStrategy.builder()
                    .strategyKey(rs.getString("STRATEGYKEY"))
                    .strategyName(rs.getString("STRATEGYNAME"))
                    .description(rs.getString("DESCRIPTION"))
                    .type(PutawayStrategyType.valueOf(rs.getString("STRATEGYTYPE")))
                    .active(rs.getBoolean("ACTIVE"))
                    .priority(rs.getInt("PRIORITY"))
                    .allowedZones(parseList(rs.getString("ALLOWEDZONES")))
                    .excludedZones(parseList(rs.getString("EXCLUDEDZONES")))
                    .defaultZone(rs.getString("DEFAULTZONE"))
                    .preferredLocationTypes(parseList(rs.getString("PREFERREDLOCATIONTYPES")))
                    .allowMixedSku(rs.getBoolean("ALLOWMIXEDSKU"))
                    .allowMixedLot(rs.getBoolean("ALLOWMIXEDLOT"))
                    .checkCapacity(rs.getBoolean("CHECKCAPACITY"))
                    .consolidate(rs.getBoolean("CONSOLIDATE"))
                    .enforceFifo(rs.getBoolean("ENFORCEFIFO"))
                    .checkExpiry(rs.getBoolean("CHECKEXPIRY"))
                    .minDaysToExpiry(rs.getInt("MINDAYSTOEXPIRY"))
                    .useVelocity(rs.getBoolean("USEVELOCITY"))
                    .velocityZoneMapping(rs.getString("VELOCITYZONEMAPPING"))
                    .forReturns(rs.getBoolean("FORRETURNS"))
                    .returnZone(rs.getString("RETURNZONE"))
                    .build();
        }

        private List<String> parseList(String value) {
            if (value == null || value.isEmpty()) {
                return List.of();
            }
            return Arrays.asList(value.split(","));
        }
    }
}
