package com.maersk.wms.task.shared.kernel.events;

/**
 * Enumeration of bounded contexts within Task Management Service.
 */
public enum TaskBoundedContext {
    LIFECYCLE("task-lifecycle"),
    GROUPING("task-grouping"),
    ORCHESTRATION("task-orchestration"),
    CONTEXT("task-context"),
    PRIORITIZATION("task-prioritization");

    private final String topicPrefix;

    TaskBoundedContext(String topicPrefix) {
        this.topicPrefix = topicPrefix;
    }

    public String getTopicPrefix() {
        return topicPrefix;
    }

    public String getEventTopic(String eventType) {
        return topicPrefix + "." + eventType;
    }
}
