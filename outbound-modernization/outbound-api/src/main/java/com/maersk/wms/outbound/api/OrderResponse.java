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

    private String orderKey;
    private String externalOrderKey;
    private String orderType;
    private String status;
    private String priority;
    private String consigneeKey;

    // Ship-to information
    private String shipToName;
    private String shipToAddress1;
    private String shipToAddress2;
    private String shipToCity;
    private String shipToState;
    private String shipToZip;
    private String shipToCountry;

    // Shipping
    private String carrierCode;
    private LocalDateTime requiredDeliveryDate;

    // Summary
    private int lineCount;
    private BigDecimal totalQtyOrdered;
    private BigDecimal totalQtyAllocated;
    private BigDecimal totalQtyPicked;
    private BigDecimal totalQtyShipped;

    // Lines
    private List<OrderLineResponse> lines;

    // Timestamps
    private LocalDateTime addDate;
    private String addWho;

    public static OrderResponse fromEntity(Order order) {
        return OrderResponse.builder()
                .orderKey(order.getOrderKey())
                .externalOrderKey(order.getExternalOrderKey())
                .orderType(order.getOrderType())
                .status(order.getStatus() != null ? order.getStatus().name() : null)
                .priority(order.getPriority() != null ? order.getPriority().name() : null)
                .consigneeKey(order.getConsigneeKey())
                .shipToName(order.getShipToName())
                .shipToAddress1(order.getShipToAddress1())
                .shipToAddress2(order.getShipToAddress2())
                .shipToCity(order.getShipToCity())
                .shipToState(order.getShipToState())
                .shipToZip(order.getShipToZip())
                .shipToCountry(order.getShipToCountry())
                .carrierCode(order.getCarrierCode())
                .requiredDeliveryDate(order.getRequiredDeliveryDate())
                .lineCount(order.getDetails() != null ? order.getDetails().size() : 0)
                .totalQtyOrdered(order.getTotalQtyOrdered())
                .totalQtyAllocated(order.getTotalQtyAllocated())
                .totalQtyPicked(order.getTotalQtyPicked())
                .totalQtyShipped(order.getTotalQtyShipped())
                .lines(order.getDetails() != null ?
                        order.getDetails().stream()
                                .map(OrderLineResponse::fromEntity)
                                .collect(Collectors.toList()) : List.of())
                .addDate(order.getAddDate())
                .addWho(order.getAddWho())
                .build();
    }

    @Data
    @Builder
    public static class OrderLineResponse {
        private String orderKey;
        private String orderLineNumber;
        private String sku;
        private String skuDescription;
        private BigDecimal qtyOrdered;
        private BigDecimal qtyAllocated;
        private BigDecimal qtyPicked;
        private BigDecimal qtyShipped;
        private String status;
        private String uom;

        public static OrderLineResponse fromEntity(OrderDetail detail) {
            return OrderLineResponse.builder()
                    .orderKey(detail.getOrderKey())
                    .orderLineNumber(detail.getOrderLineNumber())
                    .sku(detail.getSku())
                    .skuDescription(detail.getSkuDescription())
                    .qtyOrdered(detail.getQtyOrdered())
                    .qtyAllocated(detail.getQtyAllocated())
                    .qtyPicked(detail.getQtyPicked())
                    .qtyShipped(detail.getQtyShipped())
                    .status(detail.getStatus() != null ? detail.getStatus().name() : null)
                    .uom(detail.getUom())
                    .build();
        }
    }
}
