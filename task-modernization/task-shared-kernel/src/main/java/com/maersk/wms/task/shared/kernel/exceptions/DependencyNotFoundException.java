package com.maersk.wms.task.shared.kernel.exceptions;

/**
 * Exception thrown when a task dependency is not found.
 */
public class DependencyNotFoundException extends TaskManagementException {

    public DependencyNotFoundException(String dependencyKey) {
        super("DEPENDENCY_NOT_FOUND", "Task dependency not found: " + dependencyKey);
    }
}
