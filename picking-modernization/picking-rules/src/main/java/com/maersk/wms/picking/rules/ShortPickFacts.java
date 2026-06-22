package com.maersk.wms.picking.rules;

import com.maersk.wms.picking.domain.PickTask;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

/**
 * Facts object for short pick rules.
 */
@Data
@Builder
public class ShortPickFacts {
    private PickTask task;
    private BigDecimal pickedQty;
    private BigDecimal requestedQty;
    private ShortPickDecision decision;
    private String reason;
    private boolean requiresApproval;
}
