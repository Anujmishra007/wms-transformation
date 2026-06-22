package com.maersk.wms.outbound.workflow.shipping;

import com.maersk.wms.outbound.activity.ShippingActivities;
import com.maersk.wms.outbound.activity.ShippingActivities.*;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the shipping workflow.
 * Orchestrates the complete shipping lifecycle: MBOL → Carrier → Labels → Manifest → Ship
 *
 * Legacy SP References:
 * - WM.lsp_WaveGenMBOL - Generate MBOL from wave
 * - nsp_BackEndShipped - Ship confirmation
 * - isp_PrintCarrierLabel - Label generation
 * - nsp_ShippingManifestDetails - Manifest operations
 */
@Slf4j
public class ShippingWorkflowImpl implements ShippingWorkflow {

    private static final String TASK_QUEUE = "shipping-processing";

    // Activity stubs
    private final ShippingActivities activities;

    // Workflow state
    private ShippingWorkflowState state;
    private ShippingWorkflowRequest request;

    // Signals
    private boolean selectCarrierSignal = false;
    private boolean calculateFreightSignal = false;
    private boolean generateLabelsSignal = false;
    private boolean closeManifestSignal = false;
    private boolean transmitManifestSignal = false;
    private boolean cancelSignal = false;
    private String cancelReason;

    private String generateMbolFromWaveKey = null;
    private String generateMbolFromLoadKey = null;
    private String addToManifestKey = null;
    private boolean removeFromManifestSignal = false;

    private final List<String> addOrderSignals = new ArrayList<>();
    private final List<String> removeOrderSignals = new ArrayList<>();
    private List<String> splitMbolOrderKeys = null;

    private ChangeCarrierSignal changeCarrierSignal = null;
    private final List<GenerateLabelSignal> generateLabelSignals = new ArrayList<>();
    private final List<String> reprintLabelSignals = new ArrayList<>();
    private final List<VoidLabelSignal> voidLabelSignals = new ArrayList<>();

    private ShipConfirmSignal shipConfirmSignal = null;
    private SchedulePickupSignal schedulePickupSignal = null;
    private boolean cancelPickupSignal = false;

    public ShippingWorkflowImpl() {
        ActivityOptions options = ActivityOptions.newBuilder()
                .setStartToCloseTimeout(Duration.ofMinutes(5))
                .setRetryOptions(RetryOptions.newBuilder()
                        .setInitialInterval(Duration.ofSeconds(1))
                        .setMaximumInterval(Duration.ofMinutes(1))
                        .setBackoffCoefficient(2.0)
                        .setMaximumAttempts(3)
                        .build())
                .build();

        this.activities = Workflow.newActivityStub(ShippingActivities.class, options);
    }

    @Override
    public ShippingWorkflowResult execute(ShippingWorkflowRequest request) {
        this.request = request;
        initializeState();

        log.info("Starting shipping workflow for wave: {} load: {}",
                request.getWaveKey(), request.getLoadKey());

        try {
            // Phase 1: Create/Get MBOL
            if (!createOrGetMbol()) {
                return buildFailureResult("Failed to create MBOL");
            }

            if (checkCancellation()) {
                return buildCancellationResult();
            }

            // Phase 2: Carrier Selection
            if (!executeCarrierPhase()) {
                return buildFailureResult("Carrier selection failed");
            }

            if (checkCancellation()) {
                return buildCancellationResult();
            }

            // Phase 3: Label Generation
            if (!executeLabelPhase()) {
                return buildFailureResult("Label generation failed");
            }

            if (checkCancellation()) {
                return buildCancellationResult();
            }

            // Phase 4: Manifest
            if (!executeManifestPhase()) {
                return buildFailureResult("Manifest processing failed");
            }

            if (checkCancellation()) {
                return buildCancellationResult();
            }

            // Phase 5: Ship Confirmation
            if (!executeShipPhase()) {
                return buildFailureResult("Ship confirmation failed");
            }

            // Phase 6: Pickup (optional)
            if (request.isAutoSchedulePickup() || schedulePickupSignal != null) {
                executePickupPhase();
            }

            // Success
            state.setStatus(ShippingWorkflowStatus.COMPLETED);

            log.info("Shipping workflow completed successfully for MBOL: {}", state.getMbolKey());

            return buildSuccessResult();

        } catch (Exception e) {
            log.error("Shipping workflow failed", e);
            state.setStatus(ShippingWorkflowStatus.FAILED);
            state.getErrors().add(e.getMessage());
            return buildFailureResult(e.getMessage());
        }
    }

