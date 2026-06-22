package com.maersk.wms.inbound.domain.operations_service.dto;

import com.maersk.wms.inbound.domain.operations_service.ReturnDisposition;
import lombok.Builder;
import lombok.Data;

/**
 * Result DTO for disposition determination.
 */
@Data
@Builder
public class DispositionResult {
    private ReturnDisposition disposition;
    private String dispositionReason;
    private boolean requiresApproval;
    private String approvalReason;
    private String destinationLocation;
    private String putawayStrategy;
}
