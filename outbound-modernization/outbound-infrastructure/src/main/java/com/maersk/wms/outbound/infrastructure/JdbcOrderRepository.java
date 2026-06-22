package com.maersk.wms.outbound.infrastructure;

import com.maersk.wms.outbound.domain.Order;
import com.maersk.wms.outbound.domain.OrderDetail;
import com.maersk.wms.outbound.domain.OrderStatus;
import com.maersk.wms.outbound.domain.repository.OrderRepository;
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
 * JDBC implementation of OrderRepository.
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class JdbcOrderRepository implements OrderRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Order save(Order order) {
        if (order.getId() == null) {
            return insert(order);
        }
        return update(order);
    }

    @Override
    public Optional<Order> findById(Long id) {
        String sql = "SELECT * FROM ORDERS WHERE ORDERS_ID = ?";
        List<Order> results = jdbcTemplate.query(sql, new OrderRowMapper(), id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public Optional<Order> findByOrderNumber(String orderNumber) {
        String sql = "SELECT * FROM ORDERS WHERE ORDERSKEY = ?";
        List<Order> results = jdbcTemplate.query(sql, new OrderRowMapper(), orderNumber);

        if (results.isEmpty()) {
            return Optional.empty();
        }

        Order order = results.get(0);
        order.setDetails(findDetailsByOrderNumber(orderNumber));
        return Optional.of(order);
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        String sql = "SELECT * FROM ORDERS WHERE STATUS = ?";
        return jdbcTemplate.query(sql, new OrderRowMapper(), status.getCode());
    }

    @Override
    public List<Order> findByCustomerCode(String customerCode) {
        String sql = "SELECT * FROM ORDERS WHERE BILLTOADDRESSKEY = ?";
        return jdbcTemplate.query(sql, new OrderRowMapper(), customerCode);
    }

    @Override
    public void delete(Order order) {
        String sql = "DELETE FROM ORDERS WHERE ORDERS_ID = ?";
        jdbcTemplate.update(sql, order.getId());
    }

    private Order insert(Order order) {
        // Get next key
        String keyQuery = "EXEC nspg_GetKey 'ORDERS', 1";
        Long newId = jdbcTemplate.queryForObject(keyQuery, Long.class);
        order.setId(newId);

        String sql = """
            INSERT INTO ORDERS (ORDERS_ID, ORDERSKEY, EXTERNALORDERSKEY, STATUS, PRIORITY,
                BILLTOADDRESSKEY, SHIPTOADDRESSKEY, CARRIERCODE, SHIPMETHOD,
                SHIPTO_NAME, SHIPTO_ADDRESS1, SHIPTO_ADDRESS2, SHIPTO_CITY,
                SHIPTO_STATE, SHIPTO_ZIP, SHIPTO_COUNTRY, REQUIREDDATE,
                ADDWHO, ADDDATE)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE())
            """;

        jdbcTemplate.update(sql,
                order.getId(),
                order.getOrderNumber(),
                order.getExternalOrderNumber(),
                order.getStatus().getCode(),
                order.getPriority() != null ? order.getPriority().ordinal() : 2,
                order.getCustomerCode(),
                order.getShipToCode(),
                order.getCarrierCode(),
                order.getShipMethod(),
                order.getShipToName(),
                order.getShipToAddress1(),
                order.getShipToAddress2(),
                order.getShipToCity(),
                order.getShipToState(),
                order.getShipToZip(),
                order.getShipToCountry(),
                order.getRequiredDate(),
                order.getCreatedBy()
        );

        // Insert details
        for (OrderDetail detail : order.getDetails()) {
            insertDetail(order.getOrderNumber(), detail);
        }

        return order;
    }

    private Order update(Order order) {
        String sql = """
            UPDATE ORDERS SET
                STATUS = ?,
                PRIORITY = ?,
                CARRIERCODE = ?,
                SHIPMETHOD = ?,
                RELEASEDDATE = ?,
                RELEASEDBY = ?,
                EDITWHO = ?,
                EDITDATE = GETDATE()
            WHERE ORDERS_ID = ?
            """;

        jdbcTemplate.update(sql,
                order.getStatus().getCode(),
                order.getPriority() != null ? order.getPriority().ordinal() : 2,
                order.getCarrierCode(),
                order.getShipMethod(),
                order.getReleasedAt(),
                order.getReleasedBy(),
                order.getUpdatedBy(),
                order.getId()
        );

        return order;
    }

    private void insertDetail(String orderNumber, OrderDetail detail) {
        String keyQuery = "EXEC nspg_GetKey 'ORDERDETAIL', 1";
        Long newId = jdbcTemplate.queryForObject(keyQuery, Long.class);
        detail.setId(newId);

        String sql = """
            INSERT INTO ORDERDETAIL (ORDERDETAIL_ID, ORDERSKEY, ORDERLINENUMBER, SKU,
                ORIGINALQTY, QTYORDERED, QTYALLOCATED, QTYPICKED, QTYSHIPPED,
                STATUS, ADDWHO, ADDDATE)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE())
            """;

        jdbcTemplate.update(sql,
                detail.getId(),
                orderNumber,
                detail.getLineNumber(),
                detail.getSku(),
                detail.getOrderedQty(),
                detail.getOrderedQty(),
                detail.getAllocatedQty(),
                detail.getPickedQty(),
                detail.getShippedQty(),
                detail.getStatus().getCode(),
                detail.getCreatedBy()
        );
    }

    private List<OrderDetail> findDetailsByOrderNumber(String orderNumber) {
        String sql = "SELECT * FROM ORDERDETAIL WHERE ORDERSKEY = ? ORDER BY ORDERLINENUMBER";
        return jdbcTemplate.query(sql, new OrderDetailRowMapper(), orderNumber);
    }

    private static class OrderRowMapper implements RowMapper<Order> {
        @Override
        public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
            Order order = new Order();
            order.setId(rs.getLong("ORDERS_ID"));
            order.setOrderNumber(rs.getString("ORDERSKEY"));
            order.setExternalOrderNumber(rs.getString("EXTERNALORDERSKEY"));
            order.setStatus(OrderStatus.fromCode(rs.getString("STATUS")));
            order.setCustomerCode(rs.getString("BILLTOADDRESSKEY"));
            order.setShipToCode(rs.getString("SHIPTOADDRESSKEY"));
            order.setCarrierCode(rs.getString("CARRIERCODE"));
            order.setShipMethod(rs.getString("SHIPMETHOD"));
            order.setShipToName(rs.getString("SHIPTO_NAME"));
            order.setShipToAddress1(rs.getString("SHIPTO_ADDRESS1"));
            order.setShipToAddress2(rs.getString("SHIPTO_ADDRESS2"));
            order.setShipToCity(rs.getString("SHIPTO_CITY"));
            order.setShipToState(rs.getString("SHIPTO_STATE"));
            order.setShipToZip(rs.getString("SHIPTO_ZIP"));
            order.setShipToCountry(rs.getString("SHIPTO_COUNTRY"));
            if (rs.getTimestamp("REQUIREDDATE") != null) {
                order.setRequiredDate(rs.getTimestamp("REQUIREDDATE").toLocalDateTime());
            }
            order.setCreatedBy(rs.getString("ADDWHO"));
            if (rs.getTimestamp("ADDDATE") != null) {
                order.setCreatedAt(rs.getTimestamp("ADDDATE").toLocalDateTime());
            }
            return order;
        }
    }

    private static class OrderDetailRowMapper implements RowMapper<OrderDetail> {
        @Override
        public OrderDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
            OrderDetail detail = new OrderDetail();
            detail.setId(rs.getLong("ORDERDETAIL_ID"));
            detail.setLineNumber(rs.getString("ORDERLINENUMBER"));
            detail.setSku(rs.getString("SKU"));
            detail.setOrderedQty(rs.getBigDecimal("QTYORDERED"));
            detail.setAllocatedQty(rs.getBigDecimal("QTYALLOCATED"));
            detail.setPickedQty(rs.getBigDecimal("QTYPICKED"));
            detail.setShippedQty(rs.getBigDecimal("QTYSHIPPED"));
            detail.setStatus(com.maersk.wms.outbound.domain.OrderDetailStatus.fromCode(rs.getString("STATUS")));
            return detail;
        }
    }
}
