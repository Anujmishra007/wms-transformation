package com.maersk.wms.task.variation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * Resolves task configuration variations based on client, facility, and context.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskVariationResolver {

    private final TaskVariationLoader variationLoader;

    /**
     * Resolves a configuration value with fallback chain:
     * client+facility -> client -> facility -> default
     */
    public <T> T resolve(String clientCode, String facilityCode, String key, Class<T> type, T defaultValue) {
        // Try client + facility specific
        Optional<T> value = variationLoader.getValue(clientCode, facilityCode, key, type);
        if (value.isPresent()) {
            return value.get();
        }

        // Try client specific
        value = variationLoader.getValue(clientCode, null, key, type);
        if (value.isPresent()) {
            return value.get();
        }

        // Try facility specific
        value = variationLoader.getValue(null, facilityCode, key, type);
        if (value.isPresent()) {
            return value.get();
        }

        // Return default
        return defaultValue;
    }

    /**
     * Resolves task configuration for a specific context.
     */
    public TaskVariationConfig resolveConfig(String clientCode, String facilityCode) {
        return TaskVariationConfig.builder()
                .clientCode(clientCode)
                .facilityCode(facilityCode)
                .autoAssignEnabled(resolve(clientCode, facilityCode, "autoAssign.enabled", Boolean.class, true))
                .autoAssignStrategy(resolve(clientCode, facilityCode, "autoAssign.strategy", String.class, "LEAST_LOADED"))
                .priorityEscalationEnabled(resolve(clientCode, facilityCode, "priority.escalation.enabled", Boolean.class, true))
                .escalationThresholdMinutes(resolve(clientCode, facilityCode, "priority.escalation.thresholdMinutes", Integer.class, 60))
                .maxTasksPerUser(resolve(clientCode, facilityCode, "assignment.maxTasksPerUser", Integer.class, 10))
                .defaultTaskTimeoutMinutes(resolve(clientCode, facilityCode, "task.defaultTimeoutMinutes", Integer.class, 120))
                .allowShortage(resolve(clientCode, facilityCode, "task.allowShortage", Boolean.class, true))
                .requireReasonForShort(resolve(clientCode, facilityCode, "task.requireReasonForShort", Boolean.class, true))
                .enableZoneRouting(resolve(clientCode, facilityCode, "routing.enableZoneRouting", Boolean.class, true))
                .enableBatching(resolve(clientCode, facilityCode, "batching.enabled", Boolean.class, true))
                .defaultBatchSize(resolve(clientCode, facilityCode, "batching.defaultSize", Integer.class, 20))
                .build();
    }

    /**
     * Gets all feature flags for a context.
     */
    public Map<String, Boolean> getFeatureFlags(String clientCode, String facilityCode) {
        return variationLoader.getFeatureFlags(clientCode, facilityCode);
    }

    /**
     * Checks if a specific feature is enabled.
     */
    public boolean isFeatureEnabled(String clientCode, String facilityCode, String featureName) {
        return resolve(clientCode, facilityCode, "features." + featureName, Boolean.class, false);
    }
}