    // ========== Workflow Phases ==========

    private boolean createOrGetMbol() {
        updateStatus(ShippingWorkflowStatus.VALIDATING, "Validating shipment data");

        // If MBOL already exists, use it
        if (request.getMbolKey() != null) {
            state.setMbolKey(request.getMbolKey());
            state.setMbolCreated(true);
            updateStatus(ShippingWorkflowStatus.MBOL_CREATED, "Using existing MBOL");
            return true;
        }

        // Generate from wave
        if (request.getWaveKey() != null) {
            updateStatus(ShippingWorkflowStatus.CREATING_MBOL, "Creating MBOL from wave");

            MbolResult result = activities.generateMbolFromWave(
                    request.getWaveKey(), request.getClientCode(), request.getFacilityCode());

            if (!result.isSuccess()) {
                state.getErrors().addAll(result.getErrors());
                return false;
            }

            state.setMbolKey(result.getMbolKey());
            state.setTotalOrders(result.getTotalOrders());
            state.setTotalPackages(result.getTotalCartons());
            state.setTotalWeight(result.getTotalWeight());
        }
        // Generate from load plan
        else if (request.getLoadKey() != null) {
            updateStatus(ShippingWorkflowStatus.POPULATING_MBOL, "Populating MBOL from load plan");

            MbolResult result = activities.populateMbolFromLoadPlan(
                    request.getLoadKey(), request.getClientCode(), request.getFacilityCode());

            if (!result.isSuccess()) {
                state.getErrors().addAll(result.getErrors());
                return false;
            }

            state.setMbolKey(result.getMbolKey());
        }
        // Generate from orders
        else if (request.getOrderKeys() != null && !request.getOrderKeys().isEmpty()) {
            updateStatus(ShippingWorkflowStatus.CREATING_MBOL, "Creating MBOL from orders");
            // Would need an activity to create MBOL from orders
            state.getErrors().add("MBOL creation from orders not yet implemented");
            return false;
        } else {
            state.getErrors().add("No wave, load, or orders provided");
            return false;
        }

        state.setMbolCreated(true);
        state.setMbolCreatedTime(LocalDateTime.now());
        updateStatus(ShippingWorkflowStatus.MBOL_CREATED, "MBOL created: " + state.getMbolKey());

        return true;
    }

    private boolean executeCarrierPhase() {
        // Auto-select or wait for signal
        if (request.isAutoSelectCarrier() || selectCarrierSignal) {
            updateStatus(ShippingWorkflowStatus.SELECTING_CARRIER, "Selecting carrier");

            if (request.isRateShopEnabled()) {
                updateStatus(ShippingWorkflowStatus.RATE_SHOPPING, "Rate shopping carriers");
            }

            CarrierResult result = activities.selectCarrier(
                    state.getMbolKey(), request.getClientCode(), request.getFacilityCode());

            if (!result.isSuccess()) {
                // If auto-select fails, use preferred carrier
                if (request.getPreferredCarrierCode() != null) {
                    state.setCarrierCode(request.getPreferredCarrierCode());
                    state.setServiceCode(request.getPreferredServiceCode());
                } else {
                    state.getErrors().addAll(result.getErrors());
                    return false;
                }
            } else {
                state.setCarrierCode(result.getCarrierCode());
                state.setCarrierName(result.getCarrierName());
                state.setServiceCode(result.getServiceCode());
                state.setServiceName(result.getServiceName());
            }
        } else if (request.getPreferredCarrierCode() != null) {
            // Use preferred carrier directly
            state.setCarrierCode(request.getPreferredCarrierCode());
            state.setServiceCode(request.getPreferredServiceCode());
        } else {
            // Wait for carrier selection signal
            updateStatus(ShippingWorkflowStatus.SELECTING_CARRIER, "Awaiting carrier selection");
            Workflow.await(() -> selectCarrierSignal || changeCarrierSignal != null || cancelSignal);

            if (cancelSignal) return false;

            if (changeCarrierSignal != null) {
                state.setCarrierCode(changeCarrierSignal.getNewCarrierCode());
                state.setServiceCode(changeCarrierSignal.getNewServiceCode());
                changeCarrierSignal = null;
            }
        }

        // Calculate freight
        updateStatus(ShippingWorkflowStatus.CALCULATING_FREIGHT, "Calculating freight");

        FreightResult freightResult = activities.calculateFreight(
                state.getMbolKey(), request.getClientCode(), request.getFacilityCode());

        if (freightResult.isSuccess()) {
            state.setFreightCharge(freightResult.getFreightCharge());
            state.setTotalShippingCost(freightResult.getFreightCharge());
        }

        state.setCarrierSelected(true);
        state.setCarrierSelectedTime(LocalDateTime.now());
        updateStatus(ShippingWorkflowStatus.CARRIER_SELECTED, "Carrier selected: " + state.getCarrierCode());

        return true;
    }

