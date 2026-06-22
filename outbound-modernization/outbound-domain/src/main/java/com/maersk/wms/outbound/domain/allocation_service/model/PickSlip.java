package com.maersk.wms.outbound.domain.allocation_service.model;

import com.maersk.wms.outbound.shared.kernel.identifiers.PickHeaderKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.StorerKey;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * PickSlip entity - represents a printed pick slip document.
 * Part of Inventory Allocation Service bounded context.
 */
@Data
@Builder
public class PickSlip {

    private String pickSlipKey;
    private PickHeaderKey pickHeaderKey;
    private StorerKey storerKey;

    // Slip attributes
    private PickSlipStatus status;
    private int totalLines;
    private int completedLines;

    // Assignment
    private String assignedUser;
    private String printer;

    // Print info
    private LocalDateTime printedTime;
    private int printCount;

    // Pick headers on this slip
    @Builder.Default
    private List<PickHeaderKey> pickHeaders = new ArrayList<>();

    // Audit
    private String addWho;
    private LocalDateTime addDate;
    private String editWho;
    private LocalDateTime editDate;

    // Business methods
    public boolean isComplete() {
        return completedLines >= totalLines;
    }

    public double getCompletionPercentage() {
        if (totalLines == 0) return 0.0;
        return (double) completedLines / totalLines * 100.0;
    }

    public void addPickHeader(PickHeaderKey pickHeaderKey) {
        if (!pickHeaders.contains(pickHeaderKey)) {
            pickHeaders.add(pickHeaderKey);
        }
    }
}
