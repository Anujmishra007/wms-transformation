package com.maersk.wms.masterdata.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Temporal workflow interface for bulk item import.
 */
@WorkflowInterface
public interface ItemImportWorkflow {

    @WorkflowMethod
    ItemImportResult importItems(ItemImportRequest request);
}
