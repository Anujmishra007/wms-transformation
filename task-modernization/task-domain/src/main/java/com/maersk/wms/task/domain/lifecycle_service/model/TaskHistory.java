package com.maersk.wms.task.domain.lifecycle_service.model;

import com.maersk.wms.task.shared.kernel.identifiers.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * TaskHistory entity - records task lifecycle events.
 */
@Data
@Builder
public class TaskHistory {

    private HistoryKey historyKey;
    private TaskKey taskKey;
    private HistoryAction action;
    private TaskStatus previousStatus;
    private TaskStatus newStatus;
    private UserKey performedBy;
    private DeviceKey device;
    private String notes;
    private String additionalData;
    private LocalDateTime timestamp;

    public enum HistoryAction {
        CREATED, RELEASED, ASSIGNED, UNASSIGNED, STARTED, SUSPENDED, RESUMED,
        COMPLETED, CANCELLED, CLOSED, PRIORITY_CHANGED, REASSIGNED, UPDATED
    }

    public static TaskHistory recordAction(TaskKey taskKey, HistoryAction action,
                                            TaskStatus previousStatus, TaskStatus newStatus,
                                            UserKey user, String notes) {
        return TaskHistory.builder()
                .historyKey(new HistoryKey(java.util.UUID.randomUUID().toString()))
                .taskKey(taskKey)
                .action(action)
                .previousStatus(previousStatus)
                .newStatus(newStatus)
                .performedBy(user)
                .notes(notes)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
