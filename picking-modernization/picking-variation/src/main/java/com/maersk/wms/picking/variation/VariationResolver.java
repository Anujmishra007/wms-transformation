package com.maersk.wms.picking.variation;

import com.maersk.wms.picking.plugin.context.PluginContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Set;

/**
 * Resolves variation context from request to determine
 * which plugins and configurations apply.
 *
 * Replaces legacy conditional SP routing based on:
 * - Client code (NIKE, ADIDAS, HM, UNILEVER, etc.)
 * - Region/country (KR, US, EU, APAC, etc.)
 * - Warehouse (KRIC01, USLA01, etc.)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VariationResolver {

    private final PickingConfigLoader configLoader;

    /**
     * Build plugin context from request parameters.
     */
    public PluginContext resolveContext(String clientCode, String countryCode,
                                         String warehouseCode, String userId) {
        log.debug("Resolving context for client={}, country={}, warehouse={}",
                clientCode, countryCode, warehouseCode);

        PluginContext context = PluginContext.builder()
                .clientCode(clientCode)
                .countryCode(countryCode)
                .warehouseCode(warehouseCode)
                .userId(userId)
                .functionCode("FN839")
                .build();

        // Load client-specific configuration
        PickingClientConfig clientConfig = configLoader.loadClientConfig(clientCode);
        if (clientConfig != null) {
            context.setAttribute("clientConfig", clientConfig);
            context.setAttribute("enabledFeatures", clientConfig.getEnabledFeatures());
        }

        // Load region-specific configuration
        PickingRegionConfig regionConfig = configLoader.loadRegionConfig(countryCode);
        if (regionConfig != null) {
            context.setAttribute("regionConfig", regionConfig);
        }

        return context;
    }

    /**
     * Check if a feature is enabled for the context.
     */
    public boolean isFeatureEnabled(PluginContext context, String feature) {
        @SuppressWarnings("unchecked")
        Set<String> enabledFeatures = context.getAttribute("enabledFeatures");
        return enabledFeatures != null && enabledFeatures.contains(feature);
    }

    /**
     * Get configuration value for context.
     */
    public <T> T getConfig(PluginContext context, String key, T defaultValue) {
        PickingClientConfig clientConfig = context.getAttribute("clientConfig");
        if (clientConfig != null && clientConfig.getSettings().containsKey(key)) {
            @SuppressWarnings("unchecked")
            T value = (T) clientConfig.getSettings().get(key);
            return value;
        }
        return defaultValue;
    }
}
