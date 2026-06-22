package com.maersk.wms.masterdata.legacy;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Result from legacy stored procedure call for parity comparison.
 */
@Data
@Builder
public class ParityResult {

    private boolean success;
    private int errorCode;
    private String errorMessage;
    private String resultKey;
    private Map<String, Object> legacyResult;
}