    private boolean executeLabelPhase() {
        if (!request.isAutoGenerateLabels() && generateLabelSignals.isEmpty()) {
            // Wait for label generation signal
            updateStatus(ShippingWorkflowStatus.GENERATING_LABELS, "Awaiting label generation");
            Workflow.await(() -> generateLabelsSignal || !generateLabelSignals.isEmpty() || cancelSignal);

            if (cancelSignal) return false;
        }

        updateStatus(ShippingWorkflowStatus.GENERATING_LABELS, "Generating shipping labels");

        // Generate labels for all packages
        LabelResult labelResult = activities.generateLabelsForMbol(
                state.getMbolKey(),
                request.getLabelFormat() != null ? request.getLabelFormat() : "ZPL",
                request.getClientCode(),
                request.getFacilityCode());

        if (!labelResult.isSuccess()) {
            state.getWarnings().addAll(labelResult.getErrors());
            // Continue even if some labels fail
        }

        // Track generated labels
        if (labelResult.getLabelUrls() != null) {
            for (int i = 0; i < labelResult.getLabelUrls().size(); i++) {
                String trackingNumber = labelResult.getLabelUrls().get(i);
                state.getPackages().add(ShippingWorkflowState.PackageState.builder()
                        .cbolKey("CBOL-" + i)
                        .trackingNumber(trackingNumber)
                        .labelGenerated(true)
                        .build());
            }
        }

        // Process individual label signals
        while (!generateLabelSignals.isEmpty()) {
            GenerateLabelSignal signal = generateLabelSignals.remove(0);
            processGenerateLabelSignal(signal);
        }

        // Set master tracking number
        if (!state.getPackages().isEmpty()) {
            state.setMasterTrackingNumber(state.getPackages().get(0).getTrackingNumber());
        }

        state.setLabelsGenerated(true);
        state.setLabelsGeneratedTime(LocalDateTime.now());
        updateStatus(ShippingWorkflowStatus.LABELS_GENERATED, "Labels generated");

        return true;
    }

