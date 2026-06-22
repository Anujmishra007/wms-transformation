package com.maersk.wms.task.variation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads and caches task variation configurations from YAML files.
 */
@Slf4j
@Component
public class TaskVariationLoader {

    private final Map<String, Map<String, Object>> configCache = new ConcurrentHashMap<>();
    private Map<String, Object> defaultConfig = new HashMap<>();

    @PostConstruct
    public void init() {
        loadDefaultConfig();
        loadClientConfigs();
    }

    @SuppressWarnings("unchecked")
    private void loadDefaultConfig() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config/task-config.yaml")) {
            if (is != null) {
                Yaml yaml = new Yaml();
                defaultConfig = yaml.load(is);
                log.info("Loaded default task configuration");
            }
        } catch (Exception e) {
            log.warn("Could not load default task config: {}", e.getMessage());
        }
    }

    private void loadClientConfigs() {
        // In production, this would scan for client-specific YAML files
        // For now, just log that we're ready to load them
        log.info("Task variation loader initialized");
    }

    /**
     * Loads configuration for a specific client.
     */
    @SuppressWarnings("unchecked")
    public void loadClientConfig(String clientCode, String facilityCode) {
        String cacheKey = buildCacheKey(clientCode, facilityCode);
        String resourcePath = String.format("config/clients/%s.yaml", clientCode.toLowerCase());

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (is != null) {
                Yaml yaml = new Yaml();
                Map<String, Object> clientConfig = yaml.load(is);
                configCache.put(cacheKey, clientConfig);
                log.info("Loaded task config for client: {}", clientCode);
            }
        } catch (Exception e) {
            log.warn("Could not load task config for client {}: {}", clientCode, e.getMessage());
        }
    }

    /**
     * Gets a configuration value.
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getValue(String clientCode, String facilityCode, String key, Class<T> type) {
        String cacheKey = buildCacheKey(clientCode, facilityCode);

        // Check client-specific config first
        if (configCache.containsKey(cacheKey)) {
            Object value = getNestedValue(configCache.get(cacheKey), key);
            if (value != null) {
                return Optional.of(convertValue(value, type));
            }
        }

        // Fall back to default config
        Object value = getNestedValue(defaultConfig, key);
        if (value != null) {
            return Optional.of(convertValue(value, type));
        }

        return Optional.empty();
    }

    /**
     * Gets feature flags for a context.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Boolean> getFeatureFlags(String clientCode, String facilityCode) {
        Map<String, Boolean> flags = new HashMap<>();

        // Load from default config
        Object defaultFlags = getNestedValue(defaultConfig, "features");
        if (defaultFlags instanceof Map) {
            ((Map<String, Object>) defaultFlags).forEach((k, v) -> {
                if (v instanceof Boolean) {
                    flags.put(k, (Boolean) v);
                }
            });
        }

        // Override with client-specific flags
        String cacheKey = buildCacheKey(clientCode, facilityCode);
        if (configCache.containsKey(cacheKey)) {
            Object clientFlags = getNestedValue(configCache.get(cacheKey), "features");
            if (clientFlags instanceof Map) {
                ((Map<String, Object>) clientFlags).forEach((k, v) -> {
                    if (v instanceof Boolean) {
                        flags.put(k, (Boolean) v);
                    }
                });
            }
        }

        return flags;
    }

    private String buildCacheKey(String clientCode, String facilityCode) {
        if (clientCode != null && facilityCode != null) {
            return clientCode + ":" + facilityCode;
        } else if (clientCode != null) {
            return clientCode;
        } else if (facilityCode != null) {
            return ":" + facilityCode;
        }
        return "default";
    }

    @SuppressWarnings("unchecked")
    private Object getNestedValue(Map<String, Object> map, String key) {
        String[] parts = key.split("\\.");
        Object current = map;

        for (String part : parts) {
            if (current instanceof Map) {
                current = ((Map<String, Object>) current).get(part);
            } else {
                return null;
            }
        }

        return current;
    }

    @SuppressWarnings("unchecked")
    private <T> T convertValue(Object value, Class<T> type) {
        if (type.isInstance(value)) {
            return type.cast(value);
        }

        if (type == Integer.class && value instanceof Number) {
            return (T) Integer.valueOf(((Number) value).intValue());
        }

        if (type == Boolean.class && value instanceof String) {
            return (T) Boolean.valueOf((String) value);
        }

        return (T) value;
    }
}
