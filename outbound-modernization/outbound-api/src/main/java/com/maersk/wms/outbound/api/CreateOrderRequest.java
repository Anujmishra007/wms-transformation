package com.maersk.wms.outbound.api;

import com.maersk.wms.outbound.domain.Order;
import com.maersk.wms.outbound.domain.OrderDetail;
import com.maersk.wms.outbound.domain.OrderPriority;
import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Request to create a new order.
 */
@Data
public class CreateOrderRequest {

    @NotBlank(message = "Order number is required")
    private String orderNumber;

    private String externalOrderNumber;
    private String orderType;
    private String priority;

    @NotBlank(message = "Customer code is required")
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

    // Shipping preferences
    private String carrierCode;
    private String shipMethod;
    private LocalDateTime requiredDate;

    @NotEmpty(message = "Order must have at least one line")
    @Valid
    private List<OrderLineRequest> lines;

    public Order toEntity() {
        Order order = new Order();
        order.setExternalOrderKey(orderNumber);
        order.setOrderType(orderType != null ? orderType : "STD");
        order.setPriority(priority != null ? OrderPriority.valueOf(priority) : OrderPriority.NORMAL);
        order.setConsigneeKey(customerCode);
        order.setShipToName(shipToName);
        order.setShipToAddress1(shipToAddress1);
        order.setShipToAddress2(shipToAddress2);
        order.setShipToCity(shipToCity);
        order.setShipToState(shipToState);
        order.setShipToZip(shipToZip);
        order.setShipToCountry(shipToCountry);
        order.setCarrierCode(carrierCode);
        order.setRequiredDeliveryDate(requiredDate);

        List<OrderDetail> details = lines.stream()
                .map(OrderLineRequest::toEntity)
                .collect(Collectors.toList());
        order.setDetails(details);

        return order;
    }

    @Data
    public static class OrderLineRequest {
        @NotBlank(message = "Line number is required")
        private String lineNumber;

        @NotBlank(message = "SKU is required")
        private String sku;

        private String skuDescription;
        private BigDecimal quantity;
        private String uom;
        private String lot;

        public OrderDetail toEntity() {
            OrderDetail detail = new OrderDetail();
            detail.setOrderLineNumber(lineNumber);
            detail.setSku(sku);
            detail.setSkuDescription(skuDescription);
            detail.setQtyOrdered(quantity);
            detail.setUom(uom != null ? uom : "EA");
            detail.setPreferredLot(lot);
            detail.setQtyAllocated(BigDecimal.ZERO);
            detail.setQtyPicked(BigDecimal.ZERO);
            detail.setQtyShipped(BigDecimal.ZERO);
            return detail;
        }
    }
}
