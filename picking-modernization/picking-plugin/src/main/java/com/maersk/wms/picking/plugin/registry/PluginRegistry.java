package com.maersk.wms.picking.plugin.registry;

import com.maersk.wms.picking.plugin.*;
import com.maersk.wms.picking.plugin.context.PluginContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Registry for all picking plugins.
 * Auto-discovers Spring beans implementing plugin interfaces.
 */
@Slf4j
@Component
public class PluginRegistry {

    private final List<GetTaskPlugin> getTaskPlugins;
    private final List<DecodePlugin> decodePlugins;
    private final List<ConfirmPlugin> confirmPlugins;
    private final List<ExtendedValidationPlugin> validationPlugins;
    private final List<ExtendedUpdatePlugin> updatePlugins;
    private final List<ExtendedInfoPlugin> infoPlugins;

    private final Map<String, PickingPlugin> pluginCache = new ConcurrentHashMap<>();

    public PluginRegistry(
            List<GetTaskPlugin> getTaskPlugins,
            List<DecodePlugin> decodePlugins,
            List<ConfirmPlugin> confirmPlugins,
            List<ExtendedValidationPlugin> validationPlugins,
            List<ExtendedUpdatePlugin> updatePlugins,
            List<ExtendedInfoPlugin> infoPlugins) {
        this.getTaskPlugins = getTaskPlugins != null ? getTaskPlugins : Collections.emptyList();
        this.decodePlugins = decodePlugins != null ? decodePlugins : Collections.emptyList();
        this.confirmPlugins = confirmPlugins != null ? confirmPlugins : Collections.emptyList();
        this.validationPlugins = validationPlugins != null ? validationPlugins : Collections.emptyList();
        this.updatePlugins = updatePlugins != null ? updatePlugins : Collections.emptyList();
        this.infoPlugins = infoPlugins != null ? infoPlugins : Collections.emptyList();
    }

    @PostConstruct
    public void init() {
        // Cache all plugins by ID
        cachePlugins(getTaskPlugins);
        cachePlugins(decodePlugins);
        cachePlugins(confirmPlugins);
        cachePlugins(validationPlugins);
        cachePlugins(updatePlugins);
        cachePlugins(infoPlugins);

        log.info("Plugin registry initialized with {} plugins: " +
                 "GetTask={}, Decode={}, Confirm={}, Validation={}, Update={}, Info={}",
                pluginCache.size(),
                getTaskPlugins.size(), decodePlugins.size(), confirmPlugins.size(),
                validationPlugins.size(), updatePlugins.size(), infoPlugins.size());
    }

    private void cachePlugins(List<? extends PickingPlugin> plugins) {
        for (PickingPlugin plugin : plugins) {
            pluginCache.put(plugin.getPluginId(), plugin);
        }
    }

    /**
     * Get applicable GetTask plugins for context.
     */
    public List<GetTaskPlugin> getApplicableGetTaskPlugins(PluginContext context) {
        return getTaskPlugins.stream()
                .filter(PickingPlugin::isEnabled)
                .filter(p -> p.appliesTo(context))
                .sorted(Comparator.comparingInt(PickingPlugin::getOrder))
                .collect(Collectors.toList());
    }

    /**
     * Get applicable Decode plugins for context.
     */
    public List<DecodePlugin> getApplicableDecodePlugins(PluginContext context) {
        return decodePlugins.stream()
                .filter(PickingPlugin::isEnabled)
                .filter(p -> p.appliesTo(context))
                .sorted(Comparator.comparingInt(PickingPlugin::getOrder))
                .collect(Collectors.toList());
    }

    /**
     * Get applicable Confirm plugins for context.
     */
    public List<ConfirmPlugin> getApplicableConfirmPlugins(PluginContext context) {
        return confirmPlugins.stream()
                .filter(PickingPlugin::isEnabled)
                .filter(p -> p.appliesTo(context))
                .sorted(Comparator.comparingInt(PickingPlugin::getOrder))
                .collect(Collectors.toList());
    }

    /**
     * Get applicable Validation plugins for context.
     */
    public List<ExtendedValidationPlugin> getApplicableValidationPlugins(PluginContext context) {
        return validationPlugins.stream()
                .filter(PickingPlugin::isEnabled)
                .filter(p -> p.appliesTo(context))
                .sorted(Comparator.comparingInt(PickingPlugin::getOrder))
                .collect(Collectors.toList());
    }

    /**
     * Get applicable Update plugins for context.
     */
    public List<ExtendedUpdatePlugin> getApplicableUpdatePlugins(PluginContext context) {
        return updatePlugins.stream()
                .filter(PickingPlugin::isEnabled)
                .filter(p -> p.appliesTo(context))
                .sorted(Comparator.comparingInt(PickingPlugin::getOrder))
                .collect(Collectors.toList());
    }

    /**
     * Get applicable Info plugins for context.
     */
    public List<ExtendedInfoPlugin> getApplicableInfoPlugins(PluginContext context) {
        return infoPlugins.stream()
                .filter(PickingPlugin::isEnabled)
                .filter(p -> p.appliesTo(context))
                .sorted(Comparator.comparingInt(PickingPlugin::getOrder))
                .collect(Collectors.toList());
    }

    /**
     * Get plugin by ID.
     */
    public Optional<PickingPlugin> getPlugin(String pluginId) {
        return Optional.ofNullable(pluginCache.get(pluginId));
    }

    /**
     * Get all registered plugin IDs.
     */
    public Set<String> getAllPluginIds() {
        return Collections.unmodifiableSet(pluginCache.keySet());
    }
}
