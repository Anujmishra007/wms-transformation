package com.maersk.wms.masterdata.plugin;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Registry for master data plugins.
 * Manages plugin discovery and resolution based on client context.
 */
@Component
public class MasterDataPluginRegistry {

    private final Map<Class<? extends MasterDataPlugin>, List<MasterDataPlugin>> pluginsByType = new ConcurrentHashMap<>();

    /**
     * Register a plugin.
     */
    public void register(MasterDataPlugin plugin) {
        getPluginInterfaces(plugin.getClass()).forEach(type -> {
            pluginsByType.computeIfAbsent(type, k -> new ArrayList<>()).add(plugin);
            pluginsByType.get(type).sort(Comparator.comparingInt(MasterDataPlugin::getPriority));
        });
    }

    /**
     * Get all plugins of a specific type for the given context.
     */
    @SuppressWarnings("unchecked")
    public <T extends MasterDataPlugin> List<T> getPlugins(Class<T> type, MasterDataPluginContext context) {
        List<MasterDataPlugin> plugins = pluginsByType.getOrDefault(type, Collections.emptyList());

        return plugins.stream()
                .filter(p -> matchesClient(p, context.getClientCode()))
                .filter(p -> p.isEnabled(context))
                .map(p -> (T) p)
                .collect(Collectors.toList());
    }

    /**
     * Get the first matching plugin of a specific type.
     */
    public <T extends MasterDataPlugin> Optional<T> getPlugin(Class<T> type, MasterDataPluginContext context) {
        return getPlugins(type, context).stream().findFirst();
    }

    /**
     * Execute a plugin operation with all matching plugins.
     */
    public <T extends MasterDataPlugin> PluginResult executeAll(
            Class<T> type,
            MasterDataPluginContext context,
            PluginExecutor<T> executor) {

        List<T> plugins = getPlugins(type, context);

        for (T plugin : plugins) {
            PluginResult result = executor.execute(plugin);
            if (!result.isSuccess() || !result.isContinueProcessing()) {
                return result;
            }
        }

        return PluginResult.success();
    }

    private boolean matchesClient(MasterDataPlugin plugin, String clientCode) {
        String pluginClient = plugin.getClientCode();
        return "*".equals(pluginClient) || pluginClient.equals(clientCode);
    }

    @SuppressWarnings("unchecked")
    private List<Class<? extends MasterDataPlugin>> getPluginInterfaces(Class<?> clazz) {
        List<Class<? extends MasterDataPlugin>> interfaces = new ArrayList<>();

        for (Class<?> iface : clazz.getInterfaces()) {
            if (MasterDataPlugin.class.isAssignableFrom(iface) && iface != MasterDataPlugin.class) {
                interfaces.add((Class<? extends MasterDataPlugin>) iface);
            }
        }

        if (clazz.getSuperclass() != null) {
            interfaces.addAll(getPluginInterfaces(clazz.getSuperclass()));
        }

        return interfaces;
    }

    @FunctionalInterface
    public interface PluginExecutor<T extends MasterDataPlugin> {
        PluginResult execute(T plugin);
    }
}
