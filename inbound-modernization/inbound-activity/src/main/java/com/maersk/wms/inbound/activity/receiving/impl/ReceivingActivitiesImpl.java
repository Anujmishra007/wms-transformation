package com.maersk.wms.inbound.activity.receiving.impl;

import com.maersk.wms.inbound.activity.receiving.ReceivingActivities;
import com.maersk.wms.inbound.acl.putaway.PutawayFacade;
import com.maersk.wms.inbound.domain.receiving.ReceiptType;
import com.maersk.wms.inbound.service.receiving.AsnService;
import com.maersk.wms.inbound.service.receiving.ReceivingService;
import com.maersk.wms.inbound.service.receiving.dto.*;
import com.maersk.wms.inbound.shared.kernel.identifiers.LpnKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.ReceiptKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.SkuKey;
import com.maersk.wms.inbound.workflow.receiving.DamageReportSignal;
import com.maersk.wms.inbound.workflow.receiving.ReceiveLineSignal;
import org.springframework.stereotype.Component;

/**
 * Implementation of ReceivingActivities.
 *
 * Delegates to domain services for actual business logic.
 */
@Component
public class ReceivingActivitiesImpl implements ReceivingActivities {

    private final ReceivingService receivingService;
    private final AsnService asnService;
    private final PutawayFacade putawayFacade;

    public ReceivingActivitiesImpl(ReceivingService receivingService,
                                   AsnService asnService,
                                   PutawayFacade putawayFacade) {
        this.receivingService = receivingService;
        this.asnService = asnService;
        this.putawayFacade = putawayFacade;
    }

    @Override
    public String createReceipt(String storerKey, String poKey, String userId) {
        var request = CreateReceiptRequest.builder()
                .storerKey(storerKey)
                .poKey(poKey)
                .receiptType(ReceiptType.NORMAL)
                .userId(userId)
                .build();

        var receipt = receivingService.createReceipt(request);
        return receipt.getReceiptKey().getValue();
    }

    @Override
    public String createReceiptFromAsn(String asnKey, String userId) {
        var receipt = asnService.convertToReceipt(asnKey, userId);
        return receipt.getReceiptKey().getValue();
    }

    @Override
    public void startReceiving(String receiptKey, String userId) {
        receivingService.startReceiving(ReceiptKey.of(receiptKey), userId);
    }

    @Override
    public ReceiveResult receiveLineItem(String receiptKey, ReceiveLineSignal signal) {
        var request = ReceiveLineItemRequest.builder()
                .lineNumber(signal.getLineNumber())
                .sku(SkuKey.of("DEFAULT", signal.getSku()))  // TODO: Get storer from receipt
                .packKey(signal.getPackKey())
                .uom(signal.getUom())
                .quantity(signal.getQuantity())
                .lpn(signal.getLpn() != null ? LpnKey.of(signal.getLpn()) : null)
                .location(signal.getLocation())
                .conditionCode(signal.getConditionCode())
                .userId(signal.getUserId())
                .build();

        return receivingService.receiveLineItem(ReceiptKey.of(receiptKey), request);
    }

    @Override
    public void reportDamage(String receiptKey, DamageReportSignal signal) {
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
