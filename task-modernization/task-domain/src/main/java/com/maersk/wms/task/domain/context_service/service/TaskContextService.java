package com.maersk.wms.task.domain.context_service.service;

import com.maersk.wms.task.shared.kernel.identifiers.*;
import com.maersk.wms.task.shared.kernel.valueobjects.TaskContext;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Task Context Service - manages business context for task execution.
 */
public interface TaskContextService {

    // Context Management
    void setTaskContext(TaskKey taskKey, TaskContext context);
    Optional<TaskContext> getTaskContext(TaskKey taskKey);
    void updateTaskContext(TaskKey taskKey, TaskContext context);
    void clearTaskContext(TaskKey taskKey);

    // Order Context
    TaskContext createOrderContext(OrderKey orderKey, WaveKey waveKey, String customerId, String priority);
    List<TaskKey> getTasksForOrder(OrderKey orderKey);
    List<TaskKey> getTasksForWave(WaveKey waveKey);

    // Inventory Context
    TaskContext createInventoryContext(LpnKey lpn, LocationKey from, LocationKey to, SkuKey sku);
    List<TaskKey> getTasksForLpn(LpnKey lpn);
    List<TaskKey> getTasksForLocation(LocationKey location);

    // Shipment Context
    TaskContext createShipmentContext(String shipmentKey, String carrierId, String trackingNumber);
    List<TaskKey> getTasksForShipment(String shipmentKey);

    // ASN Context
    TaskContext createAsnContext(String asnKey, LpnKey lpn, String poNumber);
    List<TaskKey> getTasksForAsn(String asnKey);

    // User Context
    void setUserContext(TaskKey taskKey, UserKey userId, DeviceKey deviceId);
    Optional<UserKey> getTaskUser(TaskKey taskKey);
    Optional<DeviceKey> getTaskDevice(TaskKey taskKey);

    // Context Queries
    List<TaskKey> getTasksBySourceType(String sourceType);
    List<TaskKey> getTasksBySourceKey(String sourceType, String sourceKey);
    List<TaskKey> getTasksByCustomer(String customerId);
    List<TaskKey> getTasksByCarrier(String carrierId);

    // Context Attributes
    void setContextAttribute(TaskKey taskKey, String key, String value);
    Optional<String> getContextAttribute(TaskKey taskKey, String key);
    Map<String, String> getAllContextAttributes(TaskKey taskKey);

    // Context Validation
    boolean validateContext(TaskKey taskKey);
    List<String> getContextValidationErrors(TaskKey taskKey);
}
