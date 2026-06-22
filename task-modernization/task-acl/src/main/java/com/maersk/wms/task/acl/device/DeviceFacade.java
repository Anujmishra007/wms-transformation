package com.maersk.wms.task.acl.device;

import com.maersk.wms.task.shared.kernel.identifiers.*;

import java.util.List;
import java.util.Optional;

/**
 * Facade interface for Device/RDT Service integration.
 * Used by Task Management for device-related task operations.
 */
public interface DeviceFacade {

    /**
     * Get device details.
     */
    Optional<DeviceDetails> getDeviceDetails(DeviceKey deviceId);

    /**
     * Get devices in zone.
     */
    List<DeviceDetails> getDevicesInZone(ZoneKey zone);

    /**
     * Get current user logged into device.
     */
    Optional<UserKey> getCurrentDeviceUser(DeviceKey deviceId);

    /**
     * Check if device is online.
     */
    boolean isDeviceOnline(DeviceKey deviceId);

    /**
     * Send task to device.
     */
    void sendTaskToDevice(DeviceKey deviceId, String taskKey, String taskType, String instructions);

    /**
     * Get device location.
     */
    Optional<LocationKey> getDeviceLocation(DeviceKey deviceId);

    /**
     * Record for device details.
     */
    record DeviceDetails(
            DeviceKey deviceId,
            String deviceType,
            ZoneKey zone,
            UserKey currentUser,
            boolean online,
            String status
    ) {}
}
