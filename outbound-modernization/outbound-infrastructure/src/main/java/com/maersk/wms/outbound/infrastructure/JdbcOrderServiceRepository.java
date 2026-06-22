package com.maersk.wms.outbound.infrastructure;

import com.maersk.wms.outbound.domain.order_service.model.Order;
import com.maersk.wms.outbound.domain.order_service.model.OrderDetail;
import com.maersk.wms.outbound.domain.order_service.model.OrderDetailStatus;
import com.maersk.wms.outbound.domain.order_service.model.OrderPriority;
import com.maersk.wms.outbound.domain.order_service.model.OrderStatus;
import com.maersk.wms.outbound.domain.order_service.model.OrderType;
import com.maersk.wms.outbound.domain.order_service.repository.OrderRepository;
import com.maersk.wms.outbound.shared.kernel.identifiers.OrderKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.SkuKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.StorerKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.WaveKey;
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
 * JDBC implementation of OrderRepository for order_service domain.
 * Uses value objects (OrderKey, StorerKey, etc.).
 */
@Repository("orderServiceRepository")
@RequiredArgsConstructor
@Slf4j
public class JdbcOrderServiceRepository implements OrderRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_BASE = """
            SELECT o.ORDERSKEY, o.EXTERNALORDERSKEY, o.STORERKEY, o.WAVEKEY,
                   o.ORDERTYPE, o.STATUS, o.PRIORITY,
                   o.CONSIGNEEKEY, o.SHIPTO_NAME, o.SHIPTO_ADDRESS1, o.SHIPTO_ADDRESS2,
                   o.SHIPTO_CITY, o.SHIPTO_STATE, o.SHIPTO_ZIP, o.SHIPTO_COUNTRY,
                   o.CARRIERCODE, o.SERVICETYPE, o.REQUIREDDATE, o.DELIVERYDATE,
                   o.TOTALQTYORDERED, o.TOTALQTYALLOCATED, o.TOTALQTYPICKED, o.TOTALQTYSHIPPED,
                   o.ADDWHO, o.ADDDATE, o.EDITWHO, o.EDITDATE
            FROM ORDERS o
            """;

    @Override
    public Order save(Order order) {
        if (exists(order.getOrderKey())) {
            return update(order);
        }
        return insert(order);
    }

    @Override
    public Optional<Order> findByKey(OrderKey orderKey) {
        String sql = SELECT_BASE + " WHERE o.ORDERSKEY = ?";
        List<Order> results = jdbcTemplate.query(sql, new OrderRowMapper(), orderKey.getValue());

        if (results.isEmpty()) {
            return Optional.empty();
        }

        Order order = results.get(0);
        order.setDetails(findDetailsByOrderKey(orderKey));
        return Optional.of(order);
    }

    @Override
    public Optional<Order> findByExternalKey(StorerKey storerKey, String externalOrderKey) {
        String sql = SELECT_BASE + " WHERE o.STORERKEY = ? AND o.EXTERNALORDERSKEY = ?";
        List<Order> results = jdbcTemplate.query(sql, new OrderRowMapper(),
                storerKey.getValue(), externalOrderKey);

        if (results.isEmpty()) {
            return Optional.empty();
        }

        Order order = results.get(0);
        order.setDetails(findDetailsByOrderKey(order.getOrderKey()));
        return Optional.of(order);
    }

    @Override
    public List<Order> findByStorerAndStatus(StorerKey storerKey, OrderStatus status) {
        String sql = SELECT_BASE + " WHERE o.STORERKEY = ? AND o.STATUS = ?";
        List<Order> orders = jdbcTemplate.query(sql, new OrderRowMapper(),
                storerKey.getValue(), status.getCode());

        // Load details for each order
        orders.forEach(order -> order.setDetails(findDetailsByOrderKey(order.getOrderKey())));
        return orders;
    }

    @Override
    public List<Order> findByWave(WaveKey waveKey) {
        String sql = SELECT_BASE + " WHERE o.WAVEKEY = ?";
        List<Order> orders = jdbcTemplate.query(sql, new OrderRowMapper(), waveKey.getValue());

        // Load details for each order
        orders.forEach(order -> order.setDetails(findDetailsByOrderKey(order.getOrderKey())));
        return orders;
    }

    @Override
    public List<Order> findReadyForAllocation(StorerKey storerKey) {
        String sql = SELECT_BASE + " WHERE o.STORERKEY = ? AND o.STATUS IN (?, ?)";
        List<Order> orders = jdbcTemplate.query(sql, new OrderRowMapper(),
                storerKey.getValue(),
                OrderStatus.RELEASED.getCode(),
                OrderStatus.PARTIALLY_ALLOCATED.getCode());

        // Load details for each order
        orders.forEach(order -> order.setDetails(findDetailsByOrderKey(order.getOrderKey())));
        return orders;
    }

    @Override
    public void delete(OrderKey orderKey) {
        // Delete details first (foreign key constraint)
        String deleteDetailsSql = "DELETE FROM ORDERDETAIL WHERE ORDERSKEY = ?";
        jdbcTemplate.update(deleteDetailsSql, orderKey.getValue());

        // Delete order
        String deleteOrderSql = "DELETE FROM ORDERS WHERE ORDERSKEY = ?";
        jdbcTemplate.update(deleteOrderSql, orderKey.getValue());
    }

    private boolean exists(OrderKey orderKey) {
        if (orderKey == null) {
            return false;
        }
        String sql = "SELECT COUNT(*) FROM ORDERS WHERE ORDERSKEY = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, orderKey.getValue());
        return count != null && count > 0;
    }

    private Order insert(Order order) {
        // Generate key if not present
        OrderKey orderKey = order.getOrderKey();
        if (orderKey == null) {
            String keyQuery = "EXEC nspg_GetKey 'ORDERS', 1";
            String newKey = jdbcTemplate.queryForObject(keyQuery, String.class);
            orderKey = OrderKey.of(newKey);
            order.setOrderKey(orderKey);
        }

        String sql = """
            INSERT INTO ORDERS (ORDERSKEY, EXTERNALORDERSKEY, STORERKEY, WAVEKEY,
                ORDERTYPE, STATUS, PRIORITY,
                CONSIGNEEKEY, SHIPTO_NAME, SHIPTO_ADDRESS1, SHIPTO_ADDRESS2,
                SHIPTO_CITY, SHIPTO_STATE, SHIPTO_ZIP, SHIPTO_COUNTRY,
                CARRIERCODE, SERVICETYPE, REQUIREDDATE, DELIVERYDATE,
                TOTALQTYORDERED, TOTALQTYALLOCATED, TOTALQTYPICKED, TOTALQTYSHIPPED,
                ADDWHO, ADDDATE)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE())
            """;

        jdbcTemplate.update(sql,
                orderKey.getValue(),
                order.getExternalOrderKey(),
                order.getStorerKey() != null ? order.getStorerKey().getValue() : null,
                order.getWaveKey() != null ? order.getWaveKey().getValue() : null,
                order.getOrderType() != null ? order.getOrderType().name() : null,
                order.getStatus() != null ? order.getStatus().getCode() : OrderStatus.OPEN.getCode(),
                order.getPriority() != null ? order.getPriority().ordinal() : OrderPriority.NORMAL.ordinal(),
                order.getConsigneeKey(),
                order.getShipToName(),
                order.getShipToAddress1(),
                order.getShipToAddress2(),
                order.getShipToCity(),
                order.getShipToState(),
                order.getShipToZip(),
                order.getShipToCountry(),
                order.getCarrierCode(),
                order.getServiceType(),
                order.getRequestedShipDate(),
                order.getDeliveryDate(),
                order.getTotalQtyOrdered(),
                order.getTotalQtyAllocated(),
                order.getTotalQtyPicked(),
                order.getTotalQtyShipped(),
                order.getAddWho()
        );

        // Insert details
        if (order.getDetails() != null) {
            for (OrderDetail detail : order.getDetails()) {
                insertDetail(orderKey, detail);
            }
        }

        return order;
    }

    private Order update(Order order) {
        String sql = """
            UPDATE ORDERS SET
                EXTERNALORDERSKEY = ?,
                WAVEKEY = ?,
                STATUS = ?,
                PRIORITY = ?,
                CARRIERCODE = ?,
                SERVICETYPE = ?,
                TOTALQTYORDERED = ?,
                TOTALQTYALLOCATED = ?,
                TOTALQTYPICKED = ?,
                TOTALQTYSHIPPED = ?,
                EDITWHO = ?,
                EDITDATE = GETDATE()
            WHERE ORDERSKEY = ?
            """;

        jdbcTemplate.update(sql,
                order.getExternalOrderKey(),
                order.getWaveKey() != null ? order.getWaveKey().getValue() : null,
                order.getStatus() != null ? order.getStatus().getCode() : null,
                order.getPriority() != null ? order.getPriority().ordinal() : null,
                order.getCarrierCode(),
                order.getServiceType(),
                order.getTotalQtyOrdered(),
                order.getTotalQtyAllocated(),
                order.getTotalQtyPicked(),
                order.getTotalQtyShipped(),
                order.getEditWho(),
                order.getOrderKey().getValue()
        );

        return order;
    }

    private void insertDetail(OrderKey orderKey, OrderDetail detail) {
        String sql = """
            INSERT INTO ORDERDETAIL (ORDERSKEY, ORDERLINENUMBER, STORERKEY, SKU,
                QTYORDERED, QTYALLOCATED, QTYPICKED, QTYSHIPPED,
                STATUS, PACKKEY, UOM,
                LOTTABLE01, LOTTABLE02, LOTTABLE03, LOTTABLE04, LOTTABLE05,
                ADDWHO, ADDDATE)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE())
            """;

        jdbcTemplate.update(sql,
                orderKey.getValue(),
                detail.getLineNumber(),
                detail.getSku() != null ? detail.getSku().getStorerKey() : null,
                detail.getSku() != null ? detail.getSku().getSku() : null,
                detail.getQtyOrdered(),
                detail.getQtyAllocated(),
                detail.getQtyPicked(),
                detail.getQtyShipped(),
                detail.getStatus() != null ? detail.getStatus().getCode() : OrderDetailStatus.OPEN.getCode(),
                detail.getPackKey(),
                detail.getUom(),
                detail.getLottable01(),
                detail.getLottable02(),
                detail.getLottable03(),
                detail.getLottable04(),
                detail.getLottable05(),
                detail.getAddWho()
        );
    }

    private List<OrderDetail> findDetailsByOrderKey(OrderKey orderKey) {
        String sql = """
            SELECT ORDERSKEY, ORDERLINENUMBER, STORERKEY, SKU,
                   QTYORDERED, QTYALLOCATED, QTYPICKED, QTYSHIPPED,
                   STATUS, PACKKEY, UOM,
                   LOTTABLE01, LOTTABLE02, LOTTABLE03, LOTTABLE04, LOTTABLE05,
                   ADDWHO, ADDDATE, EDITWHO, EDITDATE
            FROM ORDERDETAIL
            WHERE ORDERSKEY = ?
            ORDER BY ORDERLINENUMBER
            """;
        return jdbcTemplate.query(sql, new OrderDetailRowMapper(), orderKey.getValue());
    }

    private static class OrderRowMapper implements RowMapper<Order> {
        @Override
        public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Order.builder()
                    .orderKey(OrderKey.of(rs.getString("ORDERSKEY")))
                    .externalOrderKey(rs.getString("EXTERNALORDERSKEY"))
                    .storerKey(rs.getString("STORERKEY") != null ? StorerKey.of(rs.getString("STORERKEY")) : null)
                    .waveKey(rs.getString("WAVEKEY") != null ? WaveKey.of(rs.getString("WAVEKEY")) : null)
                    .orderType(parseOrderType(rs.getString("ORDERTYPE")))
                    .status(OrderStatus.fromCode(rs.getString("STATUS")))
                    .priority(parseOrderPriority(rs.getInt("PRIORITY")))
                    .consigneeKey(rs.getString("CONSIGNEEKEY"))
                    .shipToName(rs.getString("SHIPTO_NAME"))
                    .shipToAddress1(rs.getString("SHIPTO_ADDRESS1"))
                    .shipToAddress2(rs.getString("SHIPTO_ADDRESS2"))
                    .shipToCity(rs.getString("SHIPTO_CITY"))
                    .shipToState(rs.getString("SHIPTO_STATE"))
                    .shipToZip(rs.getString("SHIPTO_ZIP"))
                    .shipToCountry(rs.getString("SHIPTO_COUNTRY"))
                    .carrierCode(rs.getString("CARRIERCODE"))
                    .serviceType(rs.getString("SERVICETYPE"))
                    .requestedShipDate(rs.getDate("REQUIREDDATE") != null ? rs.getDate("REQUIREDDATE").toLocalDate() : null)
                    .deliveryDate(rs.getDate("DELIVERYDATE") != null ? rs.getDate("DELIVERYDATE").toLocalDate() : null)
                    .totalQtyOrdered(rs.getBigDecimal("TOTALQTYORDERED"))
                    .totalQtyAllocated(rs.getBigDecimal("TOTALQTYALLOCATED"))
                    .totalQtyPicked(rs.getBigDecimal("TOTALQTYPICKED"))
                    .totalQtyShipped(rs.getBigDecimal("TOTALQTYSHIPPED"))
                    .addWho(rs.getString("ADDWHO"))
                    .addDate(rs.getTimestamp("ADDDATE") != null ? rs.getTimestamp("ADDDATE").toLocalDateTime() : null)
                    .editWho(rs.getString("EDITWHO"))
                    .editDate(rs.getTimestamp("EDITDATE") != null ? rs.getTimestamp("EDITDATE").toLocalDateTime() : null)
                    .build();
        }

        private static OrderType parseOrderType(String value) {
            if (value == null) return null;
            try {
                return OrderType.valueOf(value);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        private static OrderPriority parseOrderPriority(int value) {
            OrderPriority[] priorities = OrderPriority.values();
            if (value >= 0 && value < priorities.length) {
                return priorities[value];
            }
            return OrderPriority.NORMAL;
        }
    }

    private static class OrderDetailRowMapper implements RowMapper<OrderDetail> {
        @Override
        public OrderDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
            String storerKey = rs.getString("STORERKEY");
            String sku = rs.getString("SKU");

            return OrderDetail.builder()
                    .orderKey(OrderKey.of(rs.getString("ORDERSKEY")))
                    .lineNumber(rs.getInt("ORDERLINENUMBER"))
                    .sku(storerKey != null && sku != null ? SkuKey.of(storerKey, sku) : null)
                    .qtyOrdered(rs.getBigDecimal("QTYORDERED"))
                    .qtyAllocated(rs.getBigDecimal("QTYALLOCATED"))
                    .qtyPicked(rs.getBigDecimal("QTYPICKED"))
                    .qtyShipped(rs.getBigDecimal("QTYSHIPPED"))
                    .status(OrderDetailStatus.fromCode(rs.getString("STATUS")))
                    .packKey(rs.getString("PACKKEY"))
                    .uom(rs.getString("UOM"))
                    .lottable01(rs.getString("LOTTABLE01"))
                    .lottable02(rs.getString("LOTTABLE02"))
                    .lottable03(rs.getString("LOTTABLE03"))
                    .lottable04(rs.getString("LOTTABLE04"))
                    .lottable05(rs.getString("LOTTABLE05"))
                    .addWho(rs.getString("ADDWHO"))
                    .addDate(rs.getTimestamp("ADDDATE") != null ? rs.getTimestamp("ADDDATE").toLocalDateTime() : null)
                    .editWho(rs.getString("EDITWHO"))
                    .editDate(rs.getTimestamp("EDITDATE") != null ? rs.getTimestamp("EDITDATE").toLocalDateTime() : null)
                    .build();
        }
    }
}
