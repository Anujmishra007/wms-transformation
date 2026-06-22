package com.maersk.wms.outbound.workflow.shipping;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.QueryMethod;

import java.util.List;

/**
 * Temporal workflow for shipping operations.
 * Orchestrates the complete shipping process from MBOL creation through
 * carrier label generation, manifest close, and ship confirmation.
 *
 * Legacy SP References:
 * - WM.lsp_WaveGenMBOL - Generate MBOL from wave
 * - nsp_BackEndShipped - Ship confirmation
 * - isp_PrintCarrierLabel - Label generation
 * - nsp_ShippingManifestDetails - Manifest operations
 * - nsp_GetLoadManifest - Load manifest
 */
@WorkflowInterface
public interface ShippingWorkflow {

    /**
     * Execute the shipping workflow.
     * Orchestrates: MBOL Creation → Carrier Selection → Labels → Manifest → Ship
     *
     * @param request The shipping request containing wave/load details
     * @return The workflow result containing shipping information
     */
    @WorkflowMethod
    ShippingWorkflowResult execute(ShippingWorkflowRequest request);

    // ========== MBOL Signals ==========

    /**
     * Signal to generate MBOL from wave.
     */
    @SignalMethod
    void generateMbolFromWave(String waveKey);

    /**
     * Signal to generate MBOL from load plan.
     */
    @SignalMethod
    void generateMbolFromLoadPlan(String loadKey);

    /**
     * Signal to add order to MBOL.
     */
    @SignalMethod
    void addOrderToMbol(String orderKey);

    /**
     * Signal to remove order from MBOL.
     */
    @SignalMethod
    void removeOrderFromMbol(String orderKey);

    /**
     * Signal to split MBOL.
     */
    @SignalMethod
    void splitMbol(List<String> orderKeysForNewMbol);

    // ========== Carrier Signals ==========

    /**
     * Signal to select carrier (rate shop).
     */
    @SignalMethod
    void selectCarrier();

    /**
     * Signal to change carrier.
     */
    @SignalMethod
    void changeCarrier(ChangeCarrierSignal signal);

    /**
     * Signal to calculate freight rates.
     */
    @SignalMethod
    void calculateFreight();

    // ========== Label Signals ==========

    /**
     * Signal to generate labels for all packages.
     */
    @SignalMethod
    void generateLabels();

    /**
     * Signal to generate label for specific package.
     */
    @SignalMethod
    void generateLabel(GenerateLabelSignal signal);

    /**
     * Signal to reprint label.
     */
    @SignalMethod
    void reprintLabel(String labelKey);

    /**
     * Signal to void label.
     */
    @SignalMethod
    void voidLabel(VoidLabelSignal signal);

    // ========== Manifest Signals ==========

    /**
     * Signal to add MBOL to manifest.
     */
    @SignalMethod
    void addToManifest(String manifestKey);

    /**
     * Signal to remove from manifest.
     */
    @SignalMethod
    void removeFromManifest();

    /**
     * Signal to close manifest (end of day).
     */
    @SignalMethod
    void closeManifest();

    /**
     * Signal to transmit manifest to carrier.
     */
    @SignalMethod
    void transmitManifest();

    // ========== Ship Signals ==========

    /**
     * Signal to confirm shipment.
     */
    @SignalMethod
    void confirmShipment(ShipConfirmSignal signal);

    /**
     * Signal to schedule pickup.
     */
    @SignalMethod
    void schedulePickup(SchedulePickupSignal signal);

    /**
     * Signal to cancel pickup.
     */
    @SignalMethod
    void cancelPickup();

    /**
     * Signal to cancel shipment.
     */
    @SignalMethod
    void cancelShipment(String reason);

    // ========== Query Methods ==========

    /**
     * Query current workflow status.
     */
    @QueryMethod
    ShippingWorkflowStatus getStatus();

    /**
     * Query workflow state.
     */
    @QueryMethod
    ShippingWorkflowState getState();

    /**
     * Query MBOL details.
     */
    @QueryMethod
    MbolDetails getMbolDetails();

    /**
     * Query generated labels.
     */
    @QueryMethod
    List<LabelInfo> getGeneratedLabels();

    /**
     * Query manifest status.
     */
    @QueryMethod
    ManifestInfo getManifestInfo();

    /**
     * Query shipping progress percentage.
     */
    @QueryMethod
    int getProgressPercent();

    /**
     * Query if ready to ship.
     */
    @QueryMethod
    boolean isReadyToShip();
}
