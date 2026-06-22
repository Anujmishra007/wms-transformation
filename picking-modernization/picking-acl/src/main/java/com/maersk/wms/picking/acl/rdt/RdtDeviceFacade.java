package com.maersk.wms.picking.acl.rdt;

import com.maersk.wms.picking.shared.kernel.identifiers.*;

import java.util.List;
import java.util.Optional;

/**
 * Anti-Corruption Layer facade for RDT Device integration.
 * Handles communication with RDT devices and session management.
 */
public interface RdtDeviceFacade {

    // Session Management
    String startDeviceSession(DeviceKey deviceId, UserKey userId, String functionCode);
    void endDeviceSession(String sessionToken);
    boolean validateSessionToken(String sessionToken);
    Optional<SessionInfo> getSessionInfo(String sessionToken);

    // Device Communication
    void sendMessage(DeviceKey deviceId, String message, MessageType type);
    void sendTaskInstruction(DeviceKey deviceId, TaskInstruction instruction);
    void sendConfirmationRequest(DeviceKey deviceId, ConfirmationRequest request);
    void sendAlert(DeviceKey deviceId, String alertMessage, AlertLevel level);

    // Screen Updates
    void refreshScreen(DeviceKey deviceId);
    void displayPickTask(DeviceKey deviceId, PickTaskDisplay task);
    void displayShortConfirmation(DeviceKey deviceId, ShortConfirmation confirmation);
    void displayCompletionSummary(DeviceKey deviceId, CompletionSummary summary);

    // Input Validation
    boolean validateBarcodeScan(String barcode, BarcodeType expectedType);
    BarcodeDecodeResult decodeBarcode(String barcode);

    // Device Status
    Optional<DeviceStatus> getDeviceStatus(DeviceKey deviceId);
    List<DeviceKey> getActiveDevices(String zone);
    boolean isDeviceAvailable(DeviceKey deviceId);

    // Enums and DTOs
    enum MessageType { INFO, WARNING, ERROR, SUCCESS }
    enum AlertLevel { LOW, MEDIUM, HIGH, CRITICAL }
    enum BarcodeType { LOCATION, LPN, SKU, LOT, SERIAL, UNKNOWN }

    record SessionInfo(
            String sessionToken,
            DeviceKey deviceId,
            UserKey userId,
            String functionCode,
            String zone,
            java.time.LocalDateTime startTime
    ) {}

    record TaskInstruction(
            String taskId,
            String location,
            String lpn,
            String sku,
            String skuDescription,
            java.math.BigDecimal quantity,
            String uom,
            String instructions,
            int sequence
    ) {}

    record ConfirmationRequest(
            String confirmationType,
            String message,
            List<String> options,
            boolean requiresScan
    ) {}

    record PickTaskDisplay(
            String taskId,
            String fromLocation,
            String fromLpn,
            String sku,
            String skuDescription,
            java.math.BigDecimal qtyToPick,
            String toLocation,
            String toLpn,
            int sequence,
            int totalTasks
    ) {}

    record ShortConfirmation(
            String taskId,
            String sku,
            java.math.BigDecimal requestedQty,
            java.math.BigDecimal availableQty,
            List<String> reasonCodes
    ) {}

    record CompletionSummary(
            int totalTasks,
            int completedTasks,
            int shortedTasks,
            int skippedTasks,
            java.time.Duration elapsedTime
    ) {}

    record BarcodeDecodeResult(
            boolean valid,
            BarcodeType type,
            String value,
            String errorMessage
    ) {}

    record DeviceStatus(
            DeviceKey deviceId,
            boolean online,
            String currentFunction,
            UserKey currentUser,
            String zone,
            java.time.LocalDateTime lastActivity
    ) {}
}
