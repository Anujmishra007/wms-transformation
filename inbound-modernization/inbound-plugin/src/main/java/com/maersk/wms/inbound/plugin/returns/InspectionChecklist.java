package com.maersk.wms.inbound.plugin.returns;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Inspection checklist for return items.
 * Defines the criteria and checks required during inspection.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InspectionChecklist {

    private String sku;
    private String productCategory;

    @Builder.Default
    private List<InspectionPoint> inspectionPoints = new ArrayList<>();

    @Builder.Default
    private List<String> defectCodes = new ArrayList<>();

    private boolean requiresPhoto;
    private boolean requiresSerial;
    private boolean requiresWeightCheck;
    private boolean requiresFunctionalTest;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InspectionPoint {
        private String code;
        private String description;
        private boolean required;
        private String checkType;  // VISUAL, FUNCTIONAL, MEASUREMENT
        private String passFailCriteria;
    }
}
