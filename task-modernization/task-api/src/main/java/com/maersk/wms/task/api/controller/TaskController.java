package com.maersk.wms.task.api.controller;

import com.maersk.wms.task.api.dto.*;
import com.maersk.wms.task.domain.entity.Task;
import com.maersk.wms.task.domain.enums.TaskStatus;
import com.maersk.wms.task.plugin.TaskPluginContext;
import com.maersk.wms.task.service.TaskService;
import com.maersk.wms.task.service.TaskAssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for task management operations.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Task Management", description = "APIs for managing warehouse tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskAssignmentService assignmentService;

    @PostMapping
    @Operation(summary = "Create a new task")
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody CreateTaskRequest request,
            @RequestHeader("X-Client-Code") String clientCode,
            @RequestHeader("X-Facility-Code") String facilityCode,
            @RequestHeader("X-User-Id") String userId) {

        log.info("Creating task: {} for client: {}", request.getTaskId(), clientCode);

        TaskPluginContext context = buildContext(clientCode, facilityCode, userId);
        Task task = mapToEntity(request);
        Task createdTask = taskService.createTask(task, context);

        return ResponseEntity.ok(mapToResponse(createdTask));
    }

    @GetMapping("/{taskKey}")
    @Operation(summary = "Get task by key")
    public ResponseEntity<TaskResponse> getTask(@PathVariable Long taskKey) {
        Task task = taskService.getTask(taskKey);
        return ResponseEntity.ok(mapToResponse(task));
    }

    @GetMapping("/by-id/{taskId}")
    @Operation(summary = "Get task by task ID")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable String taskId) {
        Task task = taskService.getTaskById(taskId);
        return ResponseEntity.ok(mapToResponse(task));
    }

    @GetMapping
    @Operation(summary = "Get tasks by status")
    public ResponseEntity<List<TaskResponse>> getTasksByStatus(
            @RequestParam(required = false) String status) {

        List<Task> tasks;
        if (status != null) {
            tasks = taskService.getTasksByStatus(TaskStatus.valueOf(status));
        } else {
            tasks = taskService.getUnassignedTasks();
        }

        return ResponseEntity.ok(tasks.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList()));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get tasks assigned to a user")
    public ResponseEntity<List<TaskResponse>> getTasksByUser(@PathVariable String userId) {
        List<Task> tasks = taskService.getTasksByAssignedUser(userId);
        return ResponseEntity.ok(tasks.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList()));
    }

    @GetMapping("/unassigned")
    @Operation(summary = "Get unassigned tasks")
    public ResponseEntity<List<TaskResponse>> getUnassignedTasks(
            @RequestParam(required = false) String workZone) {

        List<Task> tasks;
        if (workZone != null) {
            tasks = taskService.getUnassignedTasksByWorkZone(workZone);
        } else {
            tasks = taskService.getUnassignedTasks();
        }

        return ResponseEntity.ok(tasks.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList()));
    }

    @PostMapping("/{taskKey}/assign")
    @Operation(summary = "Assign task to a user")
    public ResponseEntity<TaskResponse> assignTask(
            @PathVariable Long taskKey,
            @Valid @RequestBody AssignTaskRequest request,
            @RequestHeader("X-Client-Code") String clientCode,
            @RequestHeader("X-Facility-Code") String facilityCode,
            @RequestHeader("X-User-Id") String userId) {

        log.info("Assigning task {} to user {}", taskKey, request.getUserId());

        TaskPluginContext context = buildContext(clientCode, facilityCode, userId);
        taskService.assignTask(taskKey, request.getUserId(), context);

        Task task = taskService.getTask(taskKey);
        return ResponseEntity.ok(mapToResponse(task));
    }

    @PostMapping("/{taskKey}/complete")
    @Operation(summary = "Complete a task")
    public ResponseEntity<TaskResponse> completeTask(
            @PathVariable Long taskKey,
            @Valid @RequestBody CompleteTaskRequest request,
            @RequestHeader("X-Client-Code") String clientCode,
            @RequestHeader("X-Facility-Code") String facilityCode,
            @RequestHeader("X-User-Id") String userId) {

        log.info("Completing task {} with quantity {}", taskKey, request.getCompletedQuantity());

        TaskPluginContext context = buildContext(clientCode, facilityCode, userId);
        taskService.completeTask(taskKey, request.getCompletedQuantity(), context);

        Task task = taskService.getTask(taskKey);
        return ResponseEntity.ok(mapToResponse(task));
    }

    private TaskPluginContext buildContext(String clientCode, String facilityCode, String userId) {
        return TaskPluginContext.builder()
                .clientCode(clientCode)
                .facilityCode(facilityCode)
                .userId(userId)
                .build();
    }

    private Task mapToEntity(CreateTaskRequest request) {
        return Task.builder()
                .taskId(request.getTaskId())
                .taskType(request.getTaskType())
                .priority(request.getPriority())
                .sourceLocation(request.getSourceLocation())
                .sourceZone(request.getSourceZone())
                .destinationLocation(request.getDestinationLocation())
                .destinationZone(request.getDestinationZone())
                .sku(request.getSku())
                .quantity(request.getQuantity())
                .workGroup(request.getWorkGroup())
                .workZone(request.getWorkZone())
                .orderKey(request.getOrderKey())
                .waveId(request.getWaveId())
                .dueDate(request.getDueDate())
                .build();
    }

    private TaskResponse mapToResponse(Task task) {
        return TaskResponse.builder()
                .taskKey(task.getTaskKey())
                .taskId(task.getTaskId())
                .taskType(task.getTaskType())
                .status(task.getStatus())
                .priority(task.getPriority())
                .sourceLocation(task.getSourceLocation())
                .sourceZone(task.getSourceZone())
                .destinationLocation(task.getDestinationLocation())
                .destinationZone(task.getDestinationZone())
                .sku(task.getSku())
                .quantity(task.getQuantity())
                .pickedQuantity(task.getPickedQuantity())
                .shortQuantity(task.getShortQuantity())
                .assignedUserId(task.getAssignedUserId())
                .assignedUserName(task.getAssignedUserName())
                .workGroup(task.getWorkGroup())
                .workZone(task.getWorkZone())
                .createdAt(task.getCreatedAt())
                .assignedAt(task.getAssignedAt())
                .startedAt(task.getStartedAt())
                .completedAt(task.getCompletedAt())
                .build();
    }
}
