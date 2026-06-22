package com.maersk.wms.outbound.service.shipping;

import com.maersk.wms.outbound.domain.shipping.*;
import com.maersk.wms.outbound.domain.repository.ManifestRepository;
import com.maersk.wms.outbound.domain.repository.MbolRepository;
import com.maersk.wms.outbound.plugin.OutboundPluginContext;
import com.maersk.wms.outbound.plugin.OutboundPluginRegistry;
import com.maersk.wms.outbound.plugin.PluginResult;
import com.maersk.wms.outbound.plugin.shipping.ManifestPlugin;
import com.maersk.wms.outbound.plugin.shipping.PickupRequest;
import com.maersk.wms.outbound.plugin.shipping.PickupScheduleResult;
import com.maersk.wms.outbound.service.OutboundOperationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for shipping manifest (end-of-day) operations.
 *
 * Legacy SP References:
 * - nsp_ShippingManifestDetails - Get manifest details
 * - nsp_GetLoadManifest - Get load manifest
 * - nsp_LoadManifestSum01-05 - Manifest summaries
 * - isp_shipping_manifest_by_load_* - Load-based manifests (12+ variants)
 * - isp_CartonManifestLabel* - Carton manifest labels (40+ variants)
 * - rdtfnc_PrtPltManifest - Print pallet manifest
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ManifestService {

    private final ManifestRepository manifestRepository;
    private final MbolRepository mbolRepository;
    private final OutboundPluginRegistry pluginRegistry;

    /**
     * Get or create open manifest for carrier.
     *
     * Legacy Reference: End-of-day manifest logic
     */
    @Transactional
    public ShippingManifest getOrCreateManifest(String carrierCode, String facility, OutboundPluginContext context) {
        log.info("Getting/creating manifest for carrier: {} facility: {}", carrierCode, facility);

        Optional<ShippingManifest> existingManifest = manifestRepository.findOpenManifestForCarrier(carrierCode, facility);

        if (existingManifest.isPresent()) {
            return existingManifest.get();
        }

        // Create new manifest
        ShippingManifest manifest = ShippingManifest.builder()
                .carrierCode(carrierCode)
                .facility(facility)
                .status(ManifestStatus.OPEN)
                .type(ManifestType.END_OF_DAY)
                .manifestDate(LocalDateTime.now())
                .addWho(context.getUserId())
                .addDate(LocalDateTime.now())
                .build();

        // Execute manifest creation plugins
        pluginRegistry.executeAll(
                ManifestPlugin.class,
                context,
                plugin -> plugin.onManifestCreate(manifest, context)
        );

        return manifestRepository.save(manifest);
    }

    /**
     * Add MBOL to manifest.
     */
    @Transactional
    public ShippingManifest addToManifest(String manifestKey, String mbolKey, OutboundPluginContext context) {
        log.info("Adding MBOL {} to manifest {}", mbolKey, manifestKey);

        ShippingManifest manifest = manifestRepository.findByKey(manifestKey)
                .orElseThrow(() -> new OutboundOperationException("Manifest not found: " + manifestKey));

        if (!manifest.isOpen()) {
            throw new OutboundOperationException("Manifest is not open: " + manifestKey);
        }

        MasterBillOfLading mbol = mbolRepository.findByKey(mbolKey)
                .orElseThrow(() -> new OutboundOperationException("MBOL not found: " + mbolKey));

        // Validate carrier match
        if (!manifest.getCarrierCode().equals(mbol.getCarrierCode())) {
            throw new OutboundOperationException("Carrier mismatch: manifest=" + manifest.getCarrierCode() +
                    " mbol=" + mbol.getCarrierCode());
        }

        // Add to manifest
        manifestRepository.addMbol(manifestKey, mbolKey);

        // Update totals
        manifest.setTotalShipments(manifest.getTotalShipments() + 1);
        manifest.setTotalPackages(manifest.getTotalPackages() + mbol.getTotalCartons());
        manifest.setTotalWeight(manifest.getTotalWeight() != null ?
                manifest.getTotalWeight().add(mbol.getTotalWeight() != null ? mbol.getTotalWeight() : BigDecimal.ZERO) :
                mbol.getTotalWeight());

        // Update MBOL
        mbol.setManifestKey(manifestKey);
        mbol.setStatus(MbolStatus.MANIFESTED);
        mbolRepository.save(mbol);

        return manifestRepository.save(manifest);
    }

    /**
     * Remove MBOL from manifest.
     */
    @Transactional
    public void removeFromManifest(String manifestKey, String mbolKey, OutboundPluginContext context) {
        log.info("Removing MBOL {} from manifest {}", mbolKey, manifestKey);

        ShippingManifest manifest = manifestRepository.findByKey(manifestKey)
                .orElseThrow(() -> new OutboundOperationException("Manifest not found: " + manifestKey));

        if (!manifest.isOpen()) {
            throw new OutboundOperationException("Cannot modify closed manifest: " + manifestKey);
        }

        manifestRepository.removeMbol(manifestKey, mbolKey);

        // Update MBOL
        mbolRepository.findByKey(mbolKey).ifPresent(mbol -> {
            mbol.setManifestKey(null);
            mbol.setStatus(MbolStatus.PACKED);
            mbolRepository.save(mbol);
        });
    }

    /**
     * Close manifest (end-of-day).
     *
     * Legacy Reference: Manifest close and transmission to carrier
     */
    @Transactional
    public ShippingManifest closeManifest(String manifestKey, OutboundPluginContext context) {
        log.info("Closing manifest: {}", manifestKey);

        ShippingManifest manifest = manifestRepository.findByKey(manifestKey)
                .orElseThrow(() -> new OutboundOperationException("Manifest not found: " + manifestKey));

        if (!manifest.canClose()) {
            throw new OutboundOperationException("Manifest cannot be closed: " + manifestKey);
        }

        // Execute before close plugins
        PluginResult beforeResult = pluginRegistry.executeAll(
                ManifestPlugin.class,
                context,
                plugin -> plugin.beforeManifestClose(manifest, context)
        );

        if (!beforeResult.isSuccess()) {
            throw new OutboundOperationException("Manifest close blocked: " + beforeResult.getErrorMessage());
        }

        // Close manifest
        manifest.setStatus(ManifestStatus.CLOSED);
        manifest.setCloseDate(LocalDateTime.now());
        manifest.setEditWho(context.getUserId());
        manifest.setEditDate(LocalDateTime.now());

        ShippingManifest closedManifest = manifestRepository.save(manifest);

        // Execute after close plugins (transmit to carrier, generate manifest document)
        pluginRegistry.executeAll(
                ManifestPlugin.class,
                context,
                plugin -> plugin.afterManifestClose(closedManifest, context)
        );

        log.info("Manifest closed: {} with {} shipments", manifestKey, manifest.getTotalShipments());

        return closedManifest;
    }

    /**
     * Transmit manifest to carrier.
     */
    @Transactional
    public ManifestTransmissionResult transmitManifest(String manifestKey, OutboundPluginContext context) {
        log.info("Transmitting manifest to carrier: {}", manifestKey);

        ShippingManifest manifest = manifestRepository.findByKey(manifestKey)
                .orElseThrow(() -> new OutboundOperationException("Manifest not found: " + manifestKey));

        if (manifest.getStatus() != ManifestStatus.CLOSED) {
            throw new OutboundOperationException("Manifest must be closed before transmission");
        }

        // Execute transmission via plugin (carrier API)
        Optional<ManifestPlugin> plugin = pluginRegistry.getPlugin(ManifestPlugin.class, context);

        ManifestTransmissionResult result;
        if (plugin.isPresent()) {
            result = plugin.get().transmitManifest(manifest, context);
        } else {
            result = ManifestTransmissionResult.builder()
                    .success(true)
                    .manifestKey(manifestKey)
                    .transmittedAt(LocalDateTime.now())
                    .build();
        }

        if (result.isSuccess()) {
            manifest.setStatus(ManifestStatus.TRANSMITTED);
            manifestRepository.save(manifest);
        }

        return result;
    }

    /**
     * Get manifest details.
     *
     * Legacy Reference: nsp_ShippingManifestDetails
     */
    public ManifestDetails getManifestDetails(String manifestKey) {
        ShippingManifest manifest = manifestRepository.findByKey(manifestKey)
                .orElseThrow(() -> new OutboundOperationException("Manifest not found: " + manifestKey));

        List<MasterBillOfLading> mbols = mbolRepository.findByStatus(MbolStatus.MANIFESTED)
                .stream()
                .filter(m -> manifestKey.equals(m.getManifestKey()))
                .toList();

        return ManifestDetails.builder()
                .manifest(manifest)
                .mbols(mbols)
                .totalMbols(mbols.size())
                .totalPackages(manifest.getTotalPackages())
                .totalWeight(manifest.getTotalWeight())
                .build();
    }

    /**
     * Get manifests for a date.
     */
    public List<ShippingManifest> getManifestsByDate(String carrierCode, LocalDate date) {
        return manifestRepository.findByCarrierAndDate(carrierCode, date);
    }

    /**
     * Get open manifests for facility.
     */
    public List<ShippingManifest> getOpenManifests(String facility) {
        return manifestRepository.findByStatus(ManifestStatus.OPEN)
                .stream()
                .filter(m -> facility.equals(m.getFacility()))
                .toList();
    }

    /**
     * Void manifest.
     */
    @Transactional
    public void voidManifest(String manifestKey, String reason, OutboundPluginContext context) {
        log.info("Voiding manifest: {} reason: {}", manifestKey, reason);

        ShippingManifest manifest = manifestRepository.findByKey(manifestKey)
                .orElseThrow(() -> new OutboundOperationException("Manifest not found: " + manifestKey));

        if (manifest.getStatus() == ManifestStatus.PICKED_UP) {
            throw new OutboundOperationException("Cannot void picked up manifest");
        }

        // Remove all MBOLs from manifest
        for (String mbolKey : manifest.getMbolKeys()) {
            mbolRepository.findByKey(mbolKey).ifPresent(mbol -> {
                mbol.setManifestKey(null);
                mbol.setStatus(MbolStatus.PACKED);
                mbolRepository.save(mbol);
            });
        }

        manifest.setStatus(ManifestStatus.VOIDED);
        manifestRepository.save(manifest);
    }

    /**
     * Create new manifest for carrier.
     *
     * Legacy Reference: Manifest creation from carrier manifest process
     */
    @Transactional
    public ShippingManifest createManifest(String carrierCode, OutboundPluginContext context) {
        log.info("Creating manifest for carrier: {}", carrierCode);

        ShippingManifest manifest = ShippingManifest.builder()
                .carrierCode(carrierCode)
                .facility(context.getFacilityCode())
                .status(ManifestStatus.OPEN)
                .manifestType(ManifestType.END_OF_DAY)
                .manifestDate(LocalDateTime.now())
                .totalShipments(0)
                .totalPackages(0)
                .totalWeight(BigDecimal.ZERO)
                .addWho(context.getUserId())
                .addDate(LocalDateTime.now())
                .build();

        // Execute manifest creation plugins
        pluginRegistry.executeAll(
                ManifestPlugin.class,
                context,
                plugin -> plugin.onManifestCreate(manifest, context)
        );

        return manifestRepository.save(manifest);
    }

    /**
     * Add MBOL to manifest (alias for addToManifest for activity compatibility).
     */
    @Transactional
    public ShippingManifest addMbolToManifest(String manifestKey, String mbolKey, OutboundPluginContext context) {
        return addToManifest(manifestKey, mbolKey, context);
    }

    /**
     * Schedule pickup with carrier.
     *
     * Legacy Reference: Carrier pickup scheduling
     */
    @Transactional
    public PickupScheduleResult schedulePickup(String manifestKey, PickupRequest request, OutboundPluginContext context) {
        log.info("Scheduling pickup for manifest: {}", manifestKey);

        ShippingManifest manifest = manifestRepository.findByKey(manifestKey)
                .orElseThrow(() -> new OutboundOperationException("Manifest not found: " + manifestKey));

        if (manifest.getStatus() != ManifestStatus.CLOSED && manifest.getStatus() != ManifestStatus.TRANSMITTED) {
            throw new OutboundOperationException("Manifest must be closed or transmitted before scheduling pickup");
        }

        // Execute pickup scheduling via plugin (carrier API)
        Optional<ManifestPlugin> plugin = pluginRegistry.getPlugin(ManifestPlugin.class, context);

        PickupScheduleResult result;
        if (plugin.isPresent()) {
            result = plugin.get().schedulePickup(manifest, request, context);
        } else {
            // Default - generate confirmation number
            result = PickupScheduleResult.success("PU-" + System.currentTimeMillis());
        }

        if (result.isSuccess()) {
            manifest.setPickupConfirmation(result.getConfirmationNumber());
            manifest.setScheduledPickupDate(result.getScheduledPickupTime());
            manifestRepository.save(manifest);
        }

        return result;
    }

    /**
     * Cancel scheduled pickup.
     *
     * Legacy Reference: Carrier pickup cancellation
     */
    @Transactional
    public void cancelPickup(String confirmationNumber, OutboundPluginContext context) {
        log.info("Canceling pickup: {}", confirmationNumber);

        // Find manifest by pickup confirmation
        Optional<ShippingManifest> manifestOpt = manifestRepository.findByPickupConfirmation(confirmationNumber);

        if (manifestOpt.isEmpty()) {
            throw new OutboundOperationException("Manifest not found for pickup: " + confirmationNumber);
        }

        ShippingManifest manifest = manifestOpt.get();

        // Execute pickup cancellation via plugin (carrier API)
        Optional<ManifestPlugin> plugin = pluginRegistry.getPlugin(ManifestPlugin.class, context);

        if (plugin.isPresent()) {
            plugin.get().cancelPickup(confirmationNumber, context);
        }

        manifest.setPickupConfirmation(null);
        manifest.setScheduledPickupDate(null);
        manifestRepository.save(manifest);
    }
}
