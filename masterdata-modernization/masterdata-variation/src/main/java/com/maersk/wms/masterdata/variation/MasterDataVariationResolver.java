package com.maersk.wms.masterdata.variation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Resolves client-specific variations for master data operations.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MasterDataVariationResolver {

    private final Map<String, MasterDataVariationConfig> configCache = new ConcurrentHashMap<>();
    private final MasterDataVariationLoader variationLoader;

    public MasterDataVariationConfig resolve(String clientCode, String facilityCode) {
        String cacheKey = clientCode + ":" + facilityCode;

        return configCache.computeIfAbsent(cacheKey, key -> {
            log.debug("Loading variation config for client: {}, facility: {}", clientCode, facilityCode);

            Optional<MasterDataVariationConfig> config = variationLoader.loadConfig(clientCode, facilityCode);
            if (config.isPresent()) {
                return config.get();
            }

            config = variationLoader.loadConfig(clientCode, null);
            if (config.isPresent()) {
                return config.get();
            }

            log.info("Using default variation config for client: {}", clientCode);
            return MasterDataVariationConfig.defaultConfig();
        });
    }

    public String getLocationCodeFormat(String clientCode, String facilityCode) {
        return resolve(clientCode, facilityCode).getLocationCodeFormat();
    }

    public boolean isAutoSkuGenerationEnabled(String clientCode, String facilityCode) {
        return resolve(clientCode, facilityCode).isAutoSkuGenerationEnabled();
    }

    public void clearCache() {
        configCache.clear();
    }
}
