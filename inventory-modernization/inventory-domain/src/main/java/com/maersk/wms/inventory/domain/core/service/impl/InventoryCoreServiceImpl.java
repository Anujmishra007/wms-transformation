package com.maersk.wms.inventory.domain.core.service.impl;

import com.maersk.wms.inventory.domain.core.model.Inventory;
import com.maersk.wms.inventory.domain.core.model.InventoryTransaction;
import com.maersk.wms.inventory.domain.core.repository.InventoryRepository;
import com.maersk.wms.inventory.domain.core.repository.InventoryTransactionRepository;
import com.maersk.wms.inventory.domain.core.service.InventoryCoreService;
import com.maersk.wms.inventory.domain.events.AllocationEvents;
import com.maersk.wms.inventory.domain.events.HoldEvents;
import com.maersk.wms.inventory.shared.kernel.exceptions.*;
import com.maersk.wms.inventory.shared.kernel.identifiers.*;
import com.maersk.wms.inventory.shared.kernel.valueobjects.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Core service implementation for inventory operations.
 * Central inventory management including queries, allocation, and status operations.
 */
@Service
@Transactional
public class InventoryCoreServiceImpl implements InventoryCoreService {

    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionRepository transactionRepository;
    private final ApplicationEventPublisher eventPublisher;

    public InventoryCoreServiceImpl(InventoryRepository inventoryRepository,
                                     InventoryTransactionRepository transactionRepository,
                                     ApplicationEventPublisher eventPublisher) {
        this.inventoryRepository = inventoryRepository;
        this.transactionRepository = transactionRepository;
        this.eventPublisher = eventPublisher;
    }