    private boolean executeManifestPhase() {
        if (!request.isAutoAddToManifest() && addToManifestKey == null) {
            updateStatus(ShippingWorkflowStatus.READY_TO_SHIP, "Ready for manifest");

            // Wait for manifest signal or skip if auto-ship
            if (!request.isAutoShip()) {
                Workflow.await(Duration.ofHours(8), () ->
                        addToManifestKey != null || closeManifestSignal ||
                        shipConfirmSignal != null || cancelSignal);
            }

            if (cancelSignal) return false;

            if (addToManifestKey == null && !closeManifestSignal && shipConfirmSignal == null) {
                // Timeout - proceed without manifest
                state.getWarnings().add("Manifest step skipped due to timeout");
                return true;
            }
        }

        // Create or get manifest
        String manifestKey = addToManifestKey != null ? addToManifestKey : request.getManifestKey();

        if (manifestKey == null) {
            // Create new manifest
            updateStatus(ShippingWorkflowStatus.ADDING_TO_MANIFEST, "Creating manifest");

            ManifestResult createResult = activities.createManifest(
                    state.getCarrierCode(), "END_OF_DAY",
                    request.getClientCode(), request.getFacilityCode());

            if (createResult.isSuccess()) {
                manifestKey = createResult.getManifestKey();
            } else {
                state.getWarnings().addAll(createResult.getErrors());
                return true;  // Continue without manifest
            }
        }

        // Add to manifest
        updateStatus(ShippingWorkflowStatus.ADDING_TO_MANIFEST, "Adding to manifest");

        ManifestResult addResult = activities.addMbolToManifest(
                manifestKey, state.getMbolKey(),
                request.getClientCode(), request.getFacilityCode());

        if (addResult.isSuccess()) {
            state.setManifestKey(manifestKey);
            state.setManifested(true);
            state.setManifestedTime(LocalDateTime.now());
            updateStatus(ShippingWorkflowStatus.MANIFESTED, "Added to manifest: " + manifestKey);
        }

        // Close manifest if requested
        if (request.isAutoCloseManifest() || closeManifestSignal) {
            updateStatus(ShippingWorkflowStatus.CLOSING_MANIFEST, "Closing manifest");

            ManifestResult closeResult = activities.closeManifest(
                    state.getManifestKey(), request.getClientCode(), request.getFacilityCode());

            if (closeResult.isSuccess()) {
                state.setManifestClosed(true);
                updateStatus(ShippingWorkflowStatus.MANIFEST_CLOSED, "Manifest closed");
            }

            // Transmit manifest
            if (transmitManifestSignal || request.isAutoCloseManifest()) {
                updateStatus(ShippingWorkflowStatus.TRANSMITTING_MANIFEST, "Transmitting manifest");

                ManifestResult transmitResult = activities.transmitManifest(
                        state.getManifestKey(), request.getClientCode(), request.getFacilityCode());

                if (transmitResult.isSuccess()) {
                    state.setManifestTransmitted(true);
                    state.setManifestTransmissionId(transmitResult.getTransmissionId());
                    state.setManifestTransmittedTime(LocalDateTime.now());
                    updateStatus(ShippingWorkflowStatus.MANIFEST_TRANSMITTED, "Manifest transmitted");
                }
            }
        }

        return true;
    }

    private boolean executeShipPhase() {
        updateStatus(ShippingWorkflowStatus.READY_TO_SHIP, "Ready to ship");

        // Wait for ship confirmation if not auto-ship
        if (!request.isAutoShip() && shipConfirmSignal == null) {
            Workflow.await(() -> shipConfirmSignal != null || cancelSignal);
            if (cancelSignal) return false;
        }

        updateStatus(ShippingWorkflowStatus.CONFIRMING_SHIPMENT, "Confirming shipment");

        // Build ship confirm input
        ShipConfirmInput confirmInput = ShipConfirmInput.builder()
                .trackingNumber(state.getMasterTrackingNumber())
                .actualShipDate(LocalDateTime.now())
                .build();

        if (shipConfirmSignal != null) {
            confirmInput.setTrackingNumber(shipConfirmSignal.getMasterTrackingNumber() != null ?
                    shipConfirmSignal.getMasterTrackingNumber() : state.getMasterTrackingNumber());
            confirmInput.setProNumber(shipConfirmSignal.getProNumber());
            confirmInput.setTrailerNumber(shipConfirmSignal.getTrailerNumber());
            confirmInput.setSealNumber(shipConfirmSignal.getSealNumber());
            confirmInput.setActualShipDate(shipConfirmSignal.getActualShipDateTime() != null ?
                    shipConfirmSignal.getActualShipDateTime() : LocalDateTime.now());
        }

        MbolResult shipResult = activities.shipMbol(
                state.getMbolKey(), confirmInput,
                request.getClientCode(), request.getFacilityCode());

        if (!shipResult.isSuccess()) {
            state.getErrors().addAll(shipResult.getErrors());
            return false;
        }

        state.setShipped(true);
        state.setShippedTime(LocalDateTime.now());
        updateStatus(ShippingWorkflowStatus.SHIPPED, "Shipment confirmed");

        return true;
    }

