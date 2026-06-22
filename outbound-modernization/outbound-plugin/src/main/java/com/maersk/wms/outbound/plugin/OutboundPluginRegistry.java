package com.maersk.wms.outbound.plugin;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Registry for outbound plugins.
 * Manages plugin discovery and resolution based on client context.
 */
@Component
public class OutboundPluginRegistry {

    private final Map<Class<? extends OutboundPlugin>, List<OutboundPlugin>> pluginsByType = new ConcurrentHashMap<>();

    /**
     * Register a plugin.
     */
    public void register(OutboundPlugin plugin) {
        getPluginInterfaces(plugin.getClass()).forEach(type -> {
            pluginsByType.computeIfAbsent(type, k -> new ArrayList<>()).add(plugin);
            pluginsByType.get(type).sort(Comparator.comparingInt(OutboundPlugin::getPriority));
        });
    }

    /**
     * Get all plugins of a specific type for the given context.
     */
    @SuppressWarnings("unchecked")
    public <T extends OutboundPlugin> List<T> getPlugins(Class<T> type, OutboundPluginContext context) {
        List<OutboundPlugin> plugins = pluginsByType.getOrDefault(type, Collections.emptyList());

        return plugins.stream()
                .filter(p -> matchesClient(p, context.getClientCode()))
                .filter(p -> p.isEnabled(context))
                .map(p -> (T) p)
                .collect(Collectors.toList());
    }

    /**
     * Get the first matching plugin of a specific type.
     */
    public <T extends OutboundPlugin> Optional<T> getPlugin(Class<T> type, OutboundPluginContext context) {
        return getPlugins(type, context).stream().findFirst();
    }

    /**
     * Execute a plugin operation with all matching plugins.
     */
    public <T extends OutboundPlugin> PluginResult executeAll(
            Class<T> type,
            OutboundPluginContext context,
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

    private boolean matchesClient(OutboundPlugin plugin, String clientCode) {
        String pluginClient = plugin.getClientCode();
        return "*".equals(pluginClient) || pluginClient.equals(clientCode);
    }

    @SuppressWarnings("unchecked")
    private List<Class<? extends OutboundPlugin>> getPluginInterfaces(Class<?> clazz) {
        List<Class<? extends OutboundPlugin>> interfaces = new ArrayList<>();

        for (Class<?> iface : clazz.getInterfaces()) {
            if (OutboundPlugin.class.isAssignableFrom(iface) && iface != OutboundPlugin.class) {
                interfaces.add((Class<? extends OutboundPlugin>) iface);
            }
        }

        if (clazz.getSuperclass() != null) {
            interfaces.addAll(getPluginInterfaces(clazz.getSuperclass()));
        }

        return interfaces;
    }

    @FunctionalInterface
    public interface PluginExecutor<T extends OutboundPlugin> {
        PluginResult execute(T plugin);
    }
}