    // ═══════════════════════════════════════════════════════════════
    // QUERY OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public Optional<Inventory> findByKey(InventoryKey inventoryKey) {
        return inventoryRepository.findByKey(inventoryKey);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Inventory> findByCompositeKey(LotKey lot, LocationKey location, LpnKey lpn) {
        return inventoryRepository.findByCompositeKey(lot, location, lpn);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventory> findBySku(SkuKey skuKey, WarehouseKey warehouseKey) {
        return inventoryRepository.findBySku(skuKey, warehouseKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventory> findByLocation(LocationKey locationKey, WarehouseKey warehouseKey) {
        return inventoryRepository.findByLocation(locationKey, warehouseKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventory> findByLpn(LpnKey lpnKey, WarehouseKey warehouseKey) {
        return inventoryRepository.findByLpn(lpnKey, warehouseKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventory> findByLot(LotKey lotKey, WarehouseKey warehouseKey) {
        return inventoryRepository.findByLot(lotKey, warehouseKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventory> findByStorer(StorerKey storerKey, WarehouseKey warehouseKey) {
        return inventoryRepository.findByStorer(storerKey, warehouseKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventory> findAvailableInventory(SkuKey skuKey, WarehouseKey warehouseKey) {
        return inventoryRepository.findAvailable(skuKey, warehouseKey);
    }

    // ═══════════════════════════════════════════════════════════════
    // QUANTITY AGGREGATIONS
    // ═══════════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public Quantity getOnHandQuantity(SkuKey skuKey, WarehouseKey warehouseKey) {
        return inventoryRepository.sumOnHandQuantity(skuKey, warehouseKey);
    }

    @Override
    @Transactional(readOnly = true)
    public Quantity getAvailableQuantity(SkuKey skuKey, WarehouseKey warehouseKey) {
        return inventoryRepository.sumAvailableQuantity(skuKey, warehouseKey);
    }

    @Override
    @Transactional(readOnly = true)
    public Quantity getAllocatedQuantity(SkuKey skuKey, WarehouseKey warehouseKey) {
        return inventoryRepository.sumAllocatedQuantity(skuKey, warehouseKey);
    }

    @Override
    @Transactional(readOnly = true)
    public Quantity getOnHandQuantityAtLocation(SkuKey skuKey, LocationKey locationKey) {
        return inventoryRepository.sumOnHandQuantityAtLocation(skuKey, locationKey);
    }

    @Override
    @Transactional(readOnly = true)
    public Quantity getTotalOnHandForStorer(StorerKey storerKey, WarehouseKey warehouseKey) {
        return inventoryRepository.sumOnHandForStorer(storerKey, warehouseKey);
    }

    // ═══════════════════════════════════════════════════════════════
    // ALLOCATION OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    @Override
    public AllocationResult allocateInventory(AllocationCriteria criteria) {
        // Find available inventory based on FIFO strategy
        List<Inventory> availableInventory = findInventoryForAllocation(criteria);

        if (availableInventory.isEmpty()) {
            publishAllocationShortage(criteria);
            return AllocationResult.failed(criteria.requiredQuantity(), "No inventory available for allocation");
        }

        List<AllocationResult.AllocationLine> allocationLines = new ArrayList<>();
        Quantity remainingQuantity = criteria.requiredQuantity();
        AllocationKey allocationKey = new AllocationKey(UUID.randomUUID().toString());

        for (Inventory inventory : availableInventory) {
            if (remainingQuantity.isZeroOrNegative()) {
                break;
            }

            // Lock inventory for allocation
            Inventory lockedInventory = inventoryRepository.findByKeyForUpdate(inventory.inventoryKey())
                    .orElseThrow(() -> new InventoryNotFoundException(inventory.inventoryKey()));

            Quantity availableQty = lockedInventory.availableQuantity();
            Quantity allocateQty = availableQty.min(remainingQuantity);

            // Allocate inventory
            lockedInventory.allocate(allocateQty, criteria.orderKey(), allocationKey);
            inventoryRepository.save(lockedInventory);

            // Record transaction
            recordAllocationTransaction(lockedInventory, allocateQty, criteria.orderKey(), allocationKey);

            // Use AllocationLine.of() factory method which handles parameter order correctly
            allocationLines.add(AllocationResult.AllocationLine.of(
                    lockedInventory.inventoryKey(),
                    lockedInventory.locationKey(),
                    lockedInventory.lotKey(),
                    lockedInventory.lpnKey(),
                    allocateQty
            ));

            remainingQuantity = remainingQuantity.subtract(allocateQty);
        }

        Quantity allocatedQuantity = criteria.requiredQuantity().subtract(remainingQuantity);
        boolean fullyAllocated = !remainingQuantity.isPositive();
        Quantity shortQty = fullyAllocated ? Quantity.zero(criteria.requiredQuantity().uom()) : remainingQuantity;

        AllocationResult result;
        if (fullyAllocated) {
            result = AllocationResult.success(allocationKey, criteria.skuKey(), criteria.orderKey(),
                    criteria.requiredQuantity(), allocationLines);
        } else {
            result = AllocationResult.partial(allocationKey, criteria.skuKey(), criteria.orderKey(),
                    criteria.requiredQuantity(), allocatedQuantity, allocationLines);
        }

        // Publish events
        if (result.fullyAllocated()) {
            publishAllocationComplete(result, criteria.warehouseKey(), criteria.storerKey());
        } else {
            publishAllocationPartial(result, criteria.warehouseKey());
        }

        return result;
    }

    private List<Inventory> findInventoryForAllocation(AllocationCriteria criteria) {
        return switch (criteria.fifoStrategy()) {
            case FIFO_RECEIPT_DATE -> inventoryRepository.findBySkuOrderByFifo(criteria.skuKey(), criteria.warehouseKey());
            case FIFO_EXPIRY_DATE -> inventoryRepository.findBySkuOrderByExpiry(criteria.skuKey(), criteria.warehouseKey());
            case LIFO_RECEIPT_DATE -> inventoryRepository.findBySkuOrderByLot(criteria.skuKey(), criteria.warehouseKey(), true);
            default -> inventoryRepository.findAvailable(criteria.skuKey(), criteria.warehouseKey());
        };
    }

    @Override
    public void deallocateInventory(AllocationKey allocationKey, String reason, UserKey deallocatedBy) {
        List<Inventory> allocatedInventory = inventoryRepository.findAllocatedForOrder(
                new OrderKey(allocationKey.value())); // Simplified lookup

        Quantity totalDeallocated = Quantity.ZERO;
        SkuKey skuKey = null;
        OrderKey orderKey = new OrderKey(allocationKey.value());
        WarehouseKey warehouseKey = null;

        for (Inventory inventory : allocatedInventory) {
            Inventory lockedInventory = inventoryRepository.findByKeyForUpdate(inventory.inventoryKey())
                    .orElseThrow(() -> new InventoryNotFoundException(inventory.inventoryKey()));

            Quantity deallocatedQty = lockedInventory.allocatedQuantity();
            lockedInventory.deallocate(deallocatedQty, reason);
            inventoryRepository.save(lockedInventory);

            totalDeallocated = totalDeallocated.add(deallocatedQty);
            if (skuKey == null) skuKey = lockedInventory.skuKey();
            if (warehouseKey == null) warehouseKey = lockedInventory.warehouseKey();

            // Record transaction
            recordDeallocationTransaction(lockedInventory, deallocatedQty, allocationKey, reason, deallocatedBy);
        }

        // Publish deallocation event with correct signature
        eventPublisher.publishEvent(new AllocationEvents.InventoryDeallocated(
                allocationKey,
                orderKey,
                skuKey,
                totalDeallocated,
                reason,
                warehouseKey,
                Instant.now()
        ));
    }

    @Override
    public AllocationResult reallocateInventory(AllocationKey existingAllocation, AllocationCriteria newCriteria) {
        // First deallocate existing
        deallocateInventory(existingAllocation, "Reallocation", newCriteria.allocatedBy());

        // Then allocate with new criteria
        return allocateInventory(newCriteria);
    }

    // ═══════════════════════════════════════════════════════════════
    // STATUS OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    @Override
    public void applyHold(InventoryKey inventoryKey, String holdCode, String reason, UserKey appliedBy) {
        Inventory inventory = inventoryRepository.findByKeyForUpdate(inventoryKey)
                .orElseThrow(() -> new InventoryNotFoundException(inventoryKey));

        if (inventory.isOnHold()) {
            throw new InventoryOnHoldException(inventoryKey, inventory.holdCode());
        }

        inventory.applyHold(holdCode, reason);
        inventoryRepository.save(inventory);

        // Record transaction
        recordHoldTransaction(inventory, holdCode, reason, appliedBy, true);

        // Publish event with correct signature
        eventPublisher.publishEvent(new HoldEvents.HoldApplied(
                inventoryKey,
                holdCode,
                reason,
                appliedBy,
                inventory.warehouseKey(),
                Instant.now()
        ));
    }

    @Override
    public void releaseHold(InventoryKey inventoryKey, String reason, UserKey releasedBy) {
        Inventory inventory = inventoryRepository.findByKeyForUpdate(inventoryKey)
                .orElseThrow(() -> new InventoryNotFoundException(inventoryKey));

        if (!inventory.isOnHold()) {
            throw new InvalidInventoryOperationException("Inventory is not on hold");
        }

        String previousHoldCode = inventory.holdCode();
        inventory.releaseHold(reason);
        inventoryRepository.save(inventory);

        // Record transaction
        recordHoldTransaction(inventory, previousHoldCode, reason, releasedBy, false);

        // Publish event with correct signature
        eventPublisher.publishEvent(new HoldEvents.HoldReleased(
                inventoryKey,
                previousHoldCode,
                reason,
                releasedBy,
                inventory.warehouseKey(),
                Instant.now()
        ));
    }

    @Override
    public void changeStatus(InventoryKey inventoryKey, Inventory.InventoryStatusCode newStatus,
                              String reason, UserKey changedBy) {
        Inventory inventory = inventoryRepository.findByKeyForUpdate(inventoryKey)
                .orElseThrow(() -> new InventoryNotFoundException(inventoryKey));

        Inventory.InventoryStatusCode previousStatus = inventory.status();
        inventory.changeStatus(newStatus, reason);
        inventoryRepository.save(inventory);

        // Record transaction
        recordStatusChangeTransaction(inventory, previousStatus, newStatus, reason, changedBy);
    }

    // ═══════════════════════════════════════════════════════════════
    // TRANSACTION HISTORY
    // ═══════════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public List<InventoryTransaction> getTransactionHistory(InventoryKey inventoryKey) {
        return transactionRepository.findByInventoryKey(inventoryKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryTransaction> getTransactionsBySource(String sourceType, String sourceKey) {
        return transactionRepository.findBySource(
                InventoryTransaction.TransactionSource.valueOf(sourceType),
                sourceKey, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryTransaction> getRecentTransactions(WarehouseKey warehouseKey, int limit) {
        return transactionRepository.findRecent(warehouseKey, limit);
    }

    // ═══════════════════════════════════════════════════════════════
    // HELPER METHODS
    // ═══════════════════════════════════════════════════════════════

    private void recordAllocationTransaction(Inventory inventory, Quantity quantity,
                                              OrderKey orderKey, AllocationKey allocationKey) {
        InventoryTransaction transaction = InventoryTransaction.builder()
                .transactionKey(new TransactionKey(UUID.randomUUID().toString()))
                .inventoryKey(inventory.inventoryKey())
                .transactionType(InventoryTransaction.TransactionType.ALLOCATE)
                .quantity(quantity)
                .sourceType(InventoryTransaction.TransactionSource.ORDER)
                .sourceKey(orderKey != null ? orderKey.value() : null)
                .referenceKey(allocationKey.value())
                .build();
        transactionRepository.save(transaction);
    }

    private void recordDeallocationTransaction(Inventory inventory, Quantity quantity,
                                                AllocationKey allocationKey, String reason, UserKey user) {
        InventoryTransaction transaction = InventoryTransaction.builder()
                .transactionKey(new TransactionKey(UUID.randomUUID().toString()))
                .inventoryKey(inventory.inventoryKey())
                .transactionType(InventoryTransaction.TransactionType.DEALLOCATE)
                .quantity(quantity)
                .sourceType(InventoryTransaction.TransactionSource.ORDER)
                .sourceKey(allocationKey.value())
                .reason(reason)
                .performedBy(user)
                .build();
        transactionRepository.save(transaction);
    }

    private void recordHoldTransaction(Inventory inventory, String holdCode, String reason,
                                        UserKey user, boolean isApply) {
        InventoryTransaction transaction = InventoryTransaction.builder()
                .transactionKey(new TransactionKey(UUID.randomUUID().toString()))
                .inventoryKey(inventory.inventoryKey())
                .transactionType(isApply ? InventoryTransaction.TransactionType.HOLD :
                        InventoryTransaction.TransactionType.RELEASE_HOLD)
                .quantity(inventory.onHandQuantity())
                .sourceType(InventoryTransaction.TransactionSource.MANUAL)
                .referenceKey(holdCode)
                .reason(reason)
                .performedBy(user)
                .build();
        transactionRepository.save(transaction);
    }

    private void recordStatusChangeTransaction(Inventory inventory,
                                                Inventory.InventoryStatusCode fromStatus,
                                                Inventory.InventoryStatusCode toStatus,
                                                String reason, UserKey user) {
        InventoryTransaction transaction = InventoryTransaction.builder()
                .transactionKey(new TransactionKey(UUID.randomUUID().toString()))
                .inventoryKey(inventory.inventoryKey())
                .transactionType(InventoryTransaction.TransactionType.STATUS_CHANGE)
                .quantity(inventory.onHandQuantity())
                .sourceType(InventoryTransaction.TransactionSource.MANUAL)
                .reason(reason + " [" + fromStatus + " -> " + toStatus + "]")
                .performedBy(user)
                .build();
        transactionRepository.save(transaction);
    }

    private void publishAllocationComplete(AllocationResult result, WarehouseKey warehouseKey, StorerKey storerKey) {
        // Convert AllocationResult.AllocationLine to AllocationEvents.InventoryAllocated.AllocationLine
        List<AllocationEvents.InventoryAllocated.AllocationLine> eventLines = result.allocationLines().stream()
                .map(line -> new AllocationEvents.InventoryAllocated.AllocationLine(
                        line.inventoryKey(),
                        line.lotKey(),
                        line.locationKey(),
                        line.lpnKey(),
                        line.quantity()
                ))
                .toList();

        eventPublisher.publishEvent(new AllocationEvents.InventoryAllocated(
                result.allocationKey(),
                result.orderKey(),
                null, // orderLineNumber
                result.skuKey(),
                storerKey,
                result.allocatedQuantity(),
                eventLines,
                warehouseKey,
                Instant.now()
        ));
    }

    private void publishAllocationPartial(AllocationResult result, WarehouseKey warehouseKey) {
        eventPublisher.publishEvent(new AllocationEvents.AllocationShortage(
                result.skuKey(),
                result.orderKey(),
                result.requestedQuantity(),
                result.allocatedQuantity(),
                result.shortageQuantity(),
                warehouseKey,
                Instant.now()
        ));
    }

    private void publishAllocationShortage(AllocationCriteria criteria) {
        eventPublisher.publishEvent(new AllocationEvents.AllocationShortage(
                criteria.skuKey(),
                criteria.orderKey(),
                criteria.requiredQuantity(),
                Quantity.zero(criteria.requiredQuantity().uom()),
                criteria.requiredQuantity(),
                criteria.warehouseKey(),
                Instant.now()
        ));
    }
}