    private void executePickupPhase() {
        updateStatus(ShippingWorkflowStatus.SCHEDULING_PICKUP, "Scheduling pickup");

        PickupInput pickupInput;

        if (schedulePickupSignal != null) {
            pickupInput = PickupInput.builder()
                    .pickupDate(schedulePickupSignal.getPickupDate().toString())
                    .readyTime(schedulePickupSignal.getReadyTime().toString())
                    .closeTime(schedulePickupSignal.getCloseTime().toString())
                    .pickupLocation(schedulePickupSignal.getPickupLocation())
                    .contactName(schedulePickupSignal.getContactName())
                    .contactPhone(schedulePickupSignal.getContactPhone())
                    .totalPackages(schedulePickupSignal.getTotalPackages())
                    .totalWeight(schedulePickupSignal.getTotalWeight())
                    .weightUom(schedulePickupSignal.getWeightUom())
                    .residentialPickup(schedulePickupSignal.isResidentialPickup())
                    .specialInstructions(schedulePickupSignal.getSpecialInstructions())
                    .build();
        } else {
            // Default pickup for next business day
            pickupInput = PickupInput.builder()
                    .pickupDate(LocalDate.now().plusDays(1).toString())
                    .readyTime(LocalTime.of(9, 0).toString())
                    .closeTime(LocalTime.of(17, 0).toString())
                    .totalPackages(state.getTotalPackages())
                    .totalWeight(state.getTotalWeight() != null ? state.getTotalWeight().intValue() : 0)
                    .build();
        }

        PickupResult pickupResult = activities.schedulePickup(
                state.getManifestKey() != null ? state.getManifestKey() : state.getMbolKey(),
                pickupInput,
                request.getClientCode(),
                request.getFacilityCode());

        if (pickupResult.isSuccess()) {
            state.setPickupScheduled(true);
            state.setPickupConfirmationNumber(pickupResult.getConfirmationNumber());
            state.setScheduledPickupTime(pickupResult.getScheduledPickupTime());
            state.setPickupScheduledTime(LocalDateTime.now());
            updateStatus(ShippingWorkflowStatus.PICKUP_SCHEDULED,
                    "Pickup scheduled: " + pickupResult.getConfirmationNumber());
        } else {
            state.getWarnings().addAll(pickupResult.getErrors());
        }
    }

    // ========== Signal Processing ==========

    private void processGenerateLabelSignal(GenerateLabelSignal signal) {
        LabelInput labelInput = LabelInput.builder()
                .cbolKey(signal.getCbolKey())
                .carrierCode(state.getCarrierCode())
                .serviceCode(state.getServiceCode())
                .format(signal.getFormat() != null ? signal.getFormat() : request.getLabelFormat())
                .shipToName(signal.getShipToName())
                .shipToAddress1(signal.getShipToAddress1())
                .shipToCity(signal.getShipToCity())
                .shipToState(signal.getShipToState())
                .shipToZip(signal.getShipToZip())
                .shipToCountry(signal.getShipToCountry())
                .weight(signal.getWeight())
                .length(signal.getLength())
                .width(signal.getWidth())
                .height(signal.getHeight())
                .build();

        LabelResult result = activities.generateLabel(
                labelInput, request.getClientCode(), request.getFacilityCode());

        if (result.isSuccess()) {
            state.updatePackage(ShippingWorkflowState.PackageState.builder()
                    .cbolKey(signal.getCbolKey())
                    .trackingNumber(result.getTrackingNumber())
                    .labelKey(result.getLabelKey())
                    .labelGenerated(true)
                    .build());
        } else {
            state.getWarnings().addAll(result.getErrors());
        }
    }

    // ========== Signal Methods ==========

    @Override
    public void generateMbolFromWave(String waveKey) {
        this.generateMbolFromWaveKey = waveKey;
    }

