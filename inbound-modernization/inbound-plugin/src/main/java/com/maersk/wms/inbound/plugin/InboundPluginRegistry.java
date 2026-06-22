package com.maersk.wms.inbound.plugin;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Registry for inbound plugins.
 * Manages plugin discovery and resolution based on client context.
 */
@Component
public class InboundPluginRegistry {

    private final Map<Class<? extends InboundPlugin>, List<InboundPlugin>> pluginsByType = new ConcurrentHashMap<>();

    /**
     * Register a plugin.
     */
    public void register(InboundPlugin plugin) {
        getPluginInterfaces(plugin.getClass()).forEach(type -> {
            pluginsByType.computeIfAbsent(type, k -> new ArrayList<>()).add(plugin);
            // Sort by priority
            pluginsByType.get(type).sort(Comparator.comparingInt(InboundPlugin::getPriority));
        });
    }

    /**
     * Get all plugins of a specific type for the given context.
     */
    @SuppressWarnings("unchecked")
    public <T extends InboundPlugin> List<T> getPlugins(Class<T> type, InboundPluginContext context) {
        List<InboundPlugin> plugins = pluginsByType.getOrDefault(type, Collections.emptyList());

        return plugins.stream()
                .filter(p -> matchesClient(p, context.getClientCode()))
                .filter(p -> p.isEnabled(context))
                .map(p -> (T) p)
                .collect(Collectors.toList());
    }

    /**
     * Get the first matching plugin of a specific type.
     */
    public <T extends InboundPlugin> Optional<T> getPlugin(Class<T> type, InboundPluginContext context) {
        return getPlugins(type, context).stream().findFirst();
    }

    /**
     * Execute a plugin operation with all matching plugins.
     */
    public <T extends InboundPlugin> PluginResult executeAll(
            Class<T> type,
            InboundPluginContext context,
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

    private boolean matchesClient(InboundPlugin plugin, String clientCode) {
        String pluginClient = plugin.getClientCode();
        return "*".equals(pluginClient) || pluginClient.equals(clientCode);
    }

    @SuppressWarnings("unchecked")
    private List<Class<? extends InboundPlugin>> getPluginInterfaces(Class<?> clazz) {
        List<Class<? extends InboundPlugin>> interfaces = new ArrayList<>();

        for (Class<?> iface : clazz.getInterfaces()) {
            if (InboundPlugin.class.isAssignableFrom(iface) && iface != InboundPlugin.class) {
                interfaces.add((Class<? extends InboundPlugin>) iface);
            }
        }

        if (clazz.getSuperclass() != null) {
            interfaces.addAll(getPluginInterfaces(clazz.getSuperclass()));
        }

        return interfaces;
    }

    @FunctionalInterface
    public interface PluginExecutor<T extends InboundPlugin> {
        PluginResult execute(T plugin);
    }
}
