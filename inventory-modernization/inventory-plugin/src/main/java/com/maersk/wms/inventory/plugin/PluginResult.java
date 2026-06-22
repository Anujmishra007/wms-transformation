package com.maersk.wms.inventory.plugin;

import lombok.Builder;
import lombok.Data;
import java.util.HashMap;
import java.util.Map;

/**
 * Result returned by inventory plugin execution.
 */
@Data
@Builder
public class PluginResult {

    private boolean success;
    private boolean skipRemaining;
    private boolean abort;
    private String errorCode;
    private String errorMessage;

    @Builder.Default
    private Map<String, Object> data = new HashMap<>();

    @Builder.Default
    private Map<String, String> warnings = new HashMap<>();

    public static PluginResult success() {
        return PluginResult.builder().success(true).build();
    }

    public static PluginResult success(Map<String, Object> data) {
        return PluginResult.builder().success(true).data(data != null ? data : new HashMap<>()).build();
    }

    public static PluginResult failure(String errorCode, String errorMessage) {
        return PluginResult.builder().success(false).errorCode(errorCode).errorMessage(errorMessage).build();
    }

    public static PluginResult abort(String errorCode, String errorMessage) {
        return PluginResult.builder().success(false).abort(true).errorCode(errorCode).errorMessage(errorMessage).build();
    }

    public PluginResult withData(String key, Object value) {
        if (this.data == null) this.data = new HashMap<>();
        this.data.put(key, value);
        return this;
    }
}
