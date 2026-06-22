package com.maersk.wms.outbound.api;

import com.maersk.wms.outbound.domain.Order;
import com.maersk.wms.outbound.domain.OrderStatus;
import com.maersk.wms.outbound.plugin.OutboundPluginContext;
import com.maersk.wms.outbound.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST API for order operations.
 */
@RestController
@RequestMapping("/api/v1/outbound/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Orders", description = "Order management APIs")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Create a new order")
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            @RequestHeader("X-Client-Code") String clientCode,
            @RequestHeader("X-Facility-Code") String facilityCode,
            @RequestHeader("X-User-Id") String userId) {

        OutboundPluginContext context = OutboundPluginContext.builder()
                .clientCode(clientCode)
                .facilityCode(facilityCode)
                .userId(userId)
                .build();

        Order order = request.toEntity();
        Order created = orderService.createOrder(order, context);

        return ResponseEntity.ok(OrderResponse.fromEntity(created));
    }

    @GetMapping("/{orderNumber}")
    @Operation(summary = "Get order by order number")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String orderNumber) {
        return orderService.getOrder(orderNumber)
                .map(OrderResponse::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get orders by status")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(
            @RequestParam OrderStatus status,
            @RequestHeader("X-Client-Code") String clientCode) {

        List<OrderResponse> orders = orderService.getOrdersByStatus(status, clientCode)
                .stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(orders);
    }

    @PostMapping("/{orderNumber}/allocate")
    @Operation(summary = "Allocate inventory for an order")
    public ResponseEntity<AllocationResponse> allocateOrder(
            @PathVariable String orderNumber,
            @RequestHeader("X-Client-Code") String clientCode,
            @RequestHeader("X-Facility-Code") String facilityCode,
            @RequestHeader("X-User-Id") String userId) {

        OutboundPluginContext context = OutboundPluginContext.builder()
                .clientCode(clientCode)
                .facilityCode(facilityCode)
                .userId(userId)
                .build();

        OrderService.AllocationResult result = orderService.allocateOrder(orderNumber, context);

        return ResponseEntity.ok(AllocationResponse.fromResult(result));
    }

    @PostMapping("/{orderNumber}/release")
    @Operation(summary = "Release order for picking")
    public ResponseEntity<OrderResponse> releaseOrder(
            @PathVariable String orderNumber,
            @RequestHeader("X-Client-Code") String clientCode,
            @RequestHeader("X-Facility-Code") String facilityCode,
            @RequestHeader("X-User-Id") String userId) {

        OutboundPluginContext context = OutboundPluginContext.builder()
                .clientCode(clientCode)
                .facilityCode(facilityCode)
                .userId(userId)
                .build();

        Order released = orderService.releaseOrder(orderNumber, context);

        return ResponseEntity.ok(OrderResponse.fromEntity(released));
    }
}
