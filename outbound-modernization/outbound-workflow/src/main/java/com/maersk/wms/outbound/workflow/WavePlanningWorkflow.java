package com.maersk.wms.outbound.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Temporal workflow interface for wave planning.
 * Orchestrates the wave creation and release process.
 */
@WorkflowInterface
public interface WavePlanningWorkflow {

    /**
     * Execute the wave planning workflow.
     */
    @WorkflowMethod
    WavePlanningResult planAndReleaseWave(WavePlanningRequest request);
}
