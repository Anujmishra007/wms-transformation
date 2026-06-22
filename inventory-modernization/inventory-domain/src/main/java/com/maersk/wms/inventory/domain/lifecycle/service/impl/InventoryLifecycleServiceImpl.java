package com.maersk.wms.inventory.domain.lifecycle.service.impl;

import com.maersk.wms.inventory.domain.core.model.Inventory;
import com.maersk.wms.inventory.domain.core.model.InventoryTransaction;
import com.maersk.wms.inventory.domain.core.repository.InventoryRepository;
import com.maersk.wms.inventory.domain.core.repository.InventoryTransactionRepository;
import com.maersk.wms.inventory.domain.core.repository.InventorySnapshotRepository;
import com.maersk.wms.inventory.domain.core.model.InventorySnapshot;
import com.maersk.wms.inventory.domain.lifecycle.model.*;
import com.maersk.wms.inventory.domain.lifecycle.service.InventoryLifecycleService;
import com.maersk.wms.inventory.domain.events.InventoryLifecycleEvents;
import com.maersk.wms.inventory.shared.kernel.exceptions.*;
import com.maersk.wms.inventory.shared.kernel.identifiers.*;
import com.maersk.wms.inventory.shared.kernel.valueobjects.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service implementation for inventory lifecycle operations.
 * Handles Create, Change, Remove, and Finalization.
 */
@Service
@Transactional
public class InventoryLifecycleServiceImpl implements InventoryLifecycleService {

    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionRepository transactionRepository;
    private final InventorySnapshotRepository snapshotRepository;
    private final ApplicationEventPublisher eventPublisher;

    public InventoryLifecycleServiceImpl(InventoryRepository inventoryRepository,
                                          InventoryTransactionRepository transactionRepository,
                                          InventorySnapshotRepository snapshotRepository,
                                          ApplicationEventPublisher eventPublisher) {
        this.inventoryRepository = inventoryRepository;
        this.transactionRepository = transactionRepository;
        this.snapshotRepository = snapshotRepository;
        this.eventPublisher = eventPublisher;
    }

    // ═══════════════════════════════════════════════════════════════
    // CREATE OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    @Override
    public Inventory createFromReceipt(InventoryCreation creation) {
        validateCreation(creation);

        // Check if inventory already exists at this LOTxLOCxID
        inventoryRepository.findByCompositeKey(creation.lotKey(), creation.locationKey(), creation.lpnKey())
                .ifPresent(existing -> {
                    throw new InvalidInventoryOperationException(
                            "Inventory already exists at LOT/LOC/ID: " + creation.lotKey() + "/" +
                                    creation.locationKey() + "/" + creation.lpnKey());
                });

        Inventory inventory = Inventory.createFromReceipt(
                new InventoryKey(UUID.randomUUID().toString()),
                creation.skuKey(),
                creation.lotKey(),
                creation.locationKey(),
                creation.lpnKey(),
                creation.storerKey(),
                creation.warehouseKey(),
                creation.quantity(),
                creation.lottables()
        );

        inventory = inventoryRepository.save(inventory);

        // Record transaction
        recordCreateTransaction(inventory, creation);

        // Publish event
        eventPublisher.publishEvent(new InventoryLifecycleEvents.InventoryCreated(
                inventory.inventoryKey(),
                inventory.skuKey(),
                inventory.lotKey(),
                inventory.locationKey(),
                inventory.lpnKey(),
                inventory.storerKey(),
                inventory.warehouseKey(),
                inventory.onHandQuantity(),
                InventoryLifecycleEvents.CreationSource.RECEIPT,
                creation.receiptKey() != null ? creation.receiptKey().value() : null,
                creation.createdBy(),
                Instant.now()
        ));

        return inventory;
    }

    @Override
    public Inventory createFromReturn(InventoryCreation creation) {
        validateCreation(creation);

        Inventory inventory = Inventory.createFromReturn(
                new InventoryKey(UUID.randomUUID().toString()),
                creation.skuKey(),
                creation.lotKey(),
                creation.locationKey(),
                creation.lpnKey(),
                creation.storerKey(),
                creation.warehouseKey(),
                creation.quantity(),
                creation.lottables()
        );

        inventory = inventoryRepository.save(inventory);

        // Record transaction
        recordCreateTransaction(inventory, creation);

        // Publish event
        eventPublisher.publishEvent(new InventoryLifecycleEvents.InventoryCreated(
                inventory.inventoryKey(),
                inventory.skuKey(),
                inventory.lotKey(),
                inventory.locationKey(),
                inventory.lpnKey(),
                inventory.storerKey(),
                inventory.warehouseKey(),
                inventory.onHandQuantity(),
                InventoryLifecycleEvents.CreationSource.RETURN,
                creation.returnKey(),
                creation.createdBy(),
                Instant.now()
        ));

        return inventory;
    }

