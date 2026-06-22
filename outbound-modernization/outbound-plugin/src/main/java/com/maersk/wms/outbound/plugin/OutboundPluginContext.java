package com.maersk.wms.outbound.plugin;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Context object passed to outbound plugins.
 */
@Data
@Builder
public class OutboundPluginContext {

    private String clientCode;
    private String countryCode;
    private String warehouseCode;
    private String userId;
    private String operationType;

    @Builder.Default
    private Map<String, Object> attributes = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) attributes.get(key);
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }
}
