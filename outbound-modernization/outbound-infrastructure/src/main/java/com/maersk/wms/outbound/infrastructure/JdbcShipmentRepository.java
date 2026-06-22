package com.maersk.wms.outbound.infrastructure;

import com.maersk.wms.outbound.domain.Shipment;
import com.maersk.wms.outbound.domain.ShipmentStatus;
import com.maersk.wms.outbound.domain.ShipmentType;
import com.maersk.wms.outbound.domain.repository.ShipmentRepository;
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
 * JDBC implementation of ShipmentRepository.
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class JdbcShipmentRepository implements ShipmentRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_BASE = """
            SELECT SHIPMENTKEY, EXTERNALSHIPMENTKEY, STORERKEY, STATUS, SHIPMENTTYPE,
                   CARRIERCODE, CARRIERNAME, SERVICELEVEL, TRACKINGNUMBER, PRONUMBER,
                   SHIPTO_NAME, SHIPTO_ADDRESS1, SHIPTO_ADDRESS2, SHIPTO_CITY,
                   SHIPTO_STATE, SHIPTO_ZIP, SHIPTO_COUNTRY,
                   LOADKEY, DOOR, TRAILERNUMBER, SEALNUMBER,
                   EXPECTEDSHIPDATE, ACTUALSHIPDATE, DELIVERYDATE,
                   TOTALCARTONS, TOTALPALLETS, TOTALWEIGHT, TOTALVOLUME, FREIGHTCHARGE, CURRENCY,
                   MANIFESTKEY, BOLNUMBER,
                   ADDWHO, ADDDATE, EDITWHO, EDITDATE
            FROM SHIPMENT
            """;

    @Override
    public Optional<Shipment> findByKey(String shipmentKey) {
        String sql = SELECT_BASE + " WHERE SHIPMENTKEY = ?";
        List<Shipment> results = jdbcTemplate.query(sql, new ShipmentRowMapper(), shipmentKey);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public Optional<Shipment> findByTrackingNumber(String trackingNumber) {
        String sql = SELECT_BASE + " WHERE TRACKINGNUMBER = ?";
        List<Shipment> results = jdbcTemplate.query(sql, new ShipmentRowMapper(), trackingNumber);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<Shipment> findByStatus(ShipmentStatus status) {
        String sql = SELECT_BASE + " WHERE STATUS = ?";
        return jdbcTemplate.query(sql, new ShipmentRowMapper(), status.getCode());
    }

    @Override
    public List<Shipment> findByStorerKey(String storerKey) {
        String sql = SELECT_BASE + " WHERE STORERKEY = ?";
        return jdbcTemplate.query(sql, new ShipmentRowMapper(), storerKey);
    }

    @Override
    public List<Shipment> findByOrderKey(String orderKey) {
        String sql = SELECT_BASE + " WHERE SHIPMENTKEY IN (SELECT DISTINCT SHIPMENTKEY FROM SHIPMENTDETAIL WHERE ORDERSKEY = ?)";
        return jdbcTemplate.query(sql, new ShipmentRowMapper(), orderKey);
    }

    @Override
    public List<Shipment> findByLoadKey(String loadKey) {
        String sql = SELECT_BASE + " WHERE LOADKEY = ?";
        return jdbcTemplate.query(sql, new ShipmentRowMapper(), loadKey);
    }

    @Override
    public List<Shipment> findByShipDateRange(LocalDateTime fromDate, LocalDateTime toDate) {
        String sql = SELECT_BASE + " WHERE ACTUALSHIPDATE BETWEEN ? AND ?";
        return jdbcTemplate.query(sql, new ShipmentRowMapper(), fromDate, toDate);
    }

    @Override
    public List<Shipment> findReadyToShip(String storerKey) {
        String sql = SELECT_BASE + " WHERE STORERKEY = ? AND STATUS IN (?, ?)";
        return jdbcTemplate.query(sql, new ShipmentRowMapper(),
                storerKey,
                ShipmentStatus.MANIFESTED.getCode(),
                ShipmentStatus.LOADED.getCode());
    }

    @Override
    public Shipment save(Shipment shipment) {
        if (exists(shipment.getShipmentKey())) {
            return update(shipment);
        }
        return insert(shipment);
    }

    @Override
    public void delete(String shipmentKey) {
        // Delete details first
        String deleteDetailsSql = "DELETE FROM SHIPMENTDETAIL WHERE SHIPMENTKEY = ?";
        jdbcTemplate.update(deleteDetailsSql, shipmentKey);

        // Delete shipment
        String deleteShipmentSql = "DELETE FROM SHIPMENT WHERE SHIPMENTKEY = ?";
        jdbcTemplate.update(deleteShipmentSql, shipmentKey);
    }

    @Override
    public String generateShipmentKey() {
        String keyQuery = "EXEC nspg_GetKey 'SHIPMENT', 1";
        String key = jdbcTemplate.queryForObject(keyQuery, String.class);
        return "SH" + key;
    }

    private boolean exists(String shipmentKey) {
        if (shipmentKey == null) {
            return false;
        }
        String sql = "SELECT COUNT(*) FROM SHIPMENT WHERE SHIPMENTKEY = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, shipmentKey);
        return count != null && count > 0;
    }

    private Shipment insert(Shipment shipment) {
        String shipmentKey = shipment.getShipmentKey();
        if (shipmentKey == null) {
            shipmentKey = generateShipmentKey();
            shipment.setShipmentKey(shipmentKey);
        }

        String sql = """
            INSERT INTO SHIPMENT (SHIPMENTKEY, EXTERNALSHIPMENTKEY, STORERKEY, STATUS, SHIPMENTTYPE,
                CARRIERCODE, CARRIERNAME, SERVICELEVEL, TRACKINGNUMBER, PRONUMBER,
                SHIPTO_NAME, SHIPTO_ADDRESS1, SHIPTO_ADDRESS2, SHIPTO_CITY,
                SHIPTO_STATE, SHIPTO_ZIP, SHIPTO_COUNTRY,
                LOADKEY, DOOR, TRAILERNUMBER, SEALNUMBER,
                EXPECTEDSHIPDATE, TOTALCARTONS, TOTALPALLETS, TOTALWEIGHT, TOTALVOLUME,
                FREIGHTCHARGE, CURRENCY, MANIFESTKEY, BOLNUMBER,
                ADDWHO, ADDDATE)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE())
            """;

        jdbcTemplate.update(sql,
                shipmentKey,
                shipment.getExternalShipmentKey(),
                shipment.getStorerKey(),
                shipment.getStatus() != null ? shipment.getStatus().getCode() : ShipmentStatus.NEW.getCode(),
                shipment.getType() != null ? shipment.getType().name() : null,
                shipment.getCarrierCode(),
                shipment.getCarrierName(),
                shipment.getServiceLevel(),
                shipment.getTrackingNumber(),
                shipment.getProNumber(),
                shipment.getShipToName(),
                shipment.getShipToAddress1(),
                shipment.getShipToAddress2(),
                shipment.getShipToCity(),
                shipment.getShipToState(),
                shipment.getShipToZip(),
                shipment.getShipToCountry(),
                shipment.getLoadKey(),
                shipment.getDoor(),
                shipment.getTrailerNumber(),
                shipment.getSealNumber(),
                shipment.getExpectedShipDate(),
                shipment.getTotalCartons(),
                shipment.getTotalPallets(),
                shipment.getTotalWeight(),
                shipment.getTotalVolume(),
                shipment.getFreightCharge(),
                shipment.getCurrency(),
                shipment.getManifestKey(),
                shipment.getBolNumber(),
                shipment.getAddWho()
        );

        return shipment;
    }

    private Shipment update(Shipment shipment) {
        String sql = """
            UPDATE SHIPMENT SET
                STATUS = ?,
                CARRIERCODE = ?,
                CARRIERNAME = ?,
                SERVICELEVEL = ?,
                TRACKINGNUMBER = ?,
                PRONUMBER = ?,
                LOADKEY = ?,
                DOOR = ?,
                TRAILERNUMBER = ?,
                SEALNUMBER = ?,
                ACTUALSHIPDATE = ?,
                DELIVERYDATE = ?,
                TOTALCARTONS = ?,
                TOTALPALLETS = ?,
                TOTALWEIGHT = ?,
                TOTALVOLUME = ?,
                FREIGHTCHARGE = ?,
                MANIFESTKEY = ?,
                BOLNUMBER = ?,
                EDITWHO = ?,
                EDITDATE = GETDATE()
            WHERE SHIPMENTKEY = ?
            """;

        jdbcTemplate.update(sql,
                shipment.getStatus() != null ? shipment.getStatus().getCode() : null,
                shipment.getCarrierCode(),
                shipment.getCarrierName(),
                shipment.getServiceLevel(),
                shipment.getTrackingNumber(),
                shipment.getProNumber(),
                shipment.getLoadKey(),
                shipment.getDoor(),
                shipment.getTrailerNumber(),
                shipment.getSealNumber(),
                shipment.getActualShipDate(),
                shipment.getDeliveryDate(),
                shipment.getTotalCartons(),
                shipment.getTotalPallets(),
                shipment.getTotalWeight(),
                shipment.getTotalVolume(),
                shipment.getFreightCharge(),
                shipment.getManifestKey(),
                shipment.getBolNumber(),
                shipment.getEditWho(),
                shipment.getShipmentKey()
        );

        return shipment;
    }

    private static class ShipmentRowMapper implements RowMapper<Shipment> {
        @Override
        public Shipment mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Shipment.builder()
                    .shipmentKey(rs.getString("SHIPMENTKEY"))
                    .externalShipmentKey(rs.getString("EXTERNALSHIPMENTKEY"))
                    .storerKey(rs.getString("STORERKEY"))
                    .status(ShipmentStatus.fromCode(rs.getString("STATUS")))
                    .type(parseShipmentType(rs.getString("SHIPMENTTYPE")))
                    .carrierCode(rs.getString("CARRIERCODE"))
                    .carrierName(rs.getString("CARRIERNAME"))
                    .serviceLevel(rs.getString("SERVICELEVEL"))
                    .trackingNumber(rs.getString("TRACKINGNUMBER"))
                    .proNumber(rs.getString("PRONUMBER"))
                    .shipToName(rs.getString("SHIPTO_NAME"))
                    .shipToAddress1(rs.getString("SHIPTO_ADDRESS1"))
                    .shipToAddress2(rs.getString("SHIPTO_ADDRESS2"))
                    .shipToCity(rs.getString("SHIPTO_CITY"))
                    .shipToState(rs.getString("SHIPTO_STATE"))
                    .shipToZip(rs.getString("SHIPTO_ZIP"))
                    .shipToCountry(rs.getString("SHIPTO_COUNTRY"))
                    .loadKey(rs.getString("LOADKEY"))
                    .door(rs.getString("DOOR"))
                    .trailerNumber(rs.getString("TRAILERNUMBER"))
                    .sealNumber(rs.getString("SEALNUMBER"))
                    .expectedShipDate(rs.getTimestamp("EXPECTEDSHIPDATE") != null ? rs.getTimestamp("EXPECTEDSHIPDATE").toLocalDateTime() : null)
                    .actualShipDate(rs.getTimestamp("ACTUALSHIPDATE") != null ? rs.getTimestamp("ACTUALSHIPDATE").toLocalDateTime() : null)
                    .deliveryDate(rs.getTimestamp("DELIVERYDATE") != null ? rs.getTimestamp("DELIVERYDATE").toLocalDateTime() : null)
                    .totalCartons(rs.getInt("TOTALCARTONS"))
                    .totalPallets(rs.getInt("TOTALPALLETS"))
                    .totalWeight(rs.getBigDecimal("TOTALWEIGHT"))
                    .totalVolume(rs.getBigDecimal("TOTALVOLUME"))
                    .freightCharge(rs.getBigDecimal("FREIGHTCHARGE"))
                    .currency(rs.getString("CURRENCY"))
                    .manifestKey(rs.getString("MANIFESTKEY"))
                    .bolNumber(rs.getString("BOLNUMBER"))
                    .addWho(rs.getString("ADDWHO"))
                    .addDate(rs.getTimestamp("ADDDATE") != null ? rs.getTimestamp("ADDDATE").toLocalDateTime() : null)
                    .editWho(rs.getString("EDITWHO"))
                    .editDate(rs.getTimestamp("EDITDATE") != null ? rs.getTimestamp("EDITDATE").toLocalDateTime() : null)
                    .build();
        }

        private static ShipmentType parseShipmentType(String value) {
            if (value == null) return null;
            try {
                return ShipmentType.valueOf(value);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }
}
