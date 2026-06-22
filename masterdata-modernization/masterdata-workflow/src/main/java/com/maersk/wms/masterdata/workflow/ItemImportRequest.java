package com.maersk.wms.masterdata.workflow;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Request for item import workflow.
 */
@Data
@Builder
public class ItemImportRequest {

    private String clientCode;
    private String facilityCode;
    private String userId;
    private String sourceFile;
    private String sourceFormat;  // CSV, JSON, XML, EDI
    private List<Map<String, Object>> items;
    private boolean validateOnly;
    private boolean updateExisting;
    private int batchSize;
}
