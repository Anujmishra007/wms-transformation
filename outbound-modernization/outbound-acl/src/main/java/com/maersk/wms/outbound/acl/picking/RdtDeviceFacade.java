package com.maersk.wms.outbound.acl.picking;

import com.maersk.wms.outbound.shared.kernel.valueobjects.PickInstruction;

import java.util.List;

/**
 * Facade for RDT Device integration.
 * Anti-Corruption Layer for warehouse execution device communication.
 */
public interface RdtDeviceFacade {

    /**
     * Sends pick instructions to a device.
     */
    void sendPickInstructions(String deviceId, List<PickInstruction> instructions);

    /**
     * Gets the current status of a device.
     */
    DeviceStatus getDeviceStatus(String deviceId);

    /**
     * Registers a device session for a user.
     */
    String startSession(String userId, String deviceId, String zone);

    /**
     * Ends a device session.
     */
    void endSession(String sessionId);

    /**
     * Gets all active sessions.
     */
    List<DeviceSession> getActiveSessions(String zone);

    /**
     * Device status.
     */
    record DeviceStatus(
            String deviceId,
            String status,
            String currentUser,
            String currentZone,
            String lastActivity,
            java.time.LocalDateTime lastHeartbeat
    ) {}

    /**
     * Active device session.
     */
    record DeviceSession(
            String sessionId,
            String deviceId,
            String userId,
            String zone,
            java.time.LocalDateTime startTime,
            int picksCompleted
    ) {}
}
