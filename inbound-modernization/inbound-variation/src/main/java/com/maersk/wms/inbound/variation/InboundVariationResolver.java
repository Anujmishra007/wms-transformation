package com.maersk.wms.inbound.variation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Resolves client-specific variations for inbound operations.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InboundVariationResolver {

    private final Map<String, InboundVariationConfig> configCache = new ConcurrentHashMap<>();

    /**
     * Get variation configuration for a client.
     */
    public InboundVariationConfig getConfig(String clientCode, String warehouseCode) {
        String cacheKey = clientCode + ":" + warehouseCode;
        return configCache.computeIfAbsent(cacheKey, k -> loadConfig(clientCode, warehouseCode));
    }

    /**
     * Check if over-receiving is allowed.
     */
    public boolean isOverReceiveAllowed(String clientCode, String warehouseCode) {
        return getConfig(clientCode, warehouseCode).isOverReceiveAllowed();
    }

    /**
     * Get over-receive tolerance percentage.
     */
    public int getOverReceiveTolerance(String clientCode, String warehouseCode) {
        return getConfig(clientCode, warehouseCode).getOverReceiveTolerancePercent();
    }

    /**
     * Check if blind receiving is allowed.
     */
    public boolean isBlindReceiveAllowed(String clientCode, String warehouseCode) {
        return getConfig(clientCode, warehouseCode).isBlindReceiveAllowed();
    }

    /**
     * Get putaway strategy for client.
     */
    public String getPutawayStrategy(String clientCode, String warehouseCode) {
        return getConfig(clientCode, warehouseCode).getDefaultPutawayStrategy();
    }

    /**
     * Check if ASN is required for receiving.
     */
    public boolean isAsnRequired(String clientCode, String warehouseCode) {
        return getConfig(clientCode, warehouseCode).isAsnRequired();
    }

    /**
     * Get minimum shelf life percentage required.
     */
    public int getMinShelfLifePercent(String clientCode, String warehouseCode) {
        return getConfig(clientCode, warehouseCode).getMinShelfLifePercent();
    }

    /**
     * Check if auto-putaway task generation is enabled.
     */
    public boolean isAutoPutawayEnabled(String clientCode, String warehouseCode) {
        return getConfig(clientCode, warehouseCode).isAutoPutawayEnabled();
    }

    /**
     * Refresh configuration cache.
     */
    public void refreshCache(String clientCode, String warehouseCode) {
        String cacheKey = clientCode + ":" + warehouseCode;
        configCache.remove(cacheKey);
        log.info("Refreshed inbound variation cache for {}", cacheKey);
    }

    private InboundVariationConfig loadConfig(String clientCode, String warehouseCode) {
        log.debug("Loading inbound variation config for client: {}, warehouse: {}", clientCode, warehouseCode);
        // In production, this would load from database or config service
        return InboundVariationConfig.defaultConfig();
    }
}
