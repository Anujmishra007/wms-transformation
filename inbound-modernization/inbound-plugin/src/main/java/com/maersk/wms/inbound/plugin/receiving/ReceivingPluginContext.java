package com.maersk.wms.inbound.plugin.receiving;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Context passed to receiving plugins.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceivingPluginContext {
    private String storerKey;
    private String facility;
    private String userId;
    private String receiptType;
    private String clientCode;
    private String regionCode;

    @Builder.Default
    private Map<String, Object> attributes = new HashMap<>();

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) attributes.get(key);
    }

    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }
}
