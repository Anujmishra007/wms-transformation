package com.maersk.wms.inventory.service;

import com.maersk.wms.inventory.domain.*;
import com.maersk.wms.inventory.plugin.*;
import com.maersk.wms.inventory.plugin.context.InventoryPluginContext;
import com.maersk.wms.inventory.plugin.registry.InventoryPluginRegistry;
import com.maersk.wms.inventory.rules.InventoryRulesEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Core inventory service implementing business logic.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryPluginRegistry pluginRegistry;
    private final InventoryRulesEngine rulesEngine;

    /**
     * Get inventory by key.
     */
    @Transactional(readOnly = true)
    public Optional<LotxLocxId> getInventory(String lotxLocxIdKey) {
        log.info("Getting inventory: {}", lotxLocxIdKey);
        // return inventoryRepository.findById(lotxLocxIdKey);
        return Optional.empty();
    }

    /**
     * Get available inventory for SKU.
     */
    @Transactional(readOnly = true)
    public List<LotxLocxId> getAvailableInventory(String sku, String warehouse) {
        log.info("Getting available inventory for SKU {} in warehouse {}", sku, warehouse);
        // return inventoryRepository.findAvailableBySku(sku, warehouse);
        return List.of();
    }

    /**
     * Allocate inventory using FIFO rules.
     */
    @Transactional
    public List<LotxLocxId> allocateInventory(String sku, BigDecimal qty,
                                               InventoryPluginContext context) {
        log.info("Allocating {} units of SKU {}", qty, sku);

        // Get available inventory
        List<LotxLocxId> available = getAvailableInventory(sku, context.getWarehouseCode());

        // Execute pre-allocation plugins
        List<AllocationPlugin> plugins = pluginRegistry.getApplicableAllocationPlugins(context);
        for (AllocationPlugin plugin : plugins) {
            PluginResult result = plugin.preAllocate(sku, qty, context);
            if (!result.isSuccess()) {
                throw new RuntimeException("Pre-allocation failed: " + result.getErrorMessage());
            }
        }

        // Apply FIFO rules
        String fifoVariant = context.getAttribute("fifoVariant", "STANDARD");
        List<LotxLocxId> sorted = rulesEngine.applyAllocationRules(available, fifoVariant);

        // Allow plugins to customize sorting
        for (AllocationPlugin plugin : plugins) {
            sorted = plugin.sortForAllocation(sorted, context);
        }

        // Allocate from sorted inventory
        // ... allocation logic ...

        // Execute post-allocation plugins
        for (AllocationPlugin plugin : plugins) {
            plugin.postAllocate(sorted, context);
        }

        return sorted;
    }

    /**
     * Process inventory adjustment.
     */
    @Transactional
    public InventoryAdjustment processAdjustment(InventoryAdjustment adjustment,
                                                  InventoryPluginContext context) {
        log.info("Processing adjustment for SKU {} at location {}",
                adjustment.getSku(), adjustment.getLocation());

        // Execute pre-adjustment plugins
        List<AdjustmentPlugin> plugins = pluginRegistry.getApplicableAdjustmentPlugins(context);
        for (AdjustmentPlugin plugin : plugins) {
            PluginResult result = plugin.preAdjustment(adjustment, context);
            if (!result.isSuccess()) {
                throw new RuntimeException("Pre-adjustment validation failed: " + result.getErrorMessage());
            }
        }

        // Check approval rules
        var approvalResult = rulesEngine.evaluateAdjustmentApproval(adjustment);
        if (approvalResult.isRequiresApproval()) {
            adjustment.setStatus(AdjustmentStatus.PENDING_APPROVAL);
            // Save and return - workflow will wait for approval
        } else {
            // Apply adjustment directly
            adjustment.setStatus(AdjustmentStatus.APPLIED);
            // Update LOTxLOCxID qty
        }

        // Execute post-adjustment plugins
        for (AdjustmentPlugin plugin : plugins) {
            plugin.postAdjustment(adjustment, context);
        }

        return adjustment;
    }

    /**
     * Process inventory transfer.
     */
    @Transactional
    public InventoryTransfer processTransfer(InventoryTransfer transfer,
                                              InventoryPluginContext context) {
        log.info("Processing transfer from {} to {}",
                transfer.getFromLocation(), transfer.getToLocation());

        // Execute pre-transfer plugins
        List<TransferPlugin> plugins = pluginRegistry.getApplicableTransferPlugins(context);
        for (TransferPlugin plugin : plugins) {
            PluginResult result = plugin.preTransfer(transfer, context);
            if (!result.isSuccess()) {
                throw new RuntimeException("Pre-transfer validation failed: " + result.getErrorMessage());
            }
        }

        // Validate destination
        for (TransferPlugin plugin : plugins) {
            plugin.validateDestination(transfer.getToLocation(), transfer, context);
        }

        // Execute transfer
        transfer.setStatus(TransferStatus.COMPLETED);
        // Update source LOTxLOCxID (decrement)
        // Update/create destination LOTxLOCxID (increment)

        // Execute post-transfer plugins
        for (TransferPlugin plugin : plugins) {
            plugin.postTransfer(transfer, context);
        }

        return transfer;
    }

    /**
     * Apply inventory hold.
     */
    @Transactional
    public InventoryHold applyHold(InventoryHold hold, InventoryPluginContext context) {
        log.info("Applying hold {} to scope {}", hold.getHoldCode(), hold.getScope());

        List<HoldPlugin> plugins = pluginRegistry.getApplicableHoldPlugins(context);
        for (HoldPlugin plugin : plugins) {
            PluginResult result = plugin.preHold(hold, context);
            if (!result.isSuccess()) {
                throw new RuntimeException("Pre-hold validation failed: " + result.getErrorMessage());
            }
        }

        // Apply hold to matching inventory
        hold.setActive(true);
        // Update LOTxLOCxID status

        for (HoldPlugin plugin : plugins) {
            plugin.postHold(hold, context);
        }

        return hold;
    }

    /**
     * Release inventory hold.
     */
    @Transactional
    public InventoryHold releaseHold(InventoryHold hold, InventoryPluginContext context) {
        log.info("Releasing hold {}", hold.getHoldCode());

        List<HoldPlugin> plugins = pluginRegistry.getApplicableHoldPlugins(context);
        for (HoldPlugin plugin : plugins) {
            PluginResult result = plugin.preRelease(hold, context);
            if (!result.isSuccess()) {
                throw new RuntimeException("Pre-release validation failed: " + result.getErrorMessage());
            }
        }

        // Release hold
        hold.setActive(false);
        // Update LOTxLOCxID status back to available

        for (HoldPlugin plugin : plugins) {
            plugin.postRelease(hold, context);
        }

        return hold;
    }
}
