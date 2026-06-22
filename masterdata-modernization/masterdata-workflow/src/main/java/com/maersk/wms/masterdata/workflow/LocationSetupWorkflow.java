package com.maersk.wms.masterdata.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Temporal workflow interface for bulk location setup.
 */
@WorkflowInterface
public interface LocationSetupWorkflow {

    @WorkflowMethod
    LocationSetupResult setupLocations(LocationSetupRequest request);
}