    @Override
    public Inventory createFromAdjustment(SkuKey skuKey, LotKey lotKey, LocationKey locationKey,
                                           LpnKey lpnKey, Quantity quantity, LottableAttributes lottables,
                                           String reason, UserKey createdBy, WarehouseKey warehouseKey) {
        Inventory inventory = Inventory.builder()
                .inventoryKey(new InventoryKey(UUID.randomUUID().toString()))
                .skuKey(skuKey)
                .lotKey(lotKey)
                .locationKey(locationKey)
                .lpnKey(lpnKey)
                .warehouseKey(warehouseKey)
                .onHandQuantity(quantity)
                .lottables(lottables)
                .status(Inventory.InventoryStatusCode.AVAILABLE)
                .build();

        inventory = inventoryRepository.save(inventory);

        // Record transaction
        InventoryTransaction transaction = InventoryTransaction.builder()
                .transactionKey(new TransactionKey(UUID.randomUUID().toString()))
                .inventoryKey(inventory.inventoryKey())
                .transactionType(InventoryTransaction.TransactionType.ADJUST)
                .quantity(quantity)
                .sourceType(InventoryTransaction.TransactionSource.ADJUSTMENT)
                .reason(reason)
                .performedBy(createdBy)
                .build();
        transactionRepository.save(transaction);

        // Publish event
        eventPublisher.publishEvent(new InventoryLifecycleEvents.InventoryCreated(
                inventory.inventoryKey(),
                skuKey,
                lotKey,
                locationKey,
                lpnKey,
                null,
                warehouseKey,
                quantity,
                InventoryLifecycleEvents.CreationSource.ADJUSTMENT,
                reason,
                createdBy,
                Instant.now()
        ));

        return inventory;
    }

    @Override
    public Inventory createFromCrossdock(InventoryCreation creation) {
        Inventory inventory = Inventory.builder()
                .inventoryKey(new InventoryKey(UUID.randomUUID().toString()))
                .skuKey(creation.skuKey())
                .lotKey(creation.lotKey())
                .locationKey(creation.locationKey())
                .lpnKey(creation.lpnKey())
                .storerKey(creation.storerKey())
                .warehouseKey(creation.warehouseKey())
                .onHandQuantity(creation.quantity())
                .lottables(creation.lottables())
                .status(Inventory.InventoryStatusCode.AVAILABLE)
                .build();

        inventory = inventoryRepository.save(inventory);
        recordCreateTransaction(inventory, creation);

        eventPublisher.publishEvent(new InventoryLifecycleEvents.InventoryCreated(
                inventory.inventoryKey(),
                inventory.skuKey(),
                inventory.lotKey(),
                inventory.locationKey(),
                inventory.lpnKey(),
                inventory.storerKey(),
                inventory.warehouseKey(),
                inventory.onHandQuantity(),
                InventoryLifecycleEvents.CreationSource.CROSSDOCK,
                null,
                creation.createdBy(),
                Instant.now()
        ));

        return inventory;
    }

    @Override
    public List<Inventory> createBatch(List<InventoryCreation> creations) {
        List<Inventory> inventories = new ArrayList<>();
        for (InventoryCreation creation : creations) {
            inventories.add(createFromReceipt(creation));
        }
        return inventories;
    }

