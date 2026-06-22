package com.maersk.wms.task.service.context;

import com.maersk.wms.task.domain.context_service.service.TaskContextService;
import com.maersk.wms.task.domain.context_service.event.ContextEvents;
import com.maersk.wms.task.domain.lifecycle_service.model.Task;
import com.maersk.wms.task.domain.lifecycle_service.repository.TaskRepository;
import com.maersk.wms.task.shared.kernel.identifiers.*;
import com.maersk.wms.task.shared.kernel.valueobjects.TaskContext;
import com.maersk.wms.task.shared.kernel.exceptions.TaskNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Implementation of Task Context Service.
 * Manages business context (order, inventory, shipment) for task execution.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TaskContextServiceImpl implements TaskContextService {

    private final TaskRepository taskRepository;
    private final ApplicationEventPublisher eventPublisher;

    // In-memory context attribute store (would be persisted in production)
    private final Map<String, Map<String, String>> contextAttributes = new ConcurrentHashMap<>();

    // ==================== Context Management ====================

    @Override
    public void setTaskContext(TaskKey taskKey, TaskContext context) {
        log.info("Setting context for task {}", taskKey.value());

        Task task = getTask(taskKey);
        task.setContext(context);
        taskRepository.save(task);

        publishContextAttachedEvents(taskKey, context);

        log.info("Set context for task {}", taskKey.value());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TaskContext> getTaskContext(TaskKey taskKey) {
        Task task = getTask(taskKey);
        return Optional.ofNullable(task.getContext());
    }

    @Override
    public void updateTaskContext(TaskKey taskKey, TaskContext context) {
        log.info("Updating context for task {}", taskKey.value());

        Task task = getTask(taskKey);
        TaskContext previousContext = task.getContext();

        task.setContext(context);
        taskRepository.save(task);

        eventPublisher.publishEvent(new ContextEvents.TaskContextUpdated(
                taskKey, "FULL_CONTEXT",
                previousContext != null ? contextToMap(previousContext) : Map.of(),
                contextToMap(context),
                Instant.now(), "SYSTEM"
        ));

        log.info("Updated context for task {}", taskKey.value());
    }

    @Override
    public void clearTaskContext(TaskKey taskKey) {
        log.info("Clearing context for task {}", taskKey.value());

        Task task = getTask(taskKey);
        task.setContext(null);
        taskRepository.save(task);

        contextAttributes.remove(taskKey.value());

        eventPublisher.publishEvent(new ContextEvents.TaskContextDetached(
                taskKey, "ALL", "Cleared", Instant.now(), "SYSTEM"
        ));

        log.info("Cleared context for task {}", taskKey.value());
    }

    // ==================== Order Context ====================

    @Override
    public TaskContext createOrderContext(OrderKey orderKey, WaveKey waveKey,
                                           String customerId, String priority) {
        log.info("Creating order context for order {}", orderKey.value());

        return new TaskContext(
                "ORDER",
                orderKey.value(),
                waveKey != null ? waveKey.value() : null,
                customerId,
                priority,
                null, null, null, null,
                Map.of("orderKey", orderKey.value())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskKey> getTasksForOrder(OrderKey orderKey) {
        return taskRepository.findBySourceTypeAndSourceKey("ORDER", orderKey.value())
                .stream()
                .map(Task::getTaskKey)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskKey> getTasksForWave(WaveKey waveKey) {
        return taskRepository.findAll().stream()
                .filter(t -> t.getContext() != null &&
                            waveKey.value().equals(t.getContext().waveKey()))
                .map(Task::getTaskKey)
                .collect(Collectors.toList());
    }

    // ==================== Inventory Context ====================

    @Override
    public TaskContext createInventoryContext(LpnKey lpn, LocationKey from,
                                               LocationKey to, SkuKey sku) {
        log.info("Creating inventory context for LPN {}", lpn.value());

        return new TaskContext(
                "INVENTORY",
                lpn.value(),
                null, null, null,
                lpn.value(),
                from != null ? from.value() : null,
                to != null ? to.value() : null,
                sku != null ? sku.value() : null,
                Map.of("lpnKey", lpn.value())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskKey> getTasksForLpn(LpnKey lpn) {
        return taskRepository.findByLpn(lpn)
                .stream()
                .map(Task::getTaskKey)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskKey> getTasksForLocation(LocationKey location) {
        List<Task> fromTasks = taskRepository.findByFromLocation(location);
        List<Task> toTasks = taskRepository.findByToLocation(location);

        Set<TaskKey> allTasks = new HashSet<>();
        fromTasks.forEach(t -> allTasks.add(t.getTaskKey()));
        toTasks.forEach(t -> allTasks.add(t.getTaskKey()));

        return new ArrayList<>(allTasks);
    }

    // ==================== Shipment Context ====================

    @Override
    public TaskContext createShipmentContext(String shipmentKey, String carrierId,
                                              String trackingNumber) {
        log.info("Creating shipment context for shipment {}", shipmentKey);

        return new TaskContext(
                "SHIPMENT",
                shipmentKey,
                null, null, null,
                null, null, null, null,
                Map.of(
                        "shipmentKey", shipmentKey,
                        "carrierId", carrierId != null ? carrierId : "",
                        "trackingNumber", trackingNumber != null ? trackingNumber : ""
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskKey> getTasksForShipment(String shipmentKey) {
        return taskRepository.findBySourceTypeAndSourceKey("SHIPMENT", shipmentKey)
                .stream()
                .map(Task::getTaskKey)
                .collect(Collectors.toList());
    }

    // ==================== ASN Context ====================

    @Override
    public TaskContext createAsnContext(String asnKey, LpnKey lpn, String poNumber) {
        log.info("Creating ASN context for ASN {}", asnKey);

        return new TaskContext(
                "ASN",
                asnKey,
                null, null, null,
                lpn != null ? lpn.value() : null,
                null, null, null,
                Map.of(
                        "asnKey", asnKey,
                        "poNumber", poNumber != null ? poNumber : ""
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskKey> getTasksForAsn(String asnKey) {
        return taskRepository.findBySourceTypeAndSourceKey("ASN", asnKey)
                .stream()
                .map(Task::getTaskKey)
                .collect(Collectors.toList());
    }

    // ==================== User Context ====================

    @Override
    public void setUserContext(TaskKey taskKey, UserKey userId, DeviceKey deviceId) {
        log.info("Setting user context for task {}: user={}, device={}",
                taskKey.value(), userId.value(), deviceId != null ? deviceId.value() : "none");

        Task task = getTask(taskKey);
        task.setAssignedUser(userId);
        task.setAssignedDevice(deviceId);
        taskRepository.save(task);

        if (deviceId != null) {
            eventPublisher.publishEvent(new ContextEvents.EquipmentContextAttached(
                    taskKey, deviceId, "DEVICE", "ACTIVE", Instant.now()
            ));
        }

        log.info("Set user context for task {}", taskKey.value());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserKey> getTaskUser(TaskKey taskKey) {
        Task task = getTask(taskKey);
        return Optional.ofNullable(task.getAssignedUser());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DeviceKey> getTaskDevice(TaskKey taskKey) {
        Task task = getTask(taskKey);
        return Optional.ofNullable(task.getAssignedDevice());
    }

    // ==================== Context Queries ====================

    @Override
    @Transactional(readOnly = true)
    public List<TaskKey> getTasksBySourceType(String sourceType) {
        return taskRepository.findBySourceType(sourceType)
                .stream()
                .map(Task::getTaskKey)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskKey> getTasksBySourceKey(String sourceType, String sourceKey) {
        return taskRepository.findBySourceTypeAndSourceKey(sourceType, sourceKey)
                .stream()
                .map(Task::getTaskKey)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskKey> getTasksByCustomer(String customerId) {
        return taskRepository.findAll().stream()
                .filter(t -> t.getContext() != null &&
                            customerId.equals(t.getContext().customerId()))
                .map(Task::getTaskKey)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskKey> getTasksByCarrier(String carrierId) {
        return taskRepository.findAll().stream()
                .filter(t -> t.getContext() != null &&
                            t.getContext().attributes() != null &&
                            carrierId.equals(t.getContext().attributes().get("carrierId")))
                .map(Task::getTaskKey)
                .collect(Collectors.toList());
    }

    // ==================== Context Attributes ====================

    @Override
    public void setContextAttribute(TaskKey taskKey, String key, String value) {
        log.debug("Setting context attribute for task {}: {}={}", taskKey.value(), key, value);

        contextAttributes.computeIfAbsent(taskKey.value(), k -> new ConcurrentHashMap<>())
                .put(key, value);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<String> getContextAttribute(TaskKey taskKey, String key) {
        Map<String, String> attrs = contextAttributes.get(taskKey.value());
        if (attrs == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(attrs.get(key));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, String> getAllContextAttributes(TaskKey taskKey) {
        return contextAttributes.getOrDefault(taskKey.value(), Map.of());
    }

    // ==================== Context Validation ====================

    @Override
    @Transactional(readOnly = true)
    public boolean validateContext(TaskKey taskKey) {
        List<String> errors = getContextValidationErrors(taskKey);

        if (errors.isEmpty()) {
            eventPublisher.publishEvent(new ContextEvents.ContextValidationPassed(
                    taskKey, "FULL_VALIDATION", Map.of(), Instant.now()
            ));
            return true;
        } else {
            eventPublisher.publishEvent(new ContextEvents.ContextValidationFailed(
                    taskKey, "FULL_VALIDATION", String.join(", ", errors),
                    Map.of(), Instant.now()
            ));
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getContextValidationErrors(TaskKey taskKey) {
        List<String> errors = new ArrayList<>();

        Task task = getTask(taskKey);
        TaskContext context = task.getContext();

        if (context == null) {
            errors.add("Task has no context");
            return errors;
        }

        if (context.sourceType() == null || context.sourceType().isBlank()) {
            errors.add("Source type is required");
        }

        if (context.sourceKey() == null || context.sourceKey().isBlank()) {
            errors.add("Source key is required");
        }

        // Add additional validation rules based on source type
        switch (context.sourceType()) {
            case "ORDER" -> {
                // Order-specific validations
            }
            case "INVENTORY" -> {
                if (context.lpnKey() == null) {
                    errors.add("LPN is required for inventory context");
                }
            }
            case "SHIPMENT" -> {
                // Shipment-specific validations
            }
        }

        return errors;
    }

    // ==================== Private Helpers ====================

    private Task getTask(TaskKey taskKey) {
        return taskRepository.findByTaskKey(taskKey)
                .orElseThrow(() -> new TaskNotFoundException(taskKey.value()));
    }

    private void publishContextAttachedEvents(TaskKey taskKey, TaskContext context) {
        if (context.sourceType() != null) {
            switch (context.sourceType()) {
                case "ORDER" -> {
                    if (context.sourceKey() != null) {
                        eventPublisher.publishEvent(new ContextEvents.OrderContextAttached(
                                taskKey, new OrderKey(context.sourceKey()),
                                "ORDER", context.customerId(),
                                context.attributes(), Instant.now()
                        ));
                    }
                }
                case "INVENTORY" -> {
                    if (context.lpnKey() != null) {
                        eventPublisher.publishEvent(new ContextEvents.InventoryContextAttached(
                                taskKey, new LpnKey(context.lpnKey()),
                                context.skuKey() != null ? new SkuKey(context.skuKey()) : null,
                                context.fromLocation() != null ? new LocationKey(context.fromLocation()) : null,
                                0, null, Instant.now()
                        ));
                    }
                }
                case "SHIPMENT" -> {
                    Map<String, Object> details = new HashMap<>();
                    if (context.attributes() != null) {
                        details.putAll(context.attributes());
                    }
                    eventPublisher.publishEvent(new ContextEvents.ShipmentContextAttached(
                            taskKey, context.sourceKey(),
                            context.attributes() != null ? (String) context.attributes().get("carrierId") : null,
                            null, null, details, Instant.now()
                    ));
                }
            }
        }

        if (context.fromLocation() != null || context.toLocation() != null) {
            eventPublisher.publishEvent(new ContextEvents.LocationContextAttached(
                    taskKey,
                    context.fromLocation() != null ? new LocationKey(context.fromLocation()) : null,
                    context.toLocation() != null ? new LocationKey(context.toLocation()) : null,
                    null, null, Instant.now()
            ));
        }
    }

    private Map<String, Object> contextToMap(TaskContext context) {
        Map<String, Object> map = new HashMap<>();
        map.put("sourceType", context.sourceType());
        map.put("sourceKey", context.sourceKey());
        map.put("waveKey", context.waveKey());
        map.put("customerId", context.customerId());
        map.put("priority", context.priority());
        map.put("lpnKey", context.lpnKey());
        map.put("fromLocation", context.fromLocation());
        map.put("toLocation", context.toLocation());
        map.put("skuKey", context.skuKey());
        if (context.attributes() != null) {
            map.putAll(context.attributes());
        }
        return map;
    }
}
