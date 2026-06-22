package com.maersk.wms.picking.domain.task_execution_service.service;

import com.maersk.wms.picking.domain.task_execution_service.model.PickSession;
import com.maersk.wms.picking.domain.task_execution_service.model.PickTask;
import com.maersk.wms.picking.domain.task_execution_service.model.PickTaskStatus;
import com.maersk.wms.picking.shared.kernel.identifiers.*;
import com.maersk.wms.picking.shared.kernel.valueobjects.PickConfirmation;

import java.util.List;
import java.util.Optional;

/**
 * Task Execution Service - manages pick task lifecycle and RDT session handling.
 * Core bounded context for executing pick operations.
 */
public interface TaskExecutionService {

    // Session Management
    PickSession startSession(UserKey userId, DeviceKey deviceId, String zone);
    PickSession getSession(String sessionId);
    void endSession(String sessionId);
    void pauseSession(String sessionId, String reason);
    void resumeSession(String sessionId);

    // Task Assignment
    PickTask getNextTask(UserKey userId, String zone, String equipmentType);
    PickTask getTaskById(PickTaskKey taskKey);
    List<PickTask> getTasksForUser(UserKey userId);
    List<PickTask> getTasksForList(PickListKey listKey);
    List<PickTask> getTasksByStatus(PickTaskStatus status, String zone);

    // Task Execution
    void assignTask(PickTaskKey taskKey, UserKey userId, DeviceKey deviceId);
    void unassignTask(PickTaskKey taskKey, String reason);
    void startTask(PickTaskKey taskKey);
    PickTask confirmPick(PickTaskKey taskKey, PickConfirmation confirmation);
    void skipTask(PickTaskKey taskKey, String reason);
    void completeTask(PickTaskKey taskKey);

    // Validation
    boolean validateLocation(PickTaskKey taskKey, LocationKey location);
    boolean validateLpn(PickTaskKey taskKey, LpnKey lpn);
    boolean validateSku(PickTaskKey taskKey, SkuKey sku);
    boolean validateQuantity(PickTaskKey taskKey, java.math.BigDecimal quantity);

    // Query
    Optional<PickTask> findTaskByPickDetail(PickDetailKey pickDetailKey);
    List<PickTask> findTasksByOrder(OrderKey orderKey);
    List<PickTask> findTasksByWave(WaveKey waveKey);
    int countOpenTasks(String zone);
    int countAssignedTasks(UserKey userId);
}
