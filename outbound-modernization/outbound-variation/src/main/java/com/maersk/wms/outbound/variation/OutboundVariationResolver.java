package com.maersk.wms.outbound.variation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Resolves client-specific variations for outbound operations.
 * Determines configuration based on client code, facility, and operation type.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OutboundVariationResolver {

    private final Map<String, OutboundVariationConfig> configCache = new ConcurrentHashMap<>();
    private final OutboundVariationLoader variationLoader;

    /**
     * Resolve variation config for client and facility.
     */
    public OutboundVariationConfig resolve(String clientCode, String facilityCode) {
        String cacheKey = clientCode + ":" + facilityCode;

        return configCache.computeIfAbsent(cacheKey, key -> {
            log.debug("Loading variation config for client: {}, facility: {}", clientCode, facilityCode);

            // Try client+facility specific config first
            Optional<OutboundVariationConfig> config = variationLoader.loadConfig(clientCode, facilityCode);

            if (config.isPresent()) {
                return config.get();
            }

            // Fall back to client-level config
            config = variationLoader.loadConfig(clientCode, null);

            if (config.isPresent()) {
                return config.get();
            }

            // Fall back to default config
            log.info("Using default variation config for client: {}", clientCode);
            return OutboundVariationConfig.defaultConfig();
        });
    }

    /**
     * Get allocation strategy for client.
     */
    public String getAllocationStrategy(String clientCode, String facilityCode) {
        return resolve(clientCode, facilityCode).getAllocationStrategy();
    }

    /**
     * Get wave grouping strategy for client.
     */
    public String getWaveGroupingStrategy(String clientCode, String facilityCode) {
        return resolve(clientCode, facilityCode).getWaveGroupingStrategy();
    }

    /**
     * Check if auto-allocation is enabled.
     */
    public boolean isAutoAllocationEnabled(String clientCode, String facilityCode) {
        return resolve(clientCode, facilityCode).isAutoAllocationEnabled();
    }

    /**
     * Get cartonization strategy for client.
     */
    public String getCartonizationStrategy(String clientCode, String facilityCode) {
        return resolve(clientCode, facilityCode).getCartonizationStrategy();
    }

    /**
     * Clear cached configuration.
     */
    public void clearCache() {
        configCache.clear();
        log.info("Variation config cache cleared");
    }

    /**
     * Clear cached configuration for specific client.
     */
    public void clearCache(String clientCode, String facilityCode) {
        String cacheKey = clientCode + ":" + facilityCode;
        configCache.remove(cacheKey);
        log.info("Variation config cache cleared for: {}", cacheKey);
    }
}
