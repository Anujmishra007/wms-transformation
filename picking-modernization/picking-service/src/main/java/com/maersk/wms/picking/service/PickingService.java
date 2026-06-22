package com.maersk.wms.picking.service;

import com.maersk.wms.picking.domain.*;
import com.maersk.wms.picking.plugin.*;
import com.maersk.wms.picking.plugin.context.PluginContext;
import com.maersk.wms.picking.plugin.registry.PluginRegistry;
import com.maersk.wms.picking.rules.PickingRulesEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Core picking service implementing FN839 business logic.
 * Orchestrates plugins, rules, and domain operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PickingService {

    private final PluginRegistry pluginRegistry;
    private final PickingRulesEngine rulesEngine;
    // private final TaskRepository taskRepository;
    // private final InventoryRepository inventoryRepository;

    /**
     * Get next task for user based on criteria.
     *
     * @param criteria Task query criteria
     * @param context Plugin context
     * @return Optional task
     */
    @Transactional(readOnly = true)
    public Optional<PickTask> getNextTask(GetTaskCriteria criteria, PluginContext context) {
        log.info("Getting next task for user {} in warehouse {}",
                context.getUserId(), context.getWarehouseCode());

        // Execute pre-GetTask plugins
        List<GetTaskPlugin> plugins = pluginRegistry.getApplicableGetTaskPlugins(context);
        for (GetTaskPlugin plugin : plugins) {
            PluginResult result = plugin.preGetTask(criteria, context);
            if (result.isAbort()) {
                log.warn("Plugin {} aborted GetTask: {}", plugin.getPluginId(), result.getErrorMessage());
                return Optional.empty();
            }
            // Apply modified criteria from plugin result
            if (result.getData().containsKey("criteria")) {
                criteria = (GetTaskCriteria) result.getData().get("criteria");
            }
        }

        // Query tasks from repository
        // List<PickTask> tasks = taskRepository.findAvailableTasks(criteria);
        List<PickTask> tasks = List.of(); // Placeholder

        // Apply prioritization rules
        tasks = rulesEngine.applyPrioritizationRules(tasks);

        // Execute post-GetTask plugins
        for (GetTaskPlugin plugin : plugins) {
            PluginResult result = plugin.postGetTask(tasks, context);
            if (result.getData().containsKey("tasks")) {
                @SuppressWarnings("unchecked")
                List<PickTask> modifiedTasks = (List<PickTask>) result.getData().get("tasks");
                tasks = modifiedTasks;
            }
        }

        return tasks.isEmpty() ? Optional.empty() : Optional.of(tasks.get(0));
    }

    /**
     * Decode barcode for current task.
     *
     * @param barcode Scanned barcode
     * @param expectedType Expected barcode type
     * @param task Current task
     * @param context Plugin context
     * @return Decode result
     */
    @Transactional(readOnly = true)
    public DecodeResult decodeBarcode(String barcode, BarcodeType expectedType,
                                      PickTask task, PluginContext context) {
        log.info("Decoding barcode {} (expected: {}) for task {}",
                barcode, expectedType, task.getTaskId());

        // Execute pre-decode plugins
        List<DecodePlugin> plugins = pluginRegistry.getApplicableDecodePlugins(context);
        String processedBarcode = barcode;

        for (DecodePlugin plugin : plugins) {
            PluginResult result = plugin.preDecode(processedBarcode, expectedType, context);
            if (result.getData().containsKey("barcode")) {
                processedBarcode = (String) result.getData().get("barcode");
            }
        }

        // Try custom decode from plugins
        DecodeResult result = null;
        for (DecodePlugin plugin : plugins) {
            result = plugin.decode(processedBarcode, expectedType, task, context);
            if (result != null) {
                break; // Use first successful custom decode
            }
        }

        // Default decoding if no plugin handled it
        if (result == null) {
            result = defaultDecode(processedBarcode, expectedType, task);
        }

        // Execute post-decode plugins for validation
        for (DecodePlugin plugin : plugins) {
            PluginResult postResult = plugin.postDecode(result, task, context);
            if (!postResult.isSuccess()) {
                result.setValidated(false);
                result.setErrorCode(postResult.getErrorCode());
                result.setMessage(postResult.getErrorMessage());
            }
        }

        return result;
    }

    private DecodeResult defaultDecode(String barcode, BarcodeType expectedType, PickTask task) {
        switch (expectedType) {
            case LOCATION:
                boolean locationMatch = barcode.equalsIgnoreCase(task.getFromLocation());
                return locationMatch
                        ? DecodeResult.success(BarcodeType.LOCATION, barcode)
                        : DecodeResult.validationFailed(BarcodeType.LOCATION,
                                task.getFromLocation(), barcode, "LOCATION_MISMATCH");

            case SKU:
                boolean skuMatch = barcode.equalsIgnoreCase(task.getSku());
                return skuMatch
                        ? DecodeResult.success(BarcodeType.SKU, barcode)
                        : DecodeResult.validationFailed(BarcodeType.SKU,
                                task.getSku(), barcode, "SKU_MISMATCH");

            case LPN:
                return DecodeResult.success(BarcodeType.LPN, barcode);

            default:
                return DecodeResult.success(expectedType, barcode);
        }
    }

    /**
     * Confirm pick completion.
     *
     * @param confirmation Pick confirmation details
     * @param task Current task
     * @param context Plugin context
     * @return Updated task
     */
    @Transactional
    public PickTask confirmPick(PickConfirmation confirmation, PickTask task, PluginContext context) {
        log.info("Confirming pick for task {}, qty {}",
                task.getTaskId(), confirmation.getPickedQty());

        // Execute pre-confirm plugins
        List<ConfirmPlugin> plugins = pluginRegistry.getApplicableConfirmPlugins(context);
        for (ConfirmPlugin plugin : plugins) {
            PluginResult result = plugin.preConfirm(confirmation, task, context);
            if (!result.isSuccess()) {
                throw new RuntimeException("Pre-confirm validation failed: " + result.getErrorMessage());
            }
        }

        // Handle short pick
        if (confirmation.isShortPick(task.getRequestedQty())) {
            for (ConfirmPlugin plugin : plugins) {
                plugin.onShortPick(confirmation, task, context);
            }
        }

        // Update task
        task.setPickedQty(confirmation.getPickedQty());
        task.setStatus(confirmation.isFullPick(task.getRequestedQty())
                ? TaskStatus.COMPLETED : TaskStatus.SHORT_PICK);
        task.setCompletedAt(java.time.LocalDateTime.now());

        // Persist changes
        // task = taskRepository.save(task);

        // Execute post-confirm plugins
        for (ConfirmPlugin plugin : plugins) {
            plugin.postConfirm(confirmation, task, context);
        }

        return task;
    }
}
