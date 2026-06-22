package com.maersk.wms.inventory.plugin.registry;

import com.maersk.wms.inventory.plugin.*;
import com.maersk.wms.inventory.plugin.context.InventoryPluginContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Registry for all inventory plugins.
 */
@Slf4j
@Component
public class InventoryPluginRegistry {

    private final List<AdjustmentPlugin> adjustmentPlugins;
    private final List<TransferPlugin> transferPlugins;
    private final List<AllocationPlugin> allocationPlugins;
    private final List<HoldPlugin> holdPlugins;

    private final Map<String, InventoryPlugin> pluginCache = new ConcurrentHashMap<>();

    public InventoryPluginRegistry(
            List<AdjustmentPlugin> adjustmentPlugins,
            List<TransferPlugin> transferPlugins,
            List<AllocationPlugin> allocationPlugins,
            List<HoldPlugin> holdPlugins) {
        this.adjustmentPlugins = adjustmentPlugins != null ? adjustmentPlugins : Collections.emptyList();
        this.transferPlugins = transferPlugins != null ? transferPlugins : Collections.emptyList();
        this.allocationPlugins = allocationPlugins != null ? allocationPlugins : Collections.emptyList();
        this.holdPlugins = holdPlugins != null ? holdPlugins : Collections.emptyList();
    }

    @PostConstruct
    public void init() {
        cachePlugins(adjustmentPlugins);
        cachePlugins(transferPlugins);
        cachePlugins(allocationPlugins);
        cachePlugins(holdPlugins);

        log.info("Inventory plugin registry initialized with {} plugins: " +
                 "Adjustment={}, Transfer={}, Allocation={}, Hold={}",
                pluginCache.size(),
                adjustmentPlugins.size(), transferPlugins.size(),
                allocationPlugins.size(), holdPlugins.size());
    }

    private void cachePlugins(List<? extends InventoryPlugin> plugins) {
        for (InventoryPlugin plugin : plugins) {
            pluginCache.put(plugin.getPluginId(), plugin);
        }
    }

    public List<AdjustmentPlugin> getApplicableAdjustmentPlugins(InventoryPluginContext context) {
        return adjustmentPlugins.stream()
                .filter(InventoryPlugin::isEnabled)
                .filter(p -> p.appliesTo(context))
                .sorted(Comparator.comparingInt(InventoryPlugin::getOrder))
                .collect(Collectors.toList());
    }

    public List<TransferPlugin> getApplicableTransferPlugins(InventoryPluginContext context) {
        return transferPlugins.stream()
                .filter(InventoryPlugin::isEnabled)
                .filter(p -> p.appliesTo(context))
                .sorted(Comparator.comparingInt(InventoryPlugin::getOrder))
                .collect(Collectors.toList());
    }

    public List<AllocationPlugin> getApplicableAllocationPlugins(InventoryPluginContext context) {
        return allocationPlugins.stream()
                .filter(InventoryPlugin::isEnabled)
                .filter(p -> p.appliesTo(context))
                .sorted(Comparator.comparingInt(InventoryPlugin::getOrder))
                .collect(Collectors.toList());
    }

    public List<HoldPlugin> getApplicableHoldPlugins(InventoryPluginContext context) {
        return holdPlugins.stream()
                .filter(InventoryPlugin::isEnabled)
                .filter(p -> p.appliesTo(context))
                .sorted(Comparator.comparingInt(InventoryPlugin::getOrder))
                .collect(Collectors.toList());
    }

    public Optional<InventoryPlugin> getPlugin(String pluginId) {
        return Optional.ofNullable(pluginCache.get(pluginId));
    }

    public Set<String> getAllPluginIds() {
        return Collections.unmodifiableSet(pluginCache.keySet());
    }
}