    // ═══════════════════════════════════════════════════════════════
    // CHANGE OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    @Override
    public void updateQuantity(InventoryKey inventoryKey, Quantity newQuantity,
                                String reason, UserKey updatedBy) {
        Inventory inventory = inventoryRepository.findByKeyForUpdate(inventoryKey)
                .orElseThrow(() -> new InventoryNotFoundException(inventoryKey));

        Quantity previousQuantity = inventory.onHandQuantity();
        Quantity difference = newQuantity.subtract(previousQuantity);

        inventory.updateQuantity(newQuantity, reason);
        inventoryRepository.save(inventory);

        // Record transaction
        InventoryTransaction transaction = InventoryTransaction.builder()
                .transactionKey(new TransactionKey(UUID.randomUUID().toString()))
                .inventoryKey(inventoryKey)
                .transactionType(InventoryTransaction.TransactionType.ADJUST)
                .quantity(difference)
                .quantityBefore(previousQuantity)
                .quantityAfter(newQuantity)
                .sourceType(InventoryTransaction.TransactionSource.MANUAL)
                .reason(reason)
                .performedBy(updatedBy)
                .build();
        transactionRepository.save(transaction);

        // Publish event
        eventPublisher.publishEvent(new InventoryLifecycleEvents.InventoryQuantityChanged(
                inventoryKey,
                inventory.skuKey(),
                previousQuantity,
                newQuantity,
                difference,
                reason,
                updatedBy,
                Instant.now()
        ));
    }

    @Override
    public void adjustQuantity(InventoryKey inventoryKey, Quantity adjustment,
                                String adjustmentType, String reason, UserKey adjustedBy) {
        Inventory inventory = inventoryRepository.findByKeyForUpdate(inventoryKey)
                .orElseThrow(() -> new InventoryNotFoundException(inventoryKey));

        Quantity previousQuantity = inventory.onHandQuantity();
        Quantity newQuantity = previousQuantity.add(adjustment);

        if (newQuantity.isNegative()) {
            throw new InsufficientInventoryException(inventoryKey, adjustment.negate(), previousQuantity);
        }

        inventory.updateQuantity(newQuantity, reason);
        inventoryRepository.save(inventory);

        // Record transaction
        InventoryTransaction transaction = InventoryTransaction.builder()
                .transactionKey(new TransactionKey(UUID.randomUUID().toString()))
                .inventoryKey(inventoryKey)
                .transactionType(InventoryTransaction.TransactionType.ADJUST)
                .quantity(adjustment)
                .quantityBefore(previousQuantity)
                .quantityAfter(newQuantity)
                .sourceType(InventoryTransaction.TransactionSource.ADJUSTMENT)
                .sourceKey(adjustmentType)
                .reason(reason)
                .performedBy(adjustedBy)
                .build();
        transactionRepository.save(transaction);
    }

    @Override
    public void updateStatus(InventoryChange change) {
        Inventory inventory = inventoryRepository.findByKeyForUpdate(change.inventoryKey())
                .orElseThrow(() -> new InventoryNotFoundException(change.inventoryKey()));

        inventory.changeStatus(change.newStatus(), change.reason());
        inventoryRepository.save(inventory);

        // Record transaction
        InventoryTransaction transaction = InventoryTransaction.builder()
                .transactionKey(new TransactionKey(UUID.randomUUID().toString()))
                .inventoryKey(change.inventoryKey())
                .transactionType(InventoryTransaction.TransactionType.STATUS_CHANGE)
                .quantity(inventory.onHandQuantity())
                .sourceType(InventoryTransaction.TransactionSource.MANUAL)
                .reason(change.reason())
                .performedBy(change.changedBy())
                .build();
        transactionRepository.save(transaction);
    }

    @Override
    public void updateLottables(InventoryKey inventoryKey, LottableAttributes newLottables,
                                 String reason, UserKey updatedBy) {
        Inventory inventory = inventoryRepository.findByKeyForUpdate(inventoryKey)
                .orElseThrow(() -> new InventoryNotFoundException(inventoryKey));

        inventory.updateLottables(newLottables, reason);
        inventoryRepository.save(inventory);

        // Record transaction
        InventoryTransaction transaction = InventoryTransaction.builder()
                .transactionKey(new TransactionKey(UUID.randomUUID().toString()))
                .inventoryKey(inventoryKey)
                .transactionType(InventoryTransaction.TransactionType.ATTRIBUTE_CHANGE)
                .quantity(inventory.onHandQuantity())
                .sourceType(InventoryTransaction.TransactionSource.MANUAL)
                .reason(reason)
                .performedBy(updatedBy)
                .build();
        transactionRepository.save(transaction);
    }

