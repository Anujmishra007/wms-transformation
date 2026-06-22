package com.maersk.wms.inbound.service.operations_service;

import com.maersk.wms.inbound.domain.operations_service.*;
import com.maersk.wms.inbound.domain.operations_service.repository.CrossdockRepository;
import com.maersk.wms.inbound.service.operations_service.dto.*;
import com.maersk.wms.inbound.shared.kernel.identifiers.*;
import com.maersk.wms.inbound.shared.kernel.valueobjects.Quantity;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service for Crossdock task execution.
 * Part of inbound-operations-service subdomain (operations/).
 *
 * Responsibilities:
 * - Create crossdock tasks when demand matches
 * - Execute crossdock picking
 * - Manage staging and loading
 * - Track crossdock completion
 * - Coordinate with outbound
 */
@Service
@Transactional
public class CrossdockService {

    private final CrossdockRepository crossdockRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CrossdockService(CrossdockRepository crossdockRepository,
                            ApplicationEventPublisher eventPublisher) {
        this.crossdockRepository = crossdockRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Create crossdock for received inventory.
     */
    public Crossdock createCrossdock(CreateCrossdockRequest request) {
        validateCreateRequest(request);

        Crossdock crossdock = Crossdock.builder()
            .storerKey(new StorerKey(request.getStorerKey()))
            .receiptKey(new ReceiptKey(request.getReceiptKey()))
            .type(request.getCrossdockType())
            .skuKey(new SkuKey(request.getStorerKey(), request.getSku()))
            .inboundLpn(request.getInboundLpn() != null ? new LpnKey(request.getInboundLpn()) : null)
            .allocatedQty(new Quantity(request.getQuantity(), request.getUom()))
            .orderKey(request.getOrderKey())
            .waveKey(request.getWaveKey())
            .priority(request.getPriority())
            .opportunistic(request.isOpportunistic())
            .planned(request.isPlanned())
            .build();

        Crossdock saved = crossdockRepository.save(crossdock);

        eventPublisher.publishEvent(new CrossdockCreatedEvent(saved.getCrossdockKey()));
        return saved;
    }

    /**
     * Get crossdock by key.
     */
    @Transactional(readOnly = true)
    public Optional<Crossdock> getCrossdock(String crossdockKey) {
        return crossdockRepository.findByKey(crossdockKey);
    }

    /**
     * Get crossdocks by receipt.
     */
    @Transactional(readOnly = true)
    public List<Crossdock> getByReceipt(ReceiptKey receiptKey) {
        return crossdockRepository.findByReceiptKey(receiptKey);
    }

    /**
     * Get crossdocks by status.
     */
    @Transactional(readOnly = true)
    public List<Crossdock> getByStatus(CrossdockStatus status) {
        return crossdockRepository.findByStatus(status);
    }

    /**
     * Get crossdocks ready to pick.
     */
    @Transactional(readOnly = true)
    public List<Crossdock> getReadyToPick() {
        return crossdockRepository.findReadyToPick();
    }

    /**
     * Get crossdocks ready to ship.
     */
    @Transactional(readOnly = true)
    public List<Crossdock> getReadyToShip() {
        return crossdockRepository.findReadyToShip();
    }

    /**
     * Get pending crossdock allocations.
     */
    @Transactional(readOnly = true)
    public List<Crossdock> getPendingAllocation() {
        return crossdockRepository.findPendingAllocation();
    }

    /**
     * Allocate crossdock to order.
     */
    public Crossdock allocateToOrder(String crossdockKey, String orderKey, BigDecimal quantity) {
        Crossdock crossdock = crossdockRepository.findByKey(crossdockKey)
            .orElseThrow(() -> new IllegalArgumentException("Crossdock not found: " + crossdockKey));

        crossdock.allocate(orderKey, new Quantity(quantity, "EA"));
        Crossdock saved = crossdockRepository.save(crossdock);

        eventPublisher.publishEvent(new CrossdockAllocatedEvent(crossdockKey, orderKey));
        return saved;
    }

    /**
     * Release crossdock for picking.
     */
    public Crossdock releaseForPicking(String crossdockKey) {
        Crossdock crossdock = crossdockRepository.findByKey(crossdockKey)
            .orElseThrow(() -> new IllegalArgumentException("Crossdock not found: " + crossdockKey));

        crossdock.release();
        Crossdock saved = crossdockRepository.save(crossdock);

        eventPublisher.publishEvent(new CrossdockReleasedEvent(crossdockKey));
        return saved;
    }

    /**
     * Pick crossdock inventory.
     */
    public CrossdockPickResult pick(String crossdockKey, CrossdockPickRequest request) {
        Crossdock crossdock = crossdockRepository.findByKey(crossdockKey)
            .orElseThrow(() -> new IllegalArgumentException("Crossdock not found: " + crossdockKey));

        Quantity pickedQty = new Quantity(request.getPickedQuantity(), request.getUom());
        crossdock.pick(pickedQty, request.getPickedBy());

        // Set outbound LPN if provided
        if (request.getOutboundLpn() != null) {
            crossdock.setOutboundLpn(new LpnKey(request.getOutboundLpn()));
        }

        Crossdock saved = crossdockRepository.save(crossdock);

        eventPublisher.publishEvent(new CrossdockPickedEvent(crossdockKey, pickedQty));

        CrossdockPickResult result = new CrossdockPickResult();
        result.setCrossdockKey(crossdockKey);
        result.setPickedQuantity(request.getPickedQuantity());
        result.setOutboundLpn(request.getOutboundLpn());
        result.setSuccess(true);
        return result;
    }

    /**
     * Stage crossdock at staging location.
     */
    public Crossdock stage(String crossdockKey, String stagingLocation) {
        Crossdock crossdock = crossdockRepository.findByKey(crossdockKey)
            .orElseThrow(() -> new IllegalArgumentException("Crossdock not found: " + crossdockKey));

        crossdock.stage(new LocationKey(stagingLocation));
        Crossdock saved = crossdockRepository.save(crossdock);

        eventPublisher.publishEvent(new CrossdockStagedEvent(crossdockKey, stagingLocation));
        return saved;
    }

    /**
     * Load crossdock onto trailer.
     */
    public Crossdock load(String crossdockKey, String loadKey, String dockDoor) {
        Crossdock crossdock = crossdockRepository.findByKey(crossdockKey)
            .orElseThrow(() -> new IllegalArgumentException("Crossdock not found: " + crossdockKey));

        crossdock.load(loadKey, new LocationKey(dockDoor));
        Crossdock saved = crossdockRepository.save(crossdock);

        eventPublisher.publishEvent(new CrossdockLoadedEvent(crossdockKey, loadKey));
        return saved;
    }

    /**
     * Ship crossdock.
     */
    public CrossdockShipResult ship(String crossdockKey, BigDecimal shippedQuantity) {
        Crossdock crossdock = crossdockRepository.findByKey(crossdockKey)
            .orElseThrow(() -> new IllegalArgumentException("Crossdock not found: " + crossdockKey));

        crossdock.ship(new Quantity(shippedQuantity, "EA"));
        Crossdock saved = crossdockRepository.save(crossdock);

        eventPublisher.publishEvent(new CrossdockShippedEvent(crossdockKey));

        CrossdockShipResult result = new CrossdockShipResult();
        result.setCrossdockKey(crossdockKey);
        result.setShippedQuantity(shippedQuantity);
        result.setOrderKey(crossdock.getOrderKey());
        result.setSuccess(true);
        return result;
    }

    /**
     * Cancel crossdock.
     */
    public Crossdock cancel(String crossdockKey, String reason) {
        Crossdock crossdock = crossdockRepository.findByKey(crossdockKey)
            .orElseThrow(() -> new IllegalArgumentException("Crossdock not found: " + crossdockKey));

        crossdock.cancel(reason);
        Crossdock saved = crossdockRepository.save(crossdock);

        eventPublisher.publishEvent(new CrossdockCancelledEvent(crossdockKey, reason));
        return saved;
    }

    /**
     * Add detail line to crossdock.
     */
    public Crossdock addDetail(String crossdockKey, CreateCrossdockDetailRequest request) {
        Crossdock crossdock = crossdockRepository.findByKey(crossdockKey)
            .orElseThrow(() -> new IllegalArgumentException("Crossdock not found: " + crossdockKey));

        CrossdockDetail detail = new CrossdockDetail();
        detail.setSkuKey(new SkuKey(crossdock.getStorerKey().getValue(), request.getSku()));
        detail.setAllocatedQty(new Quantity(request.getAllocatedQty(), request.getUom()));
        detail.setLot(request.getLot());
        detail.setSerialNumber(request.getSerialNumber());
        detail.setOrderKey(request.getOrderKey());
        detail.setOrderDetailKey(request.getOrderDetailKey());
        detail.setLineNumber(request.getLineNumber());

        crossdock.addDetail(detail);
        return crossdockRepository.save(crossdock);
    }

    /**
     * Get crossdock by order.
     */
    @Transactional(readOnly = true)
    public List<Crossdock> getByOrder(String orderKey) {
        return crossdockRepository.findByOrderKey(orderKey);
    }

    /**
     * Get crossdock by wave.
     */
    @Transactional(readOnly = true)
    public List<Crossdock> getByWave(String waveKey) {
        return crossdockRepository.findByWaveKey(waveKey);
    }

    /**
     * Get crossdock by load.
     */
    @Transactional(readOnly = true)
    public List<Crossdock> getByLoad(String loadKey) {
        return crossdockRepository.findByLoadKey(loadKey);
    }

    /**
     * Get crossdock statistics.
     */
    @Transactional(readOnly = true)
    public CrossdockStats getStats() {
        CrossdockStats stats = new CrossdockStats();
        stats.setPendingCount(crossdockRepository.countByStatus(CrossdockStatus.PENDING));
        stats.setAllocatedCount(crossdockRepository.countByStatus(CrossdockStatus.ALLOCATED));
        stats.setReleasedCount(crossdockRepository.countByStatus(CrossdockStatus.RELEASED));
        stats.setPickedCount(crossdockRepository.countByStatus(CrossdockStatus.PICKED));
        stats.setStagedCount(crossdockRepository.countByStatus(CrossdockStatus.STAGED));
        stats.setLoadedCount(crossdockRepository.countByStatus(CrossdockStatus.LOADED));
        stats.setShippedCount(crossdockRepository.countByStatus(CrossdockStatus.SHIPPED));
        stats.setOpportunisticCount(crossdockRepository.countByType(CrossdockType.OPPORTUNISTIC));
        stats.setPlannedCount(crossdockRepository.countByType(CrossdockType.PLANNED));
        return stats;
    }

    private void validateCreateRequest(CreateCrossdockRequest request) {
        if (request.getStorerKey() == null || request.getStorerKey().isBlank()) {
            throw new IllegalArgumentException("Storer key is required");
        }
        if (request.getReceiptKey() == null || request.getReceiptKey().isBlank()) {
            throw new IllegalArgumentException("Receipt key is required");
        }
        if (request.getSku() == null || request.getSku().isBlank()) {
            throw new IllegalArgumentException("SKU is required");
        }
    }

    // Event classes
    public record CrossdockCreatedEvent(String crossdockKey) {}
    public record CrossdockAllocatedEvent(String crossdockKey, String orderKey) {}
    public record CrossdockReleasedEvent(String crossdockKey) {}
    public record CrossdockPickedEvent(String crossdockKey, Quantity pickedQty) {}
    public record CrossdockStagedEvent(String crossdockKey, String stagingLocation) {}
    public record CrossdockLoadedEvent(String crossdockKey, String loadKey) {}
    public record CrossdockShippedEvent(String crossdockKey) {}
    public record CrossdockCancelledEvent(String crossdockKey, String reason) {}
}
