package com.maersk.wms.picking.variation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads client and region configurations for picking operations.
 */
@Slf4j
@Component
public class PickingConfigLoader {

    private final Map<String, PickingClientConfig> clientConfigCache = new ConcurrentHashMap<>();
    private final Map<String, PickingRegionConfig> regionConfigCache = new ConcurrentHashMap<>();

    /**
     * Load client-specific configuration.
     */
    public PickingClientConfig loadClientConfig(String clientCode) {
        return clientConfigCache.computeIfAbsent(clientCode, this::loadClientConfigFromSource);
    }

    /**
     * Load region-specific configuration.
     */
    public PickingRegionConfig loadRegionConfig(String countryCode) {
        return regionConfigCache.computeIfAbsent(countryCode, this::loadRegionConfigFromSource);
    }

    private PickingClientConfig loadClientConfigFromSource(String clientCode) {
        // Load from YAML/database
        log.debug("Loading client config for: {}", clientCode);
        // Would load from classpath:config/clients/{clientCode}.yaml
        return null;
    }

    private PickingRegionConfig loadRegionConfigFromSource(String countryCode) {
        log.debug("Loading region config for: {}", countryCode);
        // Would load from classpath:config/regions/{countryCode}.yaml
        return null;
    }

    /**
     * Clear configuration cache (for refresh).
     */
    public void clearCache() {
        clientConfigCache.clear();
        regionConfigCache.clear();
        log.info("Configuration cache cleared");
    }
}