    @Override
    public void changeOwnership(InventoryKey inventoryKey, StorerKey newStorerKey,
                                 String reason, UserKey changedBy) {
        Inventory inventory = inventoryRepository.findByKeyForUpdate(inventoryKey)
                .orElseThrow(() -> new InventoryNotFoundException(inventoryKey));

        StorerKey previousStorer = inventory.storerKey();
        inventory.changeOwnership(newStorerKey, reason);
        inventoryRepository.save(inventory);

        // Record transaction
        InventoryTransaction transaction = InventoryTransaction.builder()
                .transactionKey(new TransactionKey(UUID.randomUUID().toString()))
                .inventoryKey(inventoryKey)
                .transactionType(InventoryTransaction.TransactionType.OWNERSHIP_CHANGE)
                .quantity(inventory.onHandQuantity())
                .sourceType(InventoryTransaction.TransactionSource.MANUAL)
                .reason(reason + " [" + previousStorer + " -> " + newStorerKey + "]")
                .performedBy(changedBy)
                .build();
        transactionRepository.save(transaction);
    }

    @Override
    public void applyHold(InventoryKey inventoryKey, String holdCode, String reason, UserKey appliedBy) {
        Inventory inventory = inventoryRepository.findByKeyForUpdate(inventoryKey)
                .orElseThrow(() -> new InventoryNotFoundException(inventoryKey));

        inventory.applyHold(holdCode, reason);
        inventoryRepository.save(inventory);

        // Record transaction
        InventoryTransaction transaction = InventoryTransaction.builder()
                .transactionKey(new TransactionKey(UUID.randomUUID().toString()))
                .inventoryKey(inventoryKey)
                .transactionType(InventoryTransaction.TransactionType.HOLD)
                .quantity(inventory.onHandQuantity())
                .sourceType(InventoryTransaction.TransactionSource.MANUAL)
                .referenceKey(holdCode)
                .reason(reason)
                .performedBy(appliedBy)
                .build();
        transactionRepository.save(transaction);
    }

    @Override
    public void releaseHold(InventoryKey inventoryKey, String reason, UserKey releasedBy) {
        Inventory inventory = inventoryRepository.findByKeyForUpdate(inventoryKey)
                .orElseThrow(() -> new InventoryNotFoundException(inventoryKey));

        String previousHoldCode = inventory.holdCode();
        inventory.releaseHold(reason);
        inventoryRepository.save(inventory);

        // Record transaction
        InventoryTransaction transaction = InventoryTransaction.builder()
                .transactionKey(new TransactionKey(UUID.randomUUID().toString()))
                .inventoryKey(inventoryKey)
                .transactionType(InventoryTransaction.TransactionType.RELEASE_HOLD)
                .quantity(inventory.onHandQuantity())
                .sourceType(InventoryTransaction.TransactionSource.MANUAL)
                .referenceKey(previousHoldCode)
                .reason(reason)
                .performedBy(releasedBy)
                .build();
        transactionRepository.save(transaction);
    }

    // ═══════════════════════════════════════════════════════════════
    // REMOVE OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    @Override
    public void remove(InventoryRemoval removal) {
        Inventory inventory = inventoryRepository.findByKeyForUpdate(removal.inventoryKey())
                .orElseThrow(() -> new InventoryNotFoundException(removal.inventoryKey()));

        Quantity previousQuantity = inventory.onHandQuantity();

        if (removal.quantity().isGreaterThan(previousQuantity)) {
            throw new InsufficientInventoryException(removal.inventoryKey(), removal.quantity(), previousQuantity);
        }

        Quantity newQuantity = previousQuantity.subtract(removal.quantity());
        inventory.updateQuantity(newQuantity, removal.reason());
        inventoryRepository.save(inventory);

        // Record transaction
        InventoryTransaction transaction = InventoryTransaction.builder()
                .transactionKey(new TransactionKey(UUID.randomUUID().toString()))
                .inventoryKey(removal.inventoryKey())
                .transactionType(InventoryTransaction.TransactionType.REMOVE)
                .quantity(removal.quantity().negate())
                .quantityBefore(previousQuantity)
                .quantityAfter(newQuantity)
                .sourceType(removal.removalSource())
                .sourceKey(removal.sourceKey())
                .reason(removal.reason())
                .performedBy(removal.removedBy())
                .build();
        transactionRepository.save(transaction);

        // Publish event
        eventPublisher.publishEvent(new InventoryLifecycleEvents.InventoryDepleted(
                removal.inventoryKey(),
                inventory.skuKey(),
                inventory.locationKey(),
                removal.quantity(),
                newQuantity,
                removal.reason(),
                removal.removedBy(),
                Instant.now()
        ));

        // Clean up zero quantity records
        if (newQuantity.isZero()) {
            inventoryRepository.delete(inventory);
        }
    }

