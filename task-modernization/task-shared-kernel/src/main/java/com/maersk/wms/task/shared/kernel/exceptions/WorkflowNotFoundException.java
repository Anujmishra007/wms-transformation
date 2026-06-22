package com.maersk.wms.task.shared.kernel.exceptions;

/**
 * Exception thrown when a task workflow is not found.
 */
public class WorkflowNotFoundException extends TaskManagementException {

    public WorkflowNotFoundException(String workflowKey) {
        super("WORKFLOW_NOT_FOUND", "Task workflow not found: " + workflowKey);
    }
}
