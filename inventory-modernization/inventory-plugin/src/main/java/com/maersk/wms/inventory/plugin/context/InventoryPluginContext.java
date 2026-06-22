package com.maersk.wms.inventory.plugin.context;

import lombok.Builder;
import lombok.Data;
import java.util.HashMap;
import java.util.Map;

/**
 * Context passed to inventory plugins.
 */
@Data
@Builder
public class InventoryPluginContext {

    private String clientCode;
    private String countryCode;
    private String warehouseCode;
    private String userId;
    private String transactionId;
    private String operationType;

    @Builder.Default
    private Map<String, Object> attributes = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) attributes.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key, T defaultValue) {
        return (T) attributes.getOrDefault(key, defaultValue);
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public boolean isClient(String... clients) {
        for (String client : clients) {
            if (client.equalsIgnoreCase(this.clientCode)) return true;
        }
        return false;
    }

    public boolean isRegion(String... regions) {
        for (String region : regions) {
            if (region.equalsIgnoreCase(this.countryCode)) return true;
        }
        return false;
    }
}
