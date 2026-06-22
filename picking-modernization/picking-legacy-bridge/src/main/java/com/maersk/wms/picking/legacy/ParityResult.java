package com.maersk.wms.picking.legacy;

import lombok.Data;
import java.util.HashMap;
import java.util.Map;

/**
 * Result of parity comparison between Java and legacy SP.
 */
@Data
public class ParityResult {
    private String operation;
    private boolean match;
    private Map<String, String[]> discrepancies = new HashMap<>();

    public void addDiscrepancy(String field, String javaValue, String legacyValue) {
        discrepancies.put(field, new String[]{javaValue, legacyValue});
        match = false;
    }
}
