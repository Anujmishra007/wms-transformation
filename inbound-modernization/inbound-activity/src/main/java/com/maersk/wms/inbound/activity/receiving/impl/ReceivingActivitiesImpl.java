package com.maersk.wms.inbound.activity.receiving.impl;

import com.maersk.wms.inbound.activity.receiving.ReceivingActivities;
import com.maersk.wms.inbound.domain.operations_service.ReceiptType;
import com.maersk.wms.inbound.service.document_service.AsnService;
import com.maersk.wms.inbound.service.operations_service.ReceivingService;
import com.maersk.wms.inbound.service.operations_service.dto.CreateReceiptRequest;
import com.maersk.wms.inbound.service.operations_service.dto.ReceiveLineRequest;
import com.maersk.wms.inbound.service.operations_service.dto.ReceiveResult;
import com.maersk.wms.inbound.shared.kernel.identifiers.ReceiptKey;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Implementation of ReceivingActivities.
 *
 * Delegates to domain services for actual business logic.
 */
@Component
public class ReceivingActivitiesImpl implements ReceivingActivities {

    private final ReceivingService receivingService;
    private final AsnService asnService;

    public ReceivingActivitiesImpl(ReceivingService receivingService,
                                   AsnService asnService) {
        this.receivingService = receivingService;
        this.asnService = asnService;
    }

    @Override
    public String createReceipt(String storerKey, String poKey, String userId) {
        var request = new CreateReceiptRequest();
        request.setStorerKey(storerKey);
        request.setPoKey(poKey);
        request.setReceiptType(ReceiptType.NORMAL);
        request.setCreatedBy(userId);

        var receipt = receivingService.createReceipt(request);
        return receipt.getReceiptKey().getValue();
    }

    @Override
    public String createReceiptFromAsn(String asnKey, String userId) {
        // Start receiving on the ASN - this creates a linked receipt
        var asn = asnService.startReceiving(asnKey);
        return asn.getReceiptKey();
    }

    @Override
    public void startReceiving(String receiptKey, String userId) {
        receivingService.startReceiving(ReceiptKey.of(receiptKey), userId);
    }

    @Override
    public ReceiveResult receiveLineItem(String receiptKey, ReceiveLineInput input) {
        var request = new ReceiveLineRequest();
        request.setSkuKey(input.getSku());
        request.setQuantity(input.getQuantity() != null ? input.getQuantity() : BigDecimal.ZERO);
        request.setUom(input.getUom());
        request.setLpnKey(input.getLpn());
        request.setLocationKey(input.getLocation());
        request.setConditionCode(input.getConditionCode());
        request.setReceivedBy(input.getUserId());

        return receivingService.receiveLine(ReceiptKey.of(receiptKey), request);
    }

    @Override
    public void reportDamage(String receiptKey, DamageReportInput input) {
        // TODO: Implement damage reporting
        // This would update the receipt detail with damaged quantity
        // and potentially create a disposition record
    }

    @Override
    public void validateReceipt(String receiptKey) {
        // TODO: Implement validation
        // Check for discrepancies, over/under receives, etc.
    }

    @Override
    public void completeReceiving(String receiptKey, String userId) {
        receivingService.completeReceiving(ReceiptKey.of(receiptKey), userId);
    }

    @Override
    public void triggerPutaway(String receiptKey) {
        // Trigger putaway through the ACL
        // This will create putaway tasks for all received items
        // TODO: Implement via PutawayFacade
    }

    @Override
    public void cancelReceipt(String receiptKey, String reason) {
        // TODO: Implement cancellation
        // Update receipt status and reverse any inventory
    }
}
