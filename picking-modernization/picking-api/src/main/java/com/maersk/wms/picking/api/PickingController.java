package com.maersk.wms.picking.api;

import com.maersk.wms.picking.api.dto.*;
import com.maersk.wms.picking.domain.*;
import com.maersk.wms.picking.plugin.GetTaskCriteria;
import com.maersk.wms.picking.plugin.context.PluginContext;
import com.maersk.wms.picking.service.PickingService;
import com.maersk.wms.picking.variation.VariationResolver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

/**
 * REST API for FN839 Piece Pick operations.
 *
 * Replaces legacy RDT screens:
 * - Screen 4640: Task Menu (getNextTask)
 * - Screen 4641: Location Scan (decodeLocation)
 * - Screen 4642: SKU Scan (decodeSku)
 * - Screen 4643: Quantity Entry (confirmPick)
 * - Screen 4644: Short Pick (confirmShortPick)
 * - Screen 4645: Container Scan (decodeLpn)
 * - Screen 4646: Task Complete (completeTask)
 * - Screen 4647: Skip Task (skipTask)
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/picking")
@RequiredArgsConstructor
@Tag(name = "Picking API", description = "FN839 Piece Pick Operations")
public class PickingController {

    private final PickingService pickingService;
    private final VariationResolver variationResolver;

    /**
     * Get next available task for user.
     * Screen 4640 - Task Menu
     */
    @GetMapping("/tasks/next")
    @Operation(summary = "Get next pick task", description = "Returns the next available task for the operator")
    public ResponseEntity<TaskResponse> getNextTask(
            @RequestParam String userId,
            @RequestParam String warehouse,
            @RequestParam(required = false) String zone,
            @RequestHeader("X-Client-Code") String clientCode,
            @RequestHeader("X-Country-Code") String countryCode) {

        log.info("GET /tasks/next - user={}, warehouse={}, zone={}", userId, warehouse, zone);

        PluginContext context = variationResolver.resolveContext(clientCode, countryCode, warehouse, userId);

        GetTaskCriteria criteria = GetTaskCriteria.builder()
                .zones(zone != null ? java.util.List.of(zone) : null)
                .maxTasks(1)
                .build();

        Optional<PickTask> task = pickingService.getNextTask(criteria, context);

        return task.map(t -> ResponseEntity.ok(TaskResponse.from(t)))
                   .orElse(ResponseEntity.noContent().build());
    }

    /**
     * Decode barcode and validate against task.
     * Screens 4641, 4642, 4645
     */
    @PostMapping("/decode")
    @Operation(summary = "Decode barcode", description = "Decodes and validates a scanned barcode")
    public ResponseEntity<DecodeResponse> decode(
            @Valid @RequestBody DecodeRequest request,
            @RequestHeader("X-Client-Code") String clientCode,
            @RequestHeader("X-Country-Code") String countryCode,
            @RequestHeader("X-Warehouse-Code") String warehouseCode,
            @RequestHeader("X-User-Id") String userId) {

        log.info("POST /decode - barcode={}, type={}, task={}",
                request.getBarcode(), request.getExpectedType(), request.getTaskId());

        PluginContext context = variationResolver.resolveContext(clientCode, countryCode, warehouseCode, userId);

        // Get task from request or session
        PickTask task = PickTask.builder()
                .taskId(request.getTaskId())
                .fromLocation(request.getExpectedLocation())
                .sku(request.getExpectedSku())
                .build();

        DecodeResult result = pickingService.decodeBarcode(
                request.getBarcode(),
                BarcodeType.fromCode(request.getExpectedType()),
                task,
                context);

        return ResponseEntity.ok(DecodeResponse.from(result));
    }

    /**
     * Confirm pick completion.
     * Screens 4643, 4644
     */
    @PostMapping("/confirm")
    @Operation(summary = "Confirm pick", description = "Confirms pick completion with quantity")
    public ResponseEntity<ConfirmResponse> confirmPick(
            @Valid @RequestBody ConfirmRequest request,
            @RequestHeader("X-Client-Code") String clientCode,
            @RequestHeader("X-Country-Code") String countryCode,
            @RequestHeader("X-Warehouse-Code") String warehouseCode,
            @RequestHeader("X-User-Id") String userId) {

        log.info("POST /confirm - task={}, qty={}", request.getTaskId(), request.getPickedQty());

        PluginContext context = variationResolver.resolveContext(clientCode, countryCode, warehouseCode, userId);

        PickTask task = PickTask.builder()
                .taskId(request.getTaskId())
                .requestedQty(request.getRequestedQty())
                .build();

        PickConfirmation confirmation = PickConfirmation.builder()
                .taskId(request.getTaskId())
                .pickedQty(request.getPickedQty())
                .fromLpn(request.getFromLpn())
                .toLpn(request.getToLpn())
                .userId(userId)
                .build();

        PickTask updatedTask = pickingService.confirmPick(confirmation, task, context);

        return ResponseEntity.ok(ConfirmResponse.from(updatedTask));
    }

    /**
     * Skip current task.
     * Screen 4647
     */
    @PostMapping("/tasks/{taskId}/skip")
    @Operation(summary = "Skip task", description = "Skips the current task")
    public ResponseEntity<Void> skipTask(
            @PathVariable String taskId,
            @RequestParam String reason,
            @RequestHeader("X-User-Id") String userId) {

        log.info("POST /tasks/{}/skip - reason={}", taskId, reason);
        // Implementation
        return ResponseEntity.ok().build();
    }
}
