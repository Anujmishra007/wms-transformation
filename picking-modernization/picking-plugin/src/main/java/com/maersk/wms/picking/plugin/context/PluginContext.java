package com.maersk.wms.picking.plugin.context;

import lombok.Builder;
import lombok.Data;
import java.util.HashMap;
import java.util.Map;

/**
 * Context passed to all plugins for variation resolution.
 * Contains client, region, warehouse, and user information.
 */
@Data
@Builder
public class PluginContext {

    /** Client code (e.g., NIKE, ADIDAS, HM) */
    private String clientCode;

    /** Country/region code (e.g., KR, US, EU) */
    private String countryCode;

    /** Warehouse code (e.g., KRIC01, KRIC02) */
    private String warehouseCode;

    /** User ID performing the operation */
    private String userId;

    /** User's language ID */
    private String languageId;

    /** Function code (e.g., FN839, FN610) */
    private String functionCode;

    /** Screen ID (e.g., 4640, 4641) */
    private String screenId;

    /** Transaction ID for tracing */
    private String transactionId;

    /** Equipment type being used */
    private String equipmentType;

    /** Zone being worked */
    private String zone;

    /** Additional context attributes */
    @Builder.Default
    private Map<String, Object> attributes = new HashMap<>();

    /**
     * Get an attribute value.
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) attributes.get(key);
    }

    /**
     * Get an attribute with default value.
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key, T defaultValue) {
        return (T) attributes.getOrDefault(key, defaultValue);
    }

    /**
     * Set an attribute.
     */
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    /**
     * Check if context matches client.
     */
    public boolean isClient(String... clients) {
        for (String client : clients) {
            if (client.equalsIgnoreCase(this.clientCode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if context matches region.
     */
    public boolean isRegion(String... regions) {
        for (String region : regions) {
            if (region.equalsIgnoreCase(this.countryCode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if context matches warehouse.
     */
    public boolean isWarehouse(String... warehouses) {
        for (String warehouse : warehouses) {
            if (warehouse.equalsIgnoreCase(this.warehouseCode)) {
                return true;
            }
        }
        return false;
    }
}
