package com.maersk.wms.task.plugin;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Registry for managing and discovering task plugins.
 */
@Component
public class TaskPluginRegistry {

    private final Map<Class<? extends TaskPlugin>, List<TaskPlugin>> pluginsByType = new ConcurrentHashMap<>();

    /**
     * Registers a plugin.
     */
    public void register(TaskPlugin plugin) {
        getPluginInterfaces(plugin).forEach(interfaceClass -> {
            pluginsByType.computeIfAbsent(interfaceClass, k -> new ArrayList<>()).add(plugin);
            // Sort by priority after adding
            pluginsByType.get(interfaceClass).sort(Comparator.comparingInt(TaskPlugin::getPriority));
        });
    }

    /**
     * Unregisters a plugin.
     */
    public void unregister(TaskPlugin plugin) {
        pluginsByType.values().forEach(plugins -> plugins.remove(plugin));
    }

    /**
     * Gets all plugins of a specific type for a client.
     */
    @SuppressWarnings("unchecked")
    public <T extends TaskPlugin> List<T> getPlugins(Class<T> pluginType, String clientCode) {
        List<TaskPlugin> plugins = pluginsByType.getOrDefault(pluginType, List.of());
        return plugins.stream()
                .filter(p -> p.isEnabled())
                .filter(p -> "*".equals(p.getClientCode()) || p.getClientCode().equals(clientCode))
                .sorted(Comparator.comparingInt(TaskPlugin::getPriority))
                .map(p -> (T) p)
                .collect(Collectors.toList());
    }

    /**
     * Gets all task creation plugins for a client.
     */
    public List<TaskCreationPlugin> getTaskCreationPlugins(String clientCode) {
        return getPlugins(TaskCreationPlugin.class, clientCode);
    }

    /**
     * Gets all task assignment plugins for a client.
     */
    public List<TaskAssignmentPlugin> getTaskAssignmentPlugins(String clientCode) {
        return getPlugins(TaskAssignmentPlugin.class, clientCode);
    }

    /**
     * Gets all task execution plugins for a client.
     */
    public List<TaskExecutionPlugin> getTaskExecutionPlugins(String clientCode) {
        return getPlugins(TaskExecutionPlugin.class, clientCode);
    }

    /**
     * Gets all task prioritization plugins for a client.
     */
    public List<TaskPrioritizationPlugin> getTaskPrioritizationPlugins(String clientCode) {
        return getPlugins(TaskPrioritizationPlugin.class, clientCode);
    }

    /**
     * Executes all plugins of a type and aggregates results.
     */
    public <T extends TaskPlugin> PluginResult executePlugins(
            Class<T> pluginType,
            String clientCode,
            PluginExecutor<T> executor) {

        List<T> plugins = getPlugins(pluginType, clientCode);
        List<String> allWarnings = new ArrayList<>();

        for (T plugin : plugins) {
            try {
                PluginResult result = executor.execute(plugin);
                allWarnings.addAll(result.getWarnings());

                if (!result.isSuccess()) {
                    result.getWarnings().addAll(allWarnings);
                    return result;
                }

                if (!result.isShouldContinue()) {
                    result.getWarnings().addAll(allWarnings);
                    return result;
                }
            } catch (Exception e) {
                return PluginResult.failure(
                        "PLUGIN_ERROR",
                        "Plugin " + plugin.getPluginName() + " failed: " + e.getMessage()
                );
            }
        }

        PluginResult success = PluginResult.success();
        success.getWarnings().addAll(allWarnings);
        return success;
    }

    @SuppressWarnings("unchecked")
    private List<Class<? extends TaskPlugin>> getPluginInterfaces(TaskPlugin plugin) {
        List<Class<? extends TaskPlugin>> interfaces = new ArrayList<>();
        for (Class<?> iface : plugin.getClass().getInterfaces()) {
            if (TaskPlugin.class.isAssignableFrom(iface) && iface != TaskPlugin.class) {
                interfaces.add((Class<? extends TaskPlugin>) iface);
            }
        }
        // Also add the base interface
        interfaces.add(TaskPlugin.class);
        return interfaces;
    }

    /**
     * Functional interface for plugin execution.
     */
    @FunctionalInterface
    public interface PluginExecutor<T extends TaskPlugin> {
        PluginResult execute(T plugin);
    }
}
