package com.maersk.wms.outbound.infrastructure;

import com.maersk.wms.outbound.domain.Order;
import com.maersk.wms.outbound.domain.OrderDetail;
import com.maersk.wms.outbound.domain.OrderDetailStatus;
import com.maersk.wms.outbound.domain.OrderPriority;
import com.maersk.wms.outbound.domain.OrderStatus;
import com.maersk.wms.outbound.domain.repository.OrderRepository;
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
 * JDBC implementation of OrderRepository for the service layer.
 * Uses primitive types (String keys).
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class JdbcOrderRepository implements OrderRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_BASE = """
            SELECT o.ORDERSKEY, o.EXTERNALORDERSKEY, o.STORERKEY, o.ORDERTYPE, o.ORDERGROUP,
                   o.STATUS, o.PRIORITY,
                   o.CONSIGNEEKEY, o.CONSIGNEENAME, o.SHIPTO_ADDRESS1, o.SHIPTO_ADDRESS2,
                   o.SHIPTO_CITY, o.SHIPTO_STATE, o.SHIPTO_ZIP, o.SHIPTO_COUNTRY,
                   o.CARRIERCODE, o.CARRIERNAME, o.SERVICELEVEL, o.DELIVERYTYPE,
                   o.ORDERDATE, o.REQUIREDDELIVERYDATE, o.PROMISEDDELIVERYDATE, o.SHIPBYDATE, o.ACTUALSHIPDATE,
                   o.TOTALQTYORDERED, o.TOTALQTYALLOCATED, o.TOTALQTYPICKED, o.TOTALQTYSHIPPED,
                   o.TOTALWEIGHT, o.TOTALVOLUME, o.TOTALVALUE, o.CURRENCY,
                   o.WAVEKEY, o.LOADKEY, o.ROUTEKEY, o.DOOR,
                   o.NOTES, o.SPECIALINSTRUCTIONS,
                   o.ADDWHO, o.ADDDATE, o.EDITWHO, o.EDITDATE
            FROM ORDERS o
            """;

    @Override
    public Optional<Order> findByKey(String orderKey) {
        String sql = SELECT_BASE + " WHERE o.ORDERSKEY = ?";
        List<Order> results = jdbcTemplate.query(sql, new OrderRowMapper(), orderKey);

        if (results.isEmpty()) {
            return Optional.empty();
        }

        Order order = results.get(0);
        order.setDetails(findDetailsByOrderKey(orderKey));
        return Optional.of(order);
    }

    @Override
    public Optional<Order> findByOrderKey(String orderKey) {
        return findByKey(orderKey);
    }

    @Override
    public Optional<Order> findByExternalKey(String externalOrderKey, String storerKey) {
        String sql = SELECT_BASE + " WHERE o.EXTERNALORDERSKEY = ? AND o.STORERKEY = ?";
        List<Order> results = jdbcTemplate.query(sql, new OrderRowMapper(), externalOrderKey, storerKey);

        if (results.isEmpty()) {
            return Optional.empty();
        }

        Order order = results.get(0);
        order.setDetails(findDetailsByOrderKey(order.getOrderKey()));
        return Optional.of(order);
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        String sql = SELECT_BASE + " WHERE o.STATUS = ?";
        List<Order> orders = jdbcTemplate.query(sql, new OrderRowMapper(), status.getCode());
        orders.forEach(order -> order.setDetails(findDetailsByOrderKey(order.getOrderKey())));
        return orders;
    }

    @Override
    public List<Order> findByStorerKey(String storerKey) {
        String sql = SELECT_BASE + " WHERE o.STORERKEY = ?";
        List<Order> orders = jdbcTemplate.query(sql, new OrderRowMapper(), storerKey);
        orders.forEach(order -> order.setDetails(findDetailsByOrderKey(order.getOrderKey())));
        return orders;
    }

    @Override
    public List<Order> findByWaveKey(String waveKey) {
        String sql = SELECT_BASE + " WHERE o.WAVEKEY = ?";
        List<Order> orders = jdbcTemplate.query(sql, new OrderRowMapper(), waveKey);
        orders.forEach(order -> order.setDetails(findDetailsByOrderKey(order.getOrderKey())));
        return orders;
    }

    @Override
    public List<Order> findOrdersReadyForAllocation(String storerKey) {
        String sql = SELECT_BASE + " WHERE o.STORERKEY = ? AND o.STATUS IN (?, ?)";
        List<Order> orders = jdbcTemplate.query(sql, new OrderRowMapper(),
                storerKey, OrderStatus.NEW.getCode(), OrderStatus.OPEN.getCode());
        orders.forEach(order -> order.setDetails(findDetailsByOrderKey(order.getOrderKey())));
        return orders;
    }

    @Override
    public List<Order> findOrdersReadyForWaving(String storerKey) {
        String sql = SELECT_BASE + " WHERE o.STORERKEY = ? AND o.STATUS = ? AND o.WAVEKEY IS NULL";
        List<Order> orders = jdbcTemplate.query(sql, new OrderRowMapper(),
                storerKey, OrderStatus.ALLOCATED.getCode());
        orders.forEach(order -> order.setDetails(findDetailsByOrderKey(order.getOrderKey())));
        return orders;
    }

    @Override
    public List<Order> findByShipByDateRange(LocalDateTime fromDate, LocalDateTime toDate) {
        String sql = SELECT_BASE + " WHERE o.SHIPBYDATE BETWEEN ? AND ?";
        List<Order> orders = jdbcTemplate.query(sql, new OrderRowMapper(), fromDate, toDate);
        orders.forEach(order -> order.setDetails(findDetailsByOrderKey(order.getOrderKey())));
        return orders;
    }

    @Override
    public Order save(Order order) {
        if (exists(order.getOrderKey())) {
            return update(order);
        }
        return insert(order);
    }

    @Override
    public void delete(String orderKey) {
        // Delete details first
        String deleteDetailsSql = "DELETE FROM ORDERDETAIL WHERE ORDERSKEY = ?";
        jdbcTemplate.update(deleteDetailsSql, orderKey);

        // Delete order
        String deleteOrderSql = "DELETE FROM ORDERS WHERE ORDERSKEY = ?";
        jdbcTemplate.update(deleteOrderSql, orderKey);
    }

    @Override
    public String generateOrderKey() {
        String keyQuery = "EXEC nspg_GetKey 'ORDERS', 1";
        return jdbcTemplate.queryForObject(keyQuery, String.class);
    }

    private boolean exists(String orderKey) {
        if (orderKey == null) {
            return false;
        }
        String sql = "SELECT COUNT(*) FROM ORDERS WHERE ORDERSKEY = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, orderKey);
        return count != null && count > 0;
    }

    private Order insert(Order order) {
        String orderKey = order.getOrderKey();
        if (orderKey == null) {
            orderKey = generateOrderKey();
            order.setOrderKey(orderKey);
        }

        String sql = """
            INSERT INTO ORDERS (ORDERSKEY, EXTERNALORDERSKEY, STORERKEY, ORDERTYPE, ORDERGROUP,
                STATUS, PRIORITY, CONSIGNEEKEY, CONSIGNEENAME, SHIPTO_ADDRESS1, SHIPTO_ADDRESS2,
                SHIPTO_CITY, SHIPTO_STATE, SHIPTO_ZIP, SHIPTO_COUNTRY,
                CARRIERCODE, CARRIERNAME, SERVICELEVEL, DELIVERYTYPE,
                ORDERDATE, REQUIREDDELIVERYDATE, PROMISEDDELIVERYDATE, SHIPBYDATE,
                TOTALQTYORDERED, TOTALQTYALLOCATED, TOTALQTYPICKED, TOTALQTYSHIPPED,
                TOTALWEIGHT, TOTALVOLUME, TOTALVALUE, CURRENCY,
                WAVEKEY, LOADKEY, ROUTEKEY, DOOR, NOTES, SPECIALINSTRUCTIONS,
                ADDWHO, ADDDATE)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE())
            """;

        jdbcTemplate.update(sql,
                orderKey,
                order.getExternalOrderKey(),
                order.getStorerKey(),
                order.getOrderType(),
                order.getOrderGroup(),
                order.getStatus() != null ? order.getStatus().getCode() : OrderStatus.NEW.getCode(),
                order.getPriority() != null ? order.getPriority().ordinal() : OrderPriority.NORMAL.ordinal(),
                order.getConsigneeKey(),
                order.getConsigneeName(),
                order.getShipToAddress1(),
                order.getShipToAddress2(),
                order.getShipToCity(),
                order.getShipToState(),
                order.getShipToZip(),
                order.getShipToCountry(),
                order.getCarrierCode(),
                order.getCarrierName(),
                order.getServiceLevel(),
                order.getDeliveryType(),
                order.getOrderDate(),
                order.getRequiredDeliveryDate(),
                order.getPromisedDeliveryDate(),
                order.getShipByDate(),
                order.getTotalQtyOrdered(),
                order.getTotalQtyAllocated(),
                order.getTotalQtyPicked(),
                order.getTotalQtyShipped(),
                order.getTotalWeight(),
                order.getTotalVolume(),
                order.getTotalValue(),
                order.getCurrency(),
                order.getWaveKey(),
                order.getLoadKey(),
                order.getRouteKey(),
                order.getDoor(),
                order.getNotes(),
                order.getSpecialInstructions(),
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
                STATUS = ?,
                PRIORITY = ?,
                WAVEKEY = ?,
                LOADKEY = ?,
                ACTUALSHIPDATE = ?,
                TOTALQTYORDERED = ?,
                TOTALQTYALLOCATED = ?,
                TOTALQTYPICKED = ?,
                TOTALQTYSHIPPED = ?,
                EDITWHO = ?,
                EDITDATE = GETDATE()
            WHERE ORDERSKEY = ?
            """;

        jdbcTemplate.update(sql,
                order.getStatus() != null ? order.getStatus().getCode() : null,
                order.getPriority() != null ? order.getPriority().ordinal() : null,
                order.getWaveKey(),
                order.getLoadKey(),
                order.getActualShipDate(),
                order.getTotalQtyOrdered(),
                order.getTotalQtyAllocated(),
                order.getTotalQtyPicked(),
                order.getTotalQtyShipped(),
                order.getEditWho(),
                order.getOrderKey()
        );

        return order;
    }

    private void insertDetail(String orderKey, OrderDetail detail) {
        String sql = """
            INSERT INTO ORDERDETAIL (ORDERSKEY, ORDERLINENUMBER, SKU, SKUDESCRIPTION, PACKKEY, UOM,
                ORIGINALQTY, QTYORDERED, QTYALLOCATED, QTYPICKED, QTYSHIPPED, QTYCANCELLED,
                STATUS, UNITPRICE, EXTENDEDPRICE, CURRENCY, LOT, PREFERREDLOT,
                LOTTABLE01, LOTTABLE02, LOTTABLE03, LOTTABLE04, LOTTABLE05,
                NOTES, ADDWHO, ADDDATE)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE())
            """;

        jdbcTemplate.update(sql,
                orderKey,
                detail.getOrderLineNumber(),
                detail.getSku(),
                detail.getSkuDescription(),
                detail.getPackKey(),
                detail.getUom(),
                detail.getOriginalQty(),
                detail.getQtyOrdered(),
                detail.getQtyAllocated(),
                detail.getQtyPicked(),
                detail.getQtyShipped(),
                detail.getQtyCancelled(),
                detail.getStatus() != null ? detail.getStatus().getCode() : OrderDetailStatus.NEW.getCode(),
                detail.getUnitPrice(),
                detail.getExtendedPrice(),
                detail.getCurrency(),
                detail.getLot(),
                detail.getPreferredLot(),
                detail.getLottable01(),
                detail.getLottable02(),
                detail.getLottable03(),
                detail.getLottable04(),
                detail.getLottable05(),
                detail.getNotes(),
                detail.getAddWho()
        );
    }

    private List<OrderDetail> findDetailsByOrderKey(String orderKey) {
        String sql = """
            SELECT ORDERSKEY, ORDERLINENUMBER, SKU, SKUDESCRIPTION, PACKKEY, UOM,
                   ORIGINALQTY, QTYORDERED, QTYALLOCATED, QTYPICKED, QTYSHIPPED, QTYCANCELLED,
                   STATUS, UNITPRICE, EXTENDEDPRICE, CURRENCY, LOT, PREFERREDLOT,
                   EXPIRATIONDATEREQUIRED,
                   LOTTABLE01, LOTTABLE02, LOTTABLE03, LOTTABLE04, LOTTABLE05,
                   LOTTABLE06, LOTTABLE07, LOTTABLE08, LOTTABLE09, LOTTABLE10,
                   NOTES, ADDWHO, ADDDATE, EDITWHO, EDITDATE
            FROM ORDERDETAIL
            WHERE ORDERSKEY = ?
            ORDER BY ORDERLINENUMBER
            """;
        return jdbcTemplate.query(sql, new OrderDetailRowMapper(), orderKey);
    }

    private static class OrderRowMapper implements RowMapper<Order> {
        @Override
        public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Order.builder()
                    .orderKey(rs.getString("ORDERSKEY"))
                    .externalOrderKey(rs.getString("EXTERNALORDERSKEY"))
                    .storerKey(rs.getString("STORERKEY"))
                    .orderType(rs.getString("ORDERTYPE"))
                    .orderGroup(rs.getString("ORDERGROUP"))
                    .status(parseOrderStatus(rs.getString("STATUS")))
                    .priority(parseOrderPriority(rs.getInt("PRIORITY")))
                    .consigneeKey(rs.getString("CONSIGNEEKEY"))
                    .consigneeName(rs.getString("CONSIGNEENAME"))
                    .shipToAddress1(rs.getString("SHIPTO_ADDRESS1"))
                    .shipToAddress2(rs.getString("SHIPTO_ADDRESS2"))
                    .shipToCity(rs.getString("SHIPTO_CITY"))
                    .shipToState(rs.getString("SHIPTO_STATE"))
                    .shipToZip(rs.getString("SHIPTO_ZIP"))
                    .shipToCountry(rs.getString("SHIPTO_COUNTRY"))
                    .carrierCode(rs.getString("CARRIERCODE"))
                    .carrierName(rs.getString("CARRIERNAME"))
                    .serviceLevel(rs.getString("SERVICELEVEL"))
                    .deliveryType(rs.getString("DELIVERYTYPE"))
                    .orderDate(rs.getTimestamp("ORDERDATE") != null ? rs.getTimestamp("ORDERDATE").toLocalDateTime() : null)
                    .requiredDeliveryDate(rs.getTimestamp("REQUIREDDELIVERYDATE") != null ? rs.getTimestamp("REQUIREDDELIVERYDATE").toLocalDateTime() : null)
                    .promisedDeliveryDate(rs.getTimestamp("PROMISEDDELIVERYDATE") != null ? rs.getTimestamp("PROMISEDDELIVERYDATE").toLocalDateTime() : null)
                    .shipByDate(rs.getTimestamp("SHIPBYDATE") != null ? rs.getTimestamp("SHIPBYDATE").toLocalDateTime() : null)
                    .actualShipDate(rs.getTimestamp("ACTUALSHIPDATE") != null ? rs.getTimestamp("ACTUALSHIPDATE").toLocalDateTime() : null)
                    .totalQtyOrdered(rs.getBigDecimal("TOTALQTYORDERED"))
                    .totalQtyAllocated(rs.getBigDecimal("TOTALQTYALLOCATED"))
                    .totalQtyPicked(rs.getBigDecimal("TOTALQTYPICKED"))
                    .totalQtyShipped(rs.getBigDecimal("TOTALQTYSHIPPED"))
                    .totalWeight(rs.getBigDecimal("TOTALWEIGHT"))
                    .totalVolume(rs.getBigDecimal("TOTALVOLUME"))
                    .totalValue(rs.getBigDecimal("TOTALVALUE"))
                    .currency(rs.getString("CURRENCY"))
                    .waveKey(rs.getString("WAVEKEY"))
                    .loadKey(rs.getString("LOADKEY"))
                    .routeKey(rs.getString("ROUTEKEY"))
                    .door(rs.getString("DOOR"))
                    .notes(rs.getString("NOTES"))
                    .specialInstructions(rs.getString("SPECIALINSTRUCTIONS"))
                    .addWho(rs.getString("ADDWHO"))
                    .addDate(rs.getTimestamp("ADDDATE") != null ? rs.getTimestamp("ADDDATE").toLocalDateTime() : null)
                    .editWho(rs.getString("EDITWHO"))
                    .editDate(rs.getTimestamp("EDITDATE") != null ? rs.getTimestamp("EDITDATE").toLocalDateTime() : null)
                    .build();
        }

        private static OrderStatus parseOrderStatus(String code) {
            if (code == null) return OrderStatus.NEW;
            try {
                return OrderStatus.fromCode(code);
            } catch (IllegalArgumentException e) {
                return OrderStatus.NEW;
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
            return OrderDetail.builder()
                    .orderKey(rs.getString("ORDERSKEY"))
                    .orderLineNumber(rs.getString("ORDERLINENUMBER"))
                    .sku(rs.getString("SKU"))
                    .skuDescription(rs.getString("SKUDESCRIPTION"))
                    .packKey(rs.getString("PACKKEY"))
                    .uom(rs.getString("UOM"))
                    .originalQty(rs.getBigDecimal("ORIGINALQTY"))
                    .qtyOrdered(rs.getBigDecimal("QTYORDERED"))
                    .qtyAllocated(rs.getBigDecimal("QTYALLOCATED"))
                    .qtyPicked(rs.getBigDecimal("QTYPICKED"))
                    .qtyShipped(rs.getBigDecimal("QTYSHIPPED"))
                    .qtyCancelled(rs.getBigDecimal("QTYCANCELLED"))
                    .status(parseDetailStatus(rs.getString("STATUS")))
                    .unitPrice(rs.getBigDecimal("UNITPRICE"))
                    .extendedPrice(rs.getBigDecimal("EXTENDEDPRICE"))
                    .currency(rs.getString("CURRENCY"))
                    .lot(rs.getString("LOT"))
                    .preferredLot(rs.getString("PREFERREDLOT"))
                    .expirationDateRequired(rs.getTimestamp("EXPIRATIONDATEREQUIRED") != null ? rs.getTimestamp("EXPIRATIONDATEREQUIRED").toLocalDateTime() : null)
                    .lottable01(rs.getString("LOTTABLE01"))
                    .lottable02(rs.getString("LOTTABLE02"))
                    .lottable03(rs.getString("LOTTABLE03"))
                    .lottable04(rs.getString("LOTTABLE04"))
                    .lottable05(rs.getString("LOTTABLE05"))
                    .lottable06(rs.getTimestamp("LOTTABLE06") != null ? rs.getTimestamp("LOTTABLE06").toLocalDateTime() : null)
                    .lottable07(rs.getTimestamp("LOTTABLE07") != null ? rs.getTimestamp("LOTTABLE07").toLocalDateTime() : null)
                    .lottable08(rs.getTimestamp("LOTTABLE08") != null ? rs.getTimestamp("LOTTABLE08").toLocalDateTime() : null)
                    .lottable09(rs.getString("LOTTABLE09"))
                    .lottable10(rs.getString("LOTTABLE10"))
                    .notes(rs.getString("NOTES"))
                    .addWho(rs.getString("ADDWHO"))
                    .addDate(rs.getTimestamp("ADDDATE") != null ? rs.getTimestamp("ADDDATE").toLocalDateTime() : null)
                    .editWho(rs.getString("EDITWHO"))
                    .editDate(rs.getTimestamp("EDITDATE") != null ? rs.getTimestamp("EDITDATE").toLocalDateTime() : null)
                    .build();
        }

        private static OrderDetailStatus parseDetailStatus(String code) {
            if (code == null) return OrderDetailStatus.NEW;
            try {
                return OrderDetailStatus.fromCode(code);
            } catch (IllegalArgumentException e) {
                return OrderDetailStatus.NEW;
            }
        }
    }
}
