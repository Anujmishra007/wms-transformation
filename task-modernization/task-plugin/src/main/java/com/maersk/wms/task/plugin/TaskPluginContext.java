package com.maersk.wms.task.plugin;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Context object passed to task plugins during execution.
 * Contains tenant information and shared data between plugin calls.
 */
@Data
@Builder
public class TaskPluginContext {

    private String clientCode;
    private String facilityCode;
    private String userId;
    private String countryCode;

    // Shared data between plugin executions
    @Builder.Default
    private Map<String, Object> attributes = new HashMap<>();

    // Feature flags
    @Builder.Default
    private Map<String, Boolean> featureFlags = new HashMap<>();

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) attributes.get(key);
    }

    public <T> T getAttribute(String key, T defaultValue) {
        Object value = attributes.get(key);
        return value != null ? (T) value : defaultValue;
    }

    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }

    public boolean isFeatureEnabled(String featureName) {
        return featureFlags.getOrDefault(featureName, false);
    }

    public void setFeatureFlag(String featureName, boolean enabled) {
        featureFlags.put(featureName, enabled);
    }
}
