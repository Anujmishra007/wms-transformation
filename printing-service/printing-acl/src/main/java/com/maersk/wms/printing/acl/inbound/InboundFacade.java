package com.maersk.wms.printing.acl.inbound;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Anti-Corruption Layer facade for Inbound Service.
 * Provides receiving and ASN data for receiving label generation.
 */
public interface InboundFacade {

    /**
     * Get ASN details for receiving labels.
     */
    Optional<AsnLabelData> getAsnDetails(String asnKey, String warehouseKey);

    /**
     * Get receipt details for receipt labels.
     */
    Optional<ReceiptLabelData> getReceiptDetails(String receiptKey, String warehouseKey);

    /**
     * Get receiving LPN details for LPN labels.
     */
    Optional<ReceivingLpnLabelData> getReceivingLpnDetails(String lpnNumber, String warehouseKey);

    /**
     * Get PO details for PO labels.
     */
    Optional<PoLabelData> getPoDetails(String poKey, String warehouseKey);

    /**
     * Get all LPNs for a receipt.
     */
    List<ReceivingLpnLabelData> getLpnsForReceipt(String receiptKey, String warehouseKey);

    /**
     * Get putaway task details for putaway labels.
     */
    Optional<PutawayLabelData> getPutawayDetails(String taskKey, String warehouseKey);

    // DTOs for inbound data
    record AsnLabelData(
            String asnKey,
            String asnNumber,
            String externalAsnNumber,
            String storerKey,
            String storerName,
            String supplierCode,
            String supplierName,
            java.time.LocalDate expectedDate,
            int lineCount,
            int totalUnits,
            String status,
            Map<String, String> attributes
    ) {}

    record ReceiptLabelData(
            String receiptKey,
            String receiptNumber,
            String asnKey,
            String storerKey,
            java.time.Instant receivedAt,
            String receivedBy,
            String dockDoor,
            int lineCount,
            int lpnCount,
            Map<String, String> attributes
    ) {}

    record ReceivingLpnLabelData(
            String lpnNumber,
            String receiptKey,
            String asnKey,
            String skuCode,
            String skuDescription,
            String storerKey,
            double quantity,
            String uom,
            String lotNumber,
            java.time.LocalDate manufactureDate,
            java.time.LocalDate expiryDate,
            String putawayLocation,
            Map<String, String> attributes
    ) {}

    record PoLabelData(
            String poKey,
            String poNumber,
            String externalPoNumber,
            String storerKey,
            String supplierCode,
            String supplierName,
            java.time.LocalDate orderDate,
            java.time.LocalDate expectedDate,
            int lineCount,
            String status,
            Map<String, String> attributes
    ) {}

    record PutawayLabelData(
            String taskKey,
            String lpnNumber,
            String fromLocation,
            String toLocation,
            String skuCode,
            double quantity,
            String uom,
            int priority,
            Map<String, String> attributes
    ) {}
}
