package com.maersk.wms.inbound.workflow.returns;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Signal to inspect a return line item.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InspectReturnLineSignal {

    private String sku;

    // Inspection results
    private String inspectionStatus;    // PASS, FAIL, PARTIAL
    private String inspectionGrade;     // A, B, C, D, SCRAP
    private String inspectionNotes;

    // Quantities after inspection
    private BigDecimal acceptedQty;
    private BigDecimal rejectedQty;
    private BigDecimal damagedQty;

    // Defect info
    private String defectCode;
    private String defectDescription;

    // Inspection checklist results (optional)
    private java.util.List<ChecklistResult> checklistResults;

    // User performing inspection
    private String inspectorId;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChecklistResult {
        private String checkpointCode;
        private boolean passed;
        private String notes;
    }
}