    @Override
    public void generateMbolFromLoadPlan(String loadKey) {
        this.generateMbolFromLoadKey = loadKey;
    }

    @Override
    public void addOrderToMbol(String orderKey) {
        this.addOrderSignals.add(orderKey);
    }

    @Override
    public void removeOrderFromMbol(String orderKey) {
        this.removeOrderSignals.add(orderKey);
    }

    @Override
    public void splitMbol(List<String> orderKeysForNewMbol) {
        this.splitMbolOrderKeys = orderKeysForNewMbol;
    }

    @Override
    public void selectCarrier() {
        this.selectCarrierSignal = true;
    }

    @Override
    public void changeCarrier(ChangeCarrierSignal signal) {
        this.changeCarrierSignal = signal;
    }

    @Override
    public void calculateFreight() {
        this.calculateFreightSignal = true;
    }

    @Override
    public void generateLabels() {
        this.generateLabelsSignal = true;
    }

    @Override
    public void generateLabel(GenerateLabelSignal signal) {
        this.generateLabelSignals.add(signal);
    }

    @Override
    public void reprintLabel(String labelKey) {
        this.reprintLabelSignals.add(labelKey);
    }

    @Override
    public void voidLabel(VoidLabelSignal signal) {
        this.voidLabelSignals.add(signal);
    }

    @Override
    public void addToManifest(String manifestKey) {
        this.addToManifestKey = manifestKey;
    }

    @Override
    public void removeFromManifest() {
        this.removeFromManifestSignal = true;
    }

    @Override
    public void closeManifest() {
        this.closeManifestSignal = true;
    }

    @Override
    public void transmitManifest() {
        this.transmitManifestSignal = true;
    }

    @Override
    public void confirmShipment(ShipConfirmSignal signal) {
        this.shipConfirmSignal = signal;
    }

    @Override
    public void schedulePickup(SchedulePickupSignal signal) {
        this.schedulePickupSignal = signal;
    }

    @Override
    public void cancelPickup() {
        this.cancelPickupSignal = true;
    }

    @Override
    public void cancelShipment(String reason) {
        this.cancelSignal = true;
        this.cancelReason = reason;
    }

    // ========== Query Methods ==========

    @Override
    public ShippingWorkflowStatus getStatus() {
        return state.getStatus();
    }

    @Override
    public ShippingWorkflowState getState() {
        return state;
    }

    @Override
    public MbolDetails getMbolDetails() {
        return MbolDetails.builder()
                .mbolKey(state.getMbolKey())
                .carrierCode(state.getCarrierCode())
                .carrierName(state.getCarrierName())
                .serviceCode(state.getServiceCode())
                .serviceName(state.getServiceName())
                .trackingNumber(state.getMasterTrackingNumber())
                .totalOrders(state.getTotalOrders())
                .totalCartons(state.getTotalPackages())
                .totalWeight(state.getTotalWeight())
                .freightCharge(state.getFreightCharge())
                .manifestKey(state.getManifestKey())
                .build();
    }

