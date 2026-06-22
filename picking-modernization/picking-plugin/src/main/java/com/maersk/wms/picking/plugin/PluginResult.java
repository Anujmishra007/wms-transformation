package com.maersk.wms.picking.plugin;

import lombok.Builder;
import lombok.Data;
import java.util.HashMap;
import java.util.Map;

/**
 * Result returned by plugin execution.
 * Carries success/failure status, modified data, and error information.
 */
@Data
@Builder
public class PluginResult {

    /** Whether the plugin executed successfully */
    private boolean success;

    /** Whether to skip remaining plugins in chain */
    private boolean skipRemaining;

    /** Whether to abort the operation */
    private boolean abort;

    /** Error code if failed */
    private String errorCode;

    /** Error message if failed */
    private String errorMessage;

    /** Modified/enriched data to pass along */
    @Builder.Default
    private Map<String, Object> data = new HashMap<>();

    /** Warning messages (non-fatal) */
    @Builder.Default
    private Map<String, String> warnings = new HashMap<>();

    /**
     * Create a successful result.
     */
    public static PluginResult success() {
        return PluginResult.builder()
                .success(true)
                .build();
    }

    /**
     * Create a successful result with data.
     */
    public static PluginResult success(Map<String, Object> data) {
        return PluginResult.builder()
                .success(true)
                .data(data != null ? data : new HashMap<>())
                .build();
    }

    /**
     * Create a failed result.
     */
    public static PluginResult failure(String errorCode, String errorMessage) {
        return PluginResult.builder()
                .success(false)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build();
    }

    /**
     * Create a result that aborts the operation.
     */
    public static PluginResult abort(String errorCode, String errorMessage) {
        return PluginResult.builder()
                .success(false)
                .abort(true)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build();
    }

    /**
     * Create a result that skips remaining plugins.
     */
    public static PluginResult skipRemaining() {
        return PluginResult.builder()
                .success(true)
                .skipRemaining(true)
                .build();
    }

    /**
     * Add data to the result.
     */
    public PluginResult withData(String key, Object value) {
        if (this.data == null) {
            this.data = new HashMap<>();
        }
        this.data.put(key, value);
        return this;
    }

    /**
     * Add a warning to the result.
     */
    public PluginResult withWarning(String code, String message) {
        if (this.warnings == null) {
            this.warnings = new HashMap<>();
        }
        this.warnings.put(code, message);
        return this;
    }
}