    @Override
    public void pick(InventoryKey inventoryKey, AllocationKey allocationKey, Quantity quantity,
                      UserKey pickedBy) {
        Inventory inventory = inventoryRepository.findByKeyForUpdate(inventoryKey)
                .orElseThrow(() -> new InventoryNotFoundException(inventoryKey));

        inventory.pick(quantity, allocationKey);
        inventoryRepository.save(inventory);

        // Record transaction
        InventoryTransaction transaction = InventoryTransaction.builder()
                .transactionKey(new TransactionKey(UUID.randomUUID().toString()))
                .inventoryKey(inventoryKey)
                .transactionType(InventoryTransaction.TransactionType.PICK)
                .quantity(quantity.negate())
                .sourceType(InventoryTransaction.TransactionSource.ORDER)
                .sourceKey(allocationKey.value())
                .performedBy(pickedBy)
                .build();
        transactionRepository.save(transaction);
    }

    @Override
    public void confirmShipment(List<InventoryKey> inventoryKeys, String shipmentKey,
                                 UserKey confirmedBy) {
        for (InventoryKey inventoryKey : inventoryKeys) {
            Inventory inventory = inventoryRepository.findByKeyForUpdate(inventoryKey)
                    .orElseThrow(() -> new InventoryNotFoundException(inventoryKey));

            Quantity shippedQuantity = inventory.pickedQuantity();
            inventory.confirmShipment(shippedQuantity);
            inventoryRepository.save(inventory);

            // Record transaction
            InventoryTransaction transaction = InventoryTransaction.builder()
                    .transactionKey(new TransactionKey(UUID.randomUUID().toString()))
                    .inventoryKey(inventoryKey)
                    .transactionType(InventoryTransaction.TransactionType.SHIP)
                    .quantity(shippedQuantity.negate())
                    .sourceType(InventoryTransaction.TransactionSource.SHIPMENT)
                    .sourceKey(shipmentKey)
                    .performedBy(confirmedBy)
                    .build();
            transactionRepository.save(transaction);

            // Publish event
            eventPublisher.publishEvent(new InventoryLifecycleEvents.InventoryShipped(
                    inventoryKey,
                    inventory.skuKey(),
                    inventory.locationKey(),
                    shippedQuantity,
                    shipmentKey,
                    confirmedBy,
                    Instant.now()
            ));
        }
    }

    @Override
    public void writeOff(InventoryKey inventoryKey, String writeOffReason, UserKey writtenOffBy) {
        Inventory inventory = inventoryRepository.findByKeyForUpdate(inventoryKey)
                .orElseThrow(() -> new InventoryNotFoundException(inventoryKey));

        Quantity writtenOffQuantity = inventory.onHandQuantity();
        inventory.updateQuantity(Quantity.zero(writtenOffQuantity.uom()), writeOffReason);
        inventoryRepository.save(inventory);

        // Record transaction
        InventoryTransaction transaction = InventoryTransaction.builder()
                .transactionKey(new TransactionKey(UUID.randomUUID().toString()))
                .inventoryKey(inventoryKey)
                .transactionType(InventoryTransaction.TransactionType.WRITE_OFF)
                .quantity(writtenOffQuantity.negate())
                .sourceType(InventoryTransaction.TransactionSource.MANUAL)
                .reason(writeOffReason)
                .performedBy(writtenOffBy)
                .build();
        transactionRepository.save(transaction);

        // Delete the record
        inventoryRepository.delete(inventory);
    }

    @Override
    public void delete(InventoryKey inventoryKey, String reason, UserKey deletedBy) {
        Inventory inventory = inventoryRepository.findByKeyForUpdate(inventoryKey)
                .orElseThrow(() -> new InventoryNotFoundException(inventoryKey));

        if (!inventory.onHandQuantity().isZero()) {
            throw new InvalidInventoryOperationException(
                    "Cannot delete inventory with non-zero quantity: " + inventory.onHandQuantity());
        }

        // Record transaction before delete
        InventoryTransaction transaction = InventoryTransaction.builder()
                .transactionKey(new TransactionKey(UUID.randomUUID().toString()))
                .inventoryKey(inventoryKey)
                .transactionType(InventoryTransaction.TransactionType.DELETE)
                .quantity(Quantity.zero(inventory.onHandQuantity().uom()))
                .sourceType(InventoryTransaction.TransactionSource.MANUAL)
                .reason(reason)
                .performedBy(deletedBy)
                .build();
        transactionRepository.save(transaction);

        inventoryRepository.delete(inventory);
    }

