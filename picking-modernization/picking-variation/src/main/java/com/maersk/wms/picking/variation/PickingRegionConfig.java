package com.maersk.wms.picking.variation;

import lombok.Data;
import java.util.Map;

/**
 * Region-specific picking configuration.
 */
@Data
public class PickingRegionConfig {
    private String countryCode;
    private String regionName;
    private String timezone;
    private String dateFormat;
    private String defaultLanguage;
    private Map<String, Object> regulatorySettings;
    private Map<String, String> labelFormats;
}
