package com.maersk.wms.outbound.infrastructure;

import com.maersk.wms.outbound.domain.Shipment;
import com.maersk.wms.outbound.domain.ShipmentStatus;
import com.maersk.wms.outbound.domain.repository.ShipmentRepository;
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
 * JDBC implementation of ShipmentRepository.
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class JdbcShipmentRepository implements ShipmentRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Shipment save(Shipment shipment) {
        if (shipment.getId() == null) {
            return insert(shipment);
        }
        return update(shipment);
    }

    @Override
    public Optional<Shipment> findById(Long id) {
        String sql = "SELECT * FROM SHIPMENT WHERE SHIPMENT_ID = ?";
        List<Shipment> results = jdbcTemplate.query(sql, new ShipmentRowMapper(), id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public Optional<Shipment> findByShipmentId(String shipmentId) {
        String sql = "SELECT * FROM SHIPMENT WHERE SHIPMENTKEY = ?";
        List<Shipment> results = jdbcTemplate.query(sql, new ShipmentRowMapper(), shipmentId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<Shipment> findByOrderNumber(String orderNumber) {
        String sql = "SELECT * FROM SHIPMENT WHERE ORDERSKEY = ?";
        return jdbcTemplate.query(sql, new ShipmentRowMapper(), orderNumber);
    }

    @Override
    public List<Shipment> findByStatus(ShipmentStatus status) {
        String sql = "SELECT * FROM SHIPMENT WHERE STATUS = ?";
        return jdbcTemplate.query(sql, new ShipmentRowMapper(), status.getCode());
    }

    @Override
    public void delete(Shipment shipment) {
        String sql = "DELETE FROM SHIPMENT WHERE SHIPMENT_ID = ?";
        jdbcTemplate.update(sql, shipment.getId());
    }

    private Shipment insert(Shipment shipment) {
        // Get next key
        String keyQuery = "EXEC nspg_GetKey 'SHIPMENT', 1";
        Long newId = jdbcTemplate.queryForObject(keyQuery, Long.class);
        shipment.setId(newId);
        shipment.setShipmentId("SH" + String.format("%010d", newId));

        String sql = """
            INSERT INTO SHIPMENT (SHIPMENT_ID, SHIPMENTKEY, ORDERSKEY, CARRIERCODE,
                SHIPMETHOD, STATUS, SHIPTO_NAME, SHIPTO_ADDRESS1, SHIPTO_ADDRESS2,
                SHIPTO_CITY, SHIPTO_STATE, SHIPTO_ZIP, SHIPTO_COUNTRY,
                ADDWHO, ADDDATE)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE())
            """;

        jdbcTemplate.update(sql,
                shipment.getId(),
                shipment.getShipmentId(),
                shipment.getOrderNumber(),
                shipment.getCarrierCode(),
                shipment.getShipMethod(),
                shipment.getStatus().getCode(),
                shipment.getShipToName(),
                shipment.getShipToAddress1(),
                shipment.getShipToAddress2(),
                shipment.getShipToCity(),
                shipment.getShipToState(),
                shipment.getShipToZip(),
                shipment.getShipToCountry(),
                shipment.getCreatedBy()
        );

        return shipment;
    }

    private Shipment update(Shipment shipment) {
        String sql = """
            UPDATE SHIPMENT SET
                STATUS = ?,
                CARRIERCODE = ?,
                TRACKINGNUMBER = ?,
                FREIGHTCHARGE = ?,
                TOTALWEIGHT = ?,
                SHIPPEDDATE = ?,
                SHIPPEDBY = ?,
                MANIFESTEDDATE = ?,
                EDITWHO = ?,
                EDITDATE = GETDATE()
            WHERE SHIPMENT_ID = ?
            """;

        jdbcTemplate.update(sql,
                shipment.getStatus().getCode(),
                shipment.getCarrierCode(),
                shipment.getTrackingNumber(),
                shipment.getFreightCharge(),
                shipment.getTotalWeight(),
                shipment.getShippedAt(),
                shipment.getShippedBy(),
                shipment.getManifestedAt(),
                shipment.getUpdatedBy(),
                shipment.getId()
        );

        return shipment;
    }

    private static class ShipmentRowMapper implements RowMapper<Shipment> {
        @Override
        public Shipment mapRow(ResultSet rs, int rowNum) throws SQLException {
            Shipment shipment = new Shipment();
            shipment.setId(rs.getLong("SHIPMENT_ID"));
            shipment.setShipmentId(rs.getString("SHIPMENTKEY"));
            shipment.setOrderNumber(rs.getString("ORDERSKEY"));
            shipment.setCarrierCode(rs.getString("CARRIERCODE"));
            shipment.setShipMethod(rs.getString("SHIPMETHOD"));
            shipment.setStatus(ShipmentStatus.fromCode(rs.getString("STATUS")));
            shipment.setShipToName(rs.getString("SHIPTO_NAME"));
            shipment.setShipToAddress1(rs.getString("SHIPTO_ADDRESS1"));
            shipment.setShipToAddress2(rs.getString("SHIPTO_ADDRESS2"));
            shipment.setShipToCity(rs.getString("SHIPTO_CITY"));
            shipment.setShipToState(rs.getString("SHIPTO_STATE"));
            shipment.setShipToZip(rs.getString("SHIPTO_ZIP"));
            shipment.setShipToCountry(rs.getString("SHIPTO_COUNTRY"));
            shipment.setTrackingNumber(rs.getString("TRACKINGNUMBER"));
            shipment.setFreightCharge(rs.getBigDecimal("FREIGHTCHARGE"));
            shipment.setTotalWeight(rs.getBigDecimal("TOTALWEIGHT"));
            shipment.setCreatedBy(rs.getString("ADDWHO"));
            if (rs.getTimestamp("ADDDATE") != null) {
                shipment.setCreatedAt(rs.getTimestamp("ADDDATE").toLocalDateTime());
            }
            if (rs.getTimestamp("SHIPPEDDATE") != null) {
                shipment.setShippedAt(rs.getTimestamp("SHIPPEDDATE").toLocalDateTime());
            }
            shipment.setShippedBy(rs.getString("SHIPPEDBY"));
            return shipment;
        }
    }
}
