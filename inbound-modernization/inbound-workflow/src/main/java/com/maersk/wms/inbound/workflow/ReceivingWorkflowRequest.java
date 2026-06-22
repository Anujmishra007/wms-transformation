package com.maersk.wms.inbound.workflow;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Request object for receiving workflow.
 */
@Data
@Builder
public class ReceivingWorkflowRequest {

    private String clientCode;
    private String countryCode;
    private String warehouseCode;
    private String userId;

    private String asnKey;
    private String poKey;
    private String externalReceiptKey;
    private String receiptType;

    private String door;
    private String carrierKey;
    private String trailerNumber;
    private String sealNumber;

    private List<ReceivingLineRequest> lines;
    private boolean autoGeneratePutaway;
    private boolean autoClose;

    @Data
    @Builder
    public static class ReceivingLineRequest {
        private String sku;
        private String lot;
        private String lpn;
        private int expectedQty;
        private String location;
        private String conditionCode;
    }
}
