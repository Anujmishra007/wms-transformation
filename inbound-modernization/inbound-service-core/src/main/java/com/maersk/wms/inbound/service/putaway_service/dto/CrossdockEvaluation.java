package com.maersk.wms.inbound.service.putaway_service.dto;

import com.maersk.wms.inbound.domain.putaway_service.CrossdockCandidate;
import com.maersk.wms.inbound.domain.putaway_service.CrossdockDemand;
import com.maersk.wms.inbound.domain.putaway_service.CrossdockStrategy;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CrossdockEvaluation {
    private CrossdockCandidate candidate;
    private boolean eligible;
    private CrossdockStrategy selectedStrategy;
    private List<CrossdockDemand> matches;
    private BigDecimal totalMatchableQuantity;
    private String stagingZone;
}