    // ═══════════════════════════════════════════════════════════════
    // FINALIZATION OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    @Override
    public void finalizeTransaction(InventoryFinalization finalization) {
        // Create snapshot of current state
        for (InventoryKey inventoryKey : finalization.inventoryKeys()) {
            Inventory inventory = inventoryRepository.findByKey(inventoryKey)
                    .orElseThrow(() -> new InventoryNotFoundException(inventoryKey));

            InventorySnapshot snapshot = InventorySnapshot.fromInventory(
                    new SnapshotKey(UUID.randomUUID().toString()),
                    inventory,
                    finalization.period(),
                    finalization.snapshotType()
            );
            snapshotRepository.save(snapshot);
        }

        // Record finalization transaction
        InventoryTransaction transaction = InventoryTransaction.builder()
                .transactionKey(new TransactionKey(UUID.randomUUID().toString()))
                .transactionType(InventoryTransaction.TransactionType.FINALIZE)
                .sourceType(InventoryTransaction.TransactionSource.SYSTEM)
                .sourceKey(finalization.period())
                .reason(finalization.notes())
                .performedBy(finalization.finalizedBy())
                .build();
        transactionRepository.save(transaction);
    }

    @Override
    public InventoryFinalization reconcileLocation(LocationKey locationKey, WarehouseKey warehouseKey,
                                                    UserKey reconciledBy) {
        List<Inventory> inventoryAtLocation = inventoryRepository.findByLocation(locationKey, warehouseKey);

        List<InventoryKey> inventoryKeys = inventoryAtLocation.stream()
                .map(Inventory::inventoryKey)
                .toList();

        InventoryFinalization finalization = new InventoryFinalization(
                inventoryKeys,
                "RECONCILE-" + locationKey.value(),
                InventorySnapshot.SnapshotType.RECONCILIATION,
                "Location reconciliation",
                reconciledBy
        );

        finalizeTransaction(finalization);
        return finalization;
    }

    @Override
    public InventoryFinalization periodEndClose(WarehouseKey warehouseKey, String period, UserKey closedBy) {
        // This would typically capture all inventory in the warehouse
        // For performance, this might be done in batches
        List<InventoryKey> allInventoryKeys = new ArrayList<>(); // Simplified

        InventoryFinalization finalization = new InventoryFinalization(
                allInventoryKeys,
                period,
                InventorySnapshot.SnapshotType.PERIOD_END,
                "Period end close: " + period,
                closedBy
        );

        finalizeTransaction(finalization);
        return finalization;
    }

    // ═══════════════════════════════════════════════════════════════
    // HELPER METHODS
    // ═══════════════════════════════════════════════════════════════

    private void validateCreation(InventoryCreation creation) {
        if (creation.skuKey() == null) {
            throw new InvalidInventoryOperationException("SKU is required");
        }
        if (creation.locationKey() == null) {
            throw new InvalidInventoryOperationException("Location is required");
        }
        if (creation.quantity() == null || creation.quantity().isNegative()) {
            throw new InvalidInventoryOperationException("Valid quantity is required");
        }
    }

    private void recordCreateTransaction(Inventory inventory, InventoryCreation creation) {
        InventoryTransaction transaction = InventoryTransaction.builder()
                .transactionKey(new TransactionKey(UUID.randomUUID().toString()))
                .inventoryKey(inventory.inventoryKey())
                .transactionType(InventoryTransaction.TransactionType.CREATE)
                .quantity(inventory.onHandQuantity())
                .sourceType(creation.receiptKey() != null ?
                        InventoryTransaction.TransactionSource.RECEIPT :
                        InventoryTransaction.TransactionSource.MANUAL)
                .sourceKey(creation.receiptKey() != null ? creation.receiptKey().value() : null)
                .performedBy(creation.createdBy())
                .build();
        transactionRepository.save(transaction);
    }
}