    @Override
    public List<LabelInfo> getGeneratedLabels() {
        return state.getPackages().stream()
                .filter(ShippingWorkflowState.PackageState::isLabelGenerated)
                .map(p -> LabelInfo.builder()
                        .labelKey(p.getLabelKey())
                        .cbolKey(p.getCbolKey())
                        .trackingNumber(p.getTrackingNumber())
                        .carrierCode(state.getCarrierCode())
                        .serviceCode(state.getServiceCode())
                        .printed(p.isLabelPrinted())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public ManifestInfo getManifestInfo() {
        if (state.getManifestKey() == null) {
            return null;
        }
        return ManifestInfo.builder()
                .manifestKey(state.getManifestKey())
                .manifestNumber(state.getManifestNumber())
                .carrierCode(state.getCarrierCode())
                .status(state.isManifestClosed() ? "CLOSED" : "OPEN")
                .totalShipments(1)
                .totalPackages(state.getTotalPackages())
                .totalWeight(state.getTotalWeight())
                .closeDate(state.isManifestClosed() ? state.getManifestedTime() : null)
                .transmittedDate(state.getManifestTransmittedTime())
                .transmissionId(state.getManifestTransmissionId())
                .pickupConfirmation(state.getPickupConfirmationNumber())
                .scheduledPickupTime(state.getScheduledPickupTime())
                .build();
    }

    @Override
    public int getProgressPercent() {
        return state.getProgressPercent();
    }

    @Override
    public boolean isReadyToShip() {
        return state.isReadyToShip();
    }

    // ========== Helper Methods ==========

    private void initializeState() {
        state = ShippingWorkflowState.builder()
                .status(ShippingWorkflowStatus.INITIATED)
                .workflowStartTime(LocalDateTime.now())
                .packages(new ArrayList<>())
                .errors(new ArrayList<>())
                .warnings(new ArrayList<>())
                .build();
    }

    private void updateStatus(ShippingWorkflowStatus status, String message) {
        state.setStatus(status);
        state.setStatusMessage(message);
        log.info("Shipping workflow status: {} - {} (MBOL: {})", status, message, state.getMbolKey());
    }

    private boolean checkCancellation() {
        if (cancelSignal) {
            state.setStatus(ShippingWorkflowStatus.CANCELLED);
            state.setCancelled(true);
            return true;
        }
        return false;
    }

    private ShippingWorkflowResult buildSuccessResult() {
        return ShippingWorkflowResult.builder()
                .success(true)
                .mbolKey(state.getMbolKey())
                .manifestKey(state.getManifestKey())
                .status(state.getStatus().name())
                .message("Shipping completed successfully")
                .carrierCode(state.getCarrierCode())
                .carrierName(state.getCarrierName())
                .serviceCode(state.getServiceCode())
                .serviceName(state.getServiceName())
                .masterTrackingNumber(state.getMasterTrackingNumber())
                .totalOrders(state.getTotalOrders())
                .totalPackages(state.getTotalPackages())
                .totalWeight(state.getTotalWeight())
                .freightCharge(state.getFreightCharge())
                .totalShippingCost(state.getTotalShippingCost())
                .workflowStartTime(state.getWorkflowStartTime())
                .mbolCreatedTime(state.getMbolCreatedTime())
                .labelsGeneratedTime(state.getLabelsGeneratedTime())
                .manifestedTime(state.getManifestedTime())
                .shippedTime(state.getShippedTime())
                .workflowEndTime(LocalDateTime.now())
                .manifestNumber(state.getManifestNumber())
                .manifestStatus(state.isManifestClosed() ? "CLOSED" : "OPEN")
                .manifestTransmissionId(state.getManifestTransmissionId())
                .manifestTransmittedTime(state.getManifestTransmittedTime())
                .pickupConfirmationNumber(state.getPickupConfirmationNumber())
                .scheduledPickupTime(state.getScheduledPickupTime())
                .warnings(state.getWarnings())
                .labels(getGeneratedLabels().stream()
                        .map(l -> ShippingWorkflowResult.LabelResult.builder()
                                .labelKey(l.getLabelKey())
                                .cbolKey(l.getCbolKey())
                                .trackingNumber(l.getTrackingNumber())
                                .format(l.getFormat())
                                .printed(l.isPrinted())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    private ShippingWorkflowResult buildFailureResult(String message) {
        return ShippingWorkflowResult.builder()
                .success(false)
                .mbolKey(state.getMbolKey())
                .status(ShippingWorkflowStatus.FAILED.name())
                .message(message)
                .errors(state.getErrors())
                .warnings(state.getWarnings())
                .workflowStartTime(state.getWorkflowStartTime())
                .workflowEndTime(LocalDateTime.now())
                .build();
    }

    private ShippingWorkflowResult buildCancellationResult() {
        return ShippingWorkflowResult.builder()
                .success(false)
                .mbolKey(state.getMbolKey())
                .status(ShippingWorkflowStatus.CANCELLED.name())
                .message("Shipment cancelled: " + cancelReason)
                .warnings(state.getWarnings())
                .workflowStartTime(state.getWorkflowStartTime())
                .workflowEndTime(LocalDateTime.now())
                .build();
    }
}
