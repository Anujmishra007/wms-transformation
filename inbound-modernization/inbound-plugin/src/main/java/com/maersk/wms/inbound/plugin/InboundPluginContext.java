package com.maersk.wms.inbound.plugin;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Context object passed to inbound plugins.
 * Contains tenant information and operation-specific data.
 */
@Data
@Builder
public class InboundPluginContext {

    private String clientCode;
    private String countryCode;
    private String warehouseCode;
    private String userId;
    private String operationType;

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
     * Set an attribute value.
     */
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    /**
     * Check if an attribute exists.
     */
    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }
}
