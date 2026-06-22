package com.maersk.wms.outbound.api;

import com.maersk.wms.outbound.domain.Order;
import com.maersk.wms.outbound.domain.OrderDetail;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Response for order operations.
 */
@Data
@Builder
public class OrderResponse {

    private Long id;
    private String orderNumber;
    private String externalOrderNumber;
    private String orderType;
    private String status;
    private String priority;
    private String customerCode;

    // Ship-to information
    private String shipToCode;
    private String shipToName;
    private String shipToAddress1;
    private String shipToAddress2;
    private String shipToCity;
    private String shipToState;
    private String shipToZip;
    private String shipToCountry;

    // Shipping
    private String carrierCode;
    private String shipMethod;
    private LocalDateTime requiredDate;

    // Summary
    private int lineCount;
    private BigDecimal totalQty;
    private BigDecimal allocatedQty;
    private BigDecimal pickedQty;
    private BigDecimal shippedQty;

    // Lines
    private List<OrderLineResponse> lines;

    // Timestamps
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime releasedAt;
    private String releasedBy;

    public static OrderResponse fromEntity(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .externalOrderNumber(order.getExternalOrderNumber())
                .orderType(order.getOrderType() != null ? order.getOrderType().name() : null)
                .status(order.getStatus() != null ? order.getStatus().name() : null)
                .priority(order.getPriority() != null ? order.getPriority().name() : null)
                .customerCode(order.getCustomerCode())
                .shipToCode(order.getShipToCode())
                .shipToName(order.getShipToName())
                .shipToAddress1(order.getShipToAddress1())
                .shipToAddress2(order.getShipToAddress2())
                .shipToCity(order.getShipToCity())
                .shipToState(order.getShipToState())
                .shipToZip(order.getShipToZip())
                .shipToCountry(order.getShipToCountry())
                .carrierCode(order.getCarrierCode())
                .shipMethod(order.getShipMethod())
                .requiredDate(order.getRequiredDate())
                .lineCount(order.getDetails() != null ? order.getDetails().size() : 0)
                .totalQty(calculateTotalQty(order))
                .allocatedQty(calculateAllocatedQty(order))
                .pickedQty(calculatePickedQty(order))
                .shippedQty(calculateShippedQty(order))
                .lines(order.getDetails() != null ?
                        order.getDetails().stream()
                                .map(OrderLineResponse::fromEntity)
                                .collect(Collectors.toList()) : List.of())
                .createdAt(order.getCreatedAt())
                .createdBy(order.getCreatedBy())
                .releasedAt(order.getReleasedAt())
                .releasedBy(order.getReleasedBy())
                .build();
    }

    private static BigDecimal calculateTotalQty(Order order) {
        if (order.getDetails() == null) return BigDecimal.ZERO;
        return order.getDetails().stream()
                .map(d -> d.getOrderedQty() != null ? d.getOrderedQty() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static BigDecimal calculateAllocatedQty(Order order) {
        if (order.getDetails() == null) return BigDecimal.ZERO;
        return order.getDetails().stream()
                .map(d -> d.getAllocatedQty() != null ? d.getAllocatedQty() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static BigDecimal calculatePickedQty(Order order) {
        if (order.getDetails() == null) return BigDecimal.ZERO;
        return order.getDetails().stream()
                .map(d -> d.getPickedQty() != null ? d.getPickedQty() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static BigDecimal calculateShippedQty(Order order) {
        if (order.getDetails() == null) return BigDecimal.ZERO;
        return order.getDetails().stream()
                .map(d -> d.getShippedQty() != null ? d.getShippedQty() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Data
    @Builder
    public static class OrderLineResponse {
        private Long id;
        private String lineNumber;
        private String sku;
        private String skuDescription;
        private BigDecimal orderedQty;
        private BigDecimal allocatedQty;
        private BigDecimal pickedQty;
        private BigDecimal shippedQty;
        private String status;
        private String uom;

        public static OrderLineResponse fromEntity(OrderDetail detail) {
            return OrderLineResponse.builder()
                    .id(detail.getId())
                    .lineNumber(detail.getLineNumber())
                    .sku(detail.getSku())
                    .skuDescription(detail.getSkuDescription())
                    .orderedQty(detail.getOrderedQty())
                    .allocatedQty(detail.getAllocatedQty())
                    .pickedQty(detail.getPickedQty())
                    .shippedQty(detail.getShippedQty())
                    .status(detail.getStatus() != null ? detail.getStatus().name() : null)
                    .uom(detail.getUom())
                    .build();
        }
    }
}
