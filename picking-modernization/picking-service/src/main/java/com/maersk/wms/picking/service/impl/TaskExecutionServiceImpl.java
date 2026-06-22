package com.maersk.wms.picking.service.impl;

import com.maersk.wms.picking.domain.task_execution_service.model.PickSession;
import com.maersk.wms.picking.domain.task_execution_service.model.PickTask;
import com.maersk.wms.picking.domain.task_execution_service.model.PickTaskStatus;
import com.maersk.wms.picking.domain.task_execution_service.model.SessionStatus;
import com.maersk.wms.picking.domain.task_execution_service.repository.PickSessionRepository;
import com.maersk.wms.picking.domain.task_execution_service.repository.PickTaskRepository;
import com.maersk.wms.picking.domain.task_execution_service.service.TaskExecutionService;
import com.maersk.wms.picking.shared.kernel.exceptions.InvalidTaskStateException;
import com.maersk.wms.picking.shared.kernel.exceptions.TaskNotFoundException;
import com.maersk.wms.picking.shared.kernel.identifiers.*;
import com.maersk.wms.picking.shared.kernel.valueobjects.PickConfirmation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of Task Execution Service.
 * Manages pick task lifecycle and RDT session handling.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskExecutionServiceImpl implements TaskExecutionService {

    private final PickTaskRepository taskRepository;
    private final PickSessionRepository sessionRepository;

    // Session Management

    @Override
    @Transactional
    public PickSession startSession(UserKey userId, DeviceKey deviceId, String zone) {
        log.info("Starting pick session for user {} on device {} in zone {}", userId, deviceId, zone);

        // Check for existing active session
        sessionRepository.findActiveByUser(userId).ifPresent(session -> {
            throw new IllegalStateException("User already has active session: " + session.getSessionId());
        });

        PickSession session = PickSession.builder()
                .sessionId(UUID.randomUUID().toString())
                .userId(userId)
                .deviceId(deviceId)
                .zone(zone)
                .status(SessionStatus.ACTIVE)
                .startTime(LocalDateTime.now())
                .tasksCompleted(0)
                .tasksShorted(0)
                .build();

        return sessionRepository.save(session);
    }

    @Override
    public PickSession getSession(String sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));
    }

    @Override
    @Transactional
    public void endSession(String sessionId) {
        log.info("Ending session {}", sessionId);
        PickSession session = getSession(sessionId);
        session.end();
        sessionRepository.save(session);
    }

    @Override
    @Transactional
    public void pauseSession(String sessionId, String reason) {
        log.info("Pausing session {} - reason: {}", sessionId, reason);
        PickSession session = getSession(sessionId);
        session.pause(reason);
        sessionRepository.save(session);
    }

    @Override
    @Transactional
    public void resumeSession(String sessionId) {
        log.info("Resuming session {}", sessionId);
        PickSession session = getSession(sessionId);
        session.resume();
        sessionRepository.save(session);
    }

    // Task Assignment

    @Override
    public PickTask getNextTask(UserKey userId, String zone, String equipmentType) {
        log.debug("Getting next task for user {} in zone {} with equipment {}", userId, zone, equipmentType);
        return taskRepository.findNextAvailableTask(zone, equipmentType)
                .orElse(null);
    }

    @Override
    public PickTask getTaskById(PickTaskKey taskKey) {
        return taskRepository.findById(taskKey)
                .orElseThrow(() -> new TaskNotFoundException(taskKey.toString()));
    }

    @Override
    public List<PickTask> getTasksForUser(UserKey userId) {
        return taskRepository.findByUser(userId);
    }

    @Override
    public List<PickTask> getTasksForList(PickListKey listKey) {
        return taskRepository.findByPickList(listKey);
    }

    @Override
    public List<PickTask> getTasksByStatus(PickTaskStatus status, String zone) {
        return taskRepository.findByZoneAndStatus(zone, status);
    }

    // Task Execution

    @Override
    @Transactional
    public void assignTask(PickTaskKey taskKey, UserKey userId, DeviceKey deviceId) {
        log.info("Assigning task {} to user {} on device {}", taskKey, userId, deviceId);
        PickTask task = getTaskById(taskKey);

        if (!task.canStart()) {
            throw new InvalidTaskStateException("Task cannot be assigned in current state: " + task.getStatus());
        }

        task.setAssignedUser(userId);
        task.setAssignedDevice(deviceId);
        task.setStatus(PickTaskStatus.ASSIGNED);
        task.setAssignedTime(LocalDateTime.now());

        taskRepository.save(task);
    }

    @Override
    @Transactional
    public void unassignTask(PickTaskKey taskKey, String reason) {
        log.info("Unassigning task {} - reason: {}", taskKey, reason);
        PickTask task = getTaskById(taskKey);

        task.setAssignedUser(null);
        task.setAssignedDevice(null);
        task.setStatus(PickTaskStatus.RELEASED);
        task.setAssignedTime(null);

        taskRepository.save(task);
    }

    @Override
    @Transactional
    public void startTask(PickTaskKey taskKey) {
        log.info("Starting task {}", taskKey);
        PickTask task = getTaskById(taskKey);

        if (!task.canStart()) {
            throw new InvalidTaskStateException("Task cannot be started in current state: " + task.getStatus());
        }

        task.start();
        taskRepository.save(task);
    }

    @Override
    @Transactional
    public PickTask confirmPick(PickTaskKey taskKey, PickConfirmation confirmation) {
        log.info("Confirming pick for task {} - qty: {}", taskKey, confirmation.pickedQuantity());
        PickTask task = getTaskById(taskKey);

        if (!task.canConfirm()) {
            throw new InvalidTaskStateException("Task cannot be confirmed in current state: " + task.getStatus());
        }

        task.recordPick(confirmation.pickedQuantity());
        return taskRepository.save(task);
    }

    @Override
    @Transactional
    public void skipTask(PickTaskKey taskKey, String reason) {
        log.info("Skipping task {} - reason: {}", taskKey, reason);
        PickTask task = getTaskById(taskKey);

        task.setStatus(PickTaskStatus.SKIPPED);
        taskRepository.save(task);
    }

    @Override
    @Transactional
    public void completeTask(PickTaskKey taskKey) {
        log.info("Completing task {}", taskKey);
        PickTask task = getTaskById(taskKey);

        if (task.isComplete()) {
            task.setStatus(PickTaskStatus.COMPLETED);
            task.setCompleteTime(LocalDateTime.now());
            taskRepository.save(task);
        }
    }

    // Validation

    @Override
    public boolean validateLocation(PickTaskKey taskKey, LocationKey location) {
        PickTask task = getTaskById(taskKey);
        return task.getFromLocation() != null &&
               task.getFromLocation().equals(location);
    }

    @Override
    public boolean validateLpn(PickTaskKey taskKey, LpnKey lpn) {
        PickTask task = getTaskById(taskKey);
        return task.getFromLpn() == null ||
               task.getFromLpn().equals(lpn);
    }

    @Override
    public boolean validateSku(PickTaskKey taskKey, SkuKey sku) {
        PickTask task = getTaskById(taskKey);
        return task.getSku() != null &&
               task.getSku().equals(sku);
    }

    @Override
    public boolean validateQuantity(PickTaskKey taskKey, BigDecimal quantity) {
        PickTask task = getTaskById(taskKey);
        return quantity != null &&
               quantity.compareTo(BigDecimal.ZERO) > 0 &&
               quantity.compareTo(task.getQtyToPick()) <= 0;
    }

    // Query

    @Override
    public Optional<PickTask> findTaskByPickDetail(PickDetailKey pickDetailKey) {
        return taskRepository.findByPickDetail(pickDetailKey).stream().findFirst();
    }

    @Override
    public List<PickTask> findTasksByOrder(OrderKey orderKey) {
        return taskRepository.findByOrder(orderKey);
    }

    @Override
    public List<PickTask> findTasksByWave(WaveKey waveKey) {
        return taskRepository.findByWave(waveKey);
    }

    @Override
    public int countOpenTasks(String zone) {
        return taskRepository.countByZoneAndStatus(zone, PickTaskStatus.RELEASED);
    }

    @Override
    public int countAssignedTasks(UserKey userId) {
        return taskRepository.countByUser(userId);
    }
}
