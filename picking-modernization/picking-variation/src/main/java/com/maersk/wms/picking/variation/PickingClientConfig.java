package com.maersk.wms.picking.variation;

import lombok.Data;
import java.util.Map;
import java.util.Set;

/**
 * Client-specific picking configuration.
 */
@Data
public class PickingClientConfig {
    private String clientCode;
    private String clientName;
    private Set<String> enabledFeatures;
    private Set<String> enabledPlugins;
    private Map<String, Object> settings;
    private Map<String, String> barcodeFormats;
    private boolean serialTracked;
    private boolean lotControlled;
    private boolean weightCapture;
}
